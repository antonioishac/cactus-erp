package br.com.cactusdigital.erp.sincronizacao.service;

import br.com.cactusdigital.erp.sincronizacao.entity.TipoRegistroSincronizacao;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

/**
 * Validação estrutural de forma (não de negócio) do {@code payload} de cada item do
 * envelope — fecha o gap encontrado na validação ao vivo de 2026-08-31 (ver
 * FASE6_SINCRONIZACAO_PDV.md, seção 11.1): antes desta classe, {@code REJEITADO} do
 * contrato da seção 5.2 era código morto — qualquer JSON sintaticamente válido virava
 * {@code ACEITO}, e um payload com campo obrigatório faltando só falhava depois, de
 * forma assíncrona, no consumidor de domínio (esgotando retry até cair em DLQ/ERRO).
 * <p>
 * Deliberadamente superficial: checa só presença/não-nulidade dos campos que o
 * consumidor de domínio exige pra sequer tentar processar — não valida regra de
 * negócio (produto existir, depósito pertencer à empresa, etc.), isso continua sendo
 * responsabilidade do serviço dono (ver Javadoc de {@code LoteSincronizacaoPushService},
 * "não decide regra de negócio nenhuma"). Um item com erro de forma é rejeitado aqui,
 * sem gerar {@link br.com.cactusdigital.erp.sincronizacao.entity.RegistroSincronizacao}
 * nem consumir a chave de idempotência — o PDV corrige e reenvia com o mesmo
 * {@code idOrigem} sem problema.
 */
@Component
public class RegistroPayloadValidator {

    public Optional<String> validar(TipoRegistroSincronizacao tipoRegistro, JsonNode payload) {
        return switch (tipoRegistro) {
            case ESTOQUE_MOVIMENTO -> validarEstoqueMovimento(payload);
            case SANGRIA, SUPRIMENTO -> exigirTodos(payload, "pdvCaixaId", "valor", "dataMovimento");
            case ABERTURA_CAIXA -> exigirTodos(payload, "pdvCaixaId", "valorFundoInicial", "dataAbertura");
            // Só a divergência da forma DINHEIRO (PDV_CONFERENCIA_CAIXA_DETALHE da forma dinheiro), nunca o
            // VALOR_DIVERGENCIA do cabeçalho, que soma todas as formas (cartão, PIX...) — ver FinanceiroPdvService.
            case CONFERENCIA_CAIXA -> exigirTodos(payload, "pdvCaixaId", "valorDivergenciaDinheiro", "dataConferencia");
            case FIADO_LANCAMENTO -> validarFiado(payload);
            case VENDA_PDV -> validarVenda(payload);
            case DEVOLUCAO_PDV -> exigirTodos(payload, "vendaIdOrigem", "numeroItem", "quantidadeDevolvida",
                    "valorDevolvido", "pdvGerenteAutorizadorId", "dataHoraDevolucao");
            case CANCELAMENTO_VENDA_PDV -> validarCancelamento(payload);
        };
    }

    /** Mesma regra de justificativa do evento de cancelamento da NFC-e (15 a 255 caracteres), exigida mesmo sem documento fiscal. */
    private Optional<String> validarCancelamento(JsonNode payload) {
        Optional<String> ausentes = exigirTodos(payload, "vendaIdOrigem", "pdvGerenteAutorizadorId", "dataHoraCancelamento", "justificativa");
        if (ausentes.isPresent()) {
            return ausentes;
        }
        int tamanho = payload.get("justificativa").asString().trim().length();
        if (tamanho < 15 || tamanho > 255) {
            return Optional.of("justificativa deve ter entre 15 e 255 caracteres (tem " + tamanho + ")");
        }
        return Optional.empty();
    }

    private Optional<String> validarEstoqueMovimento(JsonNode payload) {
        List<String> ausentes = new ArrayList<>();
        exigir(payload, "produtoId", ausentes);
        exigir(payload, "depositoId", ausentes);
        exigir(payload, "tipoMovimento", ausentes);
        exigir(payload, "quantidade", ausentes);

        if (!ausentes.isEmpty()) {
            return Optional.of("Campo(s) obrigatório(s) ausente(s) no payload: " + String.join(", ", ausentes));
        }
        return Optional.empty();
    }

    /** {@code tipoLancamento} só C/P; pagamento exige o caixa onde o dinheiro entrou (é nele que a baixa cai). */
    private Optional<String> validarFiado(JsonNode payload) {
        Optional<String> ausentes = exigirTodos(payload, "clienteId", "tipoLancamento", "valor", "dataLancamento");
        if (ausentes.isPresent()) {
            return ausentes;
        }
        String tipo = payload.get("tipoLancamento").asString();
        if (!"C".equals(tipo) && !"P".equals(tipo)) {
            return Optional.of("tipoLancamento deve ser C (compra a prazo) ou P (pagamento), recebido: " + tipo);
        }
        if ("P".equals(tipo) && (payload.get("pdvCaixaId") == null || payload.get("pdvCaixaId").isNull())) {
            return Optional.of("Pagamento de fiado (tipoLancamento=P) exige pdvCaixaId — o caixa onde o dinheiro foi recebido");
        }
        return Optional.empty();
    }

    /**
     * Forma mínima da venda: cabeçalho, ao menos 1 item com os campos que o consumidor exige, e — se
     * FECHADA — ao menos 1 pagamento com tPag. A consistência dos valores (pagamentos × recebido × troco)
     * é regra de negócio, fica no vendas-service.
     */
    private Optional<String> validarVenda(JsonNode payload) {
        Optional<String> ausentes = exigirTodos(payload, "pdvCaixaId", "dataHoraVenda", "situacao", "valorFinal", "valorRecebido", "itens");
        if (ausentes.isPresent()) {
            return ausentes;
        }
        String situacao = payload.get("situacao").asString();
        if (!"F".equals(situacao) && !"C".equals(situacao)) {
            return Optional.of("situacao deve ser F (fechada) ou C (cancelada), recebido: " + situacao);
        }
        JsonNode itens = payload.get("itens");
        if (!itens.isArray() || itens.isEmpty()) {
            return Optional.of("A venda precisa de ao menos 1 item em 'itens'");
        }
        for (JsonNode item : itens) {
            Optional<String> erroItem = exigirTodos(item, "numeroItem", "produtoId", "quantidade", "valorUnitario", "valorTotal");
            if (erroItem.isPresent()) {
                return Optional.of("Item da venda: " + erroItem.get());
            }
        }
        JsonNode pagamentos = payload.get("pagamentos");
        if ("F".equals(situacao)) {
            if (pagamentos == null || !pagamentos.isArray() || pagamentos.isEmpty()) {
                return Optional.of("Venda fechada precisa de ao menos 1 pagamento em 'pagamentos'");
            }
            for (JsonNode pagamento : pagamentos) {
                Optional<String> erroPagamento = exigirTodos(pagamento, "codigoPagamentoNfce", "valor");
                if (erroPagamento.isPresent()) {
                    return Optional.of("Pagamento da venda: " + erroPagamento.get());
                }
            }
        }
        return Optional.empty();
    }

    private Optional<String> exigirTodos(JsonNode payload, String... campos) {
        List<String> ausentes = new ArrayList<>();
        for (String campo : campos) {
            exigir(payload, campo, ausentes);
        }
        return ausentes.isEmpty()
                ? Optional.empty()
                : Optional.of("Campo(s) obrigatório(s) ausente(s) no payload: " + String.join(", ", ausentes));
    }

    private void exigir(JsonNode payload, String campo, List<String> ausentes) {
        JsonNode valor = payload.get(campo);
        if (valor == null || valor.isNull()) {
            ausentes.add(campo);
        }
    }
}
