package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.financeiro.dto.BaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.VendaPdvDinheiroRequest;
import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.ContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.OrigemContaReceber;
import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.SincronizacaoPdvProcessado;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.repository.ContaReceberRepository;
import br.com.cactusdigital.erp.financeiro.repository.SincronizacaoPdvProcessadoRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

/**
 * Aplica no financeiro os registros de caixa/fiado que o PDV empurra (Fase 6-B), um por mensagem
 * publicada pelo {@code sincronizacao-service}. Não tem regra de negócio própria de dinheiro: todo
 * lançamento passa por {@link MovimentoCaixaBancoService#registrar} e {@link ContaReceberService},
 * os mesmos caminhos da API REST (ledger append-only, lock pessimista no saldo).
 *
 * <h3>Decisões (confirmadas com o usuário em 2026-10-24)</h3>
 * <ul>
 *   <li><b>Uma conta {@code CAIXA_INTERNO} por terminal</b> ({@code PDV_CAIXA}), criada no primeiro
 *   envio daquele terminal ({@link #contaDoTerminal}). Saldo por caixa bate com a conferência do
 *   operador, sem misturar terminais.</li>
 *   <li><b>Conferência vira ajuste da diferença</b>: usa a divergência (contado − esperado) da forma
 *   <b>DINHEIRO</b>, que o PDV grava em {@code PDV_CONFERENCIA_CAIXA_DETALHE}. <b>Nunca</b> o
 *   {@code VALOR_DIVERGENCIA} do cabeçalho: ele soma todas as formas (cartão, PIX...), e uma diferença de
 *   cartão não é sobra/falta de dinheiro na gaveta. Corrigido em 2026-10-24, depois de ler o
 *   {@code ConferenciaCaixaService} do PDV (a 1ª versão usava o total). O "esperado" do dinheiro no PDV é
 *   fundo + suprimentos + vendas em dinheiro − sangrias − troco: com {@code VendaPdv} lançando o dinheiro
 *   líquido das vendas, o saldo do caixa no ERP passa a bater com a gaveta.</li>
 * </ul>
 *
 * <h3>Outras regras, deliberadas</h3>
 * <ul>
 *   <li>Fechamento de caixa não movimenta dinheiro além da conferência — fica só na auditoria do
 *   sincronizacao-service, sem tipo de registro próprio.</li>
 *   <li>Abertura com fundo zero e conferência sem divergência não geram movimento (o ledger não aceita
 *   valor zero); só são confirmadas.</li>
 *   <li>Fiado C vira um título {@code FIADO_PDV} com vencimento em {@code data do lançamento +
 *   cactus.financeiro.pdv.fiado-prazo-vencimento-dias} (default 30): o PDV não tem vencimento de fiado.</li>
 *   <li>Fiado P abate os títulos {@code FIADO_PDV} em aberto do cliente do mais antigo pro mais novo,
 *   com uma baixa em cada um, na conta do terminal onde o dinheiro entrou. Se o pagamento for maior que
 *   a dívida em aberto no ERP (ex: fiado anterior à sincronização), a transação inteira falha e o
 *   registro vai pra revisão manual ({@code ERRO} no sincronizacao-service) — nada é aplicado pela
 *   metade, e o excedente nunca vira entrada "solta" de caixa.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class FinanceiroPdvService {

    private final SincronizacaoPdvProcessadoRepository processadoRepository;
    private final ContaTerminalPdvService contaTerminalPdvService;
    private final ContaReceberRepository contaReceberRepository;
    private final MovimentoCaixaBancoService movimentoService;
    private final ContaReceberService contaReceberService;

    @Value("${cactus.financeiro.pdv.fiado-prazo-vencimento-dias:30}")
    private int fiadoPrazoVencimentoDias;

    /** @return {@code false} se o registro já tinha sido aplicado antes (reentrega) — nada foi feito de novo. */
    @Transactional
    public boolean processar(JsonNode mensagem) {
        Long registroId = mensagem.get("registroId").asLong();
        String tipo = mensagem.get("tipoRegistro").asString();
        if (processadoRepository.existsById(registroId)) {
            return false;
        }
        Long empresaId = mensagem.get("lojaId").asLong();

        switch (tipo) {
            case "SANGRIA" -> movimentar(empresaId, mensagem, TipoMovimentoCaixa.SAIDA, OrigemMovimentoCaixa.SANGRIA_PDV,
                    decimal(mensagem, "valor"), data(mensagem, "dataMovimento"), "Sangria PDV", registroId);
            case "SUPRIMENTO" -> movimentar(empresaId, mensagem, TipoMovimentoCaixa.ENTRADA, OrigemMovimentoCaixa.SUPRIMENTO_PDV,
                    decimal(mensagem, "valor"), data(mensagem, "dataMovimento"), "Suprimento PDV", registroId);
            case "ABERTURA_CAIXA" -> movimentar(empresaId, mensagem, TipoMovimentoCaixa.ENTRADA, OrigemMovimentoCaixa.ABERTURA_CAIXA_PDV,
                    decimal(mensagem, "valorFundoInicial"), data(mensagem, "dataAbertura"), "Fundo de troco - abertura do caixa PDV", registroId);
            case "CONFERENCIA_CAIXA" -> conferencia(empresaId, mensagem, registroId);
            case "FIADO_LANCAMENTO" -> fiado(empresaId, mensagem, registroId);
            default -> throw new IllegalArgumentException("tipoRegistro sem destino no financeiro-service: " + tipo);
        }

        // Mesma transação do lançamento: ou os dois ficam, ou nenhum (ver V8__sincronizacao_pdv.sql).
        processadoRepository.save(new SincronizacaoPdvProcessado(registroId, tipo, Instant.now()));
        return true;
    }

    private void conferencia(Long empresaId, JsonNode mensagem, Long registroId) {
        BigDecimal divergencia = decimal(mensagem, "valorDivergenciaDinheiro");
        if (divergencia.signum() == 0) {
            return;
        }
        TipoMovimentoCaixa tipo = divergencia.signum() > 0 ? TipoMovimentoCaixa.ENTRADA : TipoMovimentoCaixa.SAIDA;
        String historico = (divergencia.signum() > 0 ? "Sobra" : "Falta") + " na conferência do caixa PDV";
        movimentar(empresaId, mensagem, tipo, OrigemMovimentoCaixa.CONFERENCIA_CAIXA_PDV,
                divergencia.abs(), data(mensagem, "dataConferencia"), historico, registroId);
    }

    private void fiado(Long empresaId, JsonNode mensagem, Long registroId) {
        Long clienteId = mensagem.get("clienteId").asLong();
        BigDecimal valor = decimal(mensagem, "valor");
        LocalDate dataLancamento = data(mensagem, "dataLancamento");
        String observacao = texto(mensagem, "observacao");

        if ("C".equals(mensagem.get("tipoLancamento").asString())) {
            contaReceberRepository.save(ContaReceber.builder()
                    .empresaId(empresaId)
                    .clienteId(clienteId)
                    .origemTipo(OrigemContaReceber.FIADO_PDV)
                    .origemId(registroId)
                    .numeroDocumento("Fiado PDV #" + registroId)
                    .descricao(observacao != null ? truncar(observacao, 250) : "Compra a prazo no fiado do PDV")
                    .dataEmissao(dataLancamento)
                    .dataVencimento(dataLancamento.plusDays(fiadoPrazoVencimentoDias))
                    .valorOriginal(valor)
                    .valorSaldo(valor)
                    .build());
            return;
        }

        Long contaTerminalId = contaDoTerminal(empresaId, mensagem).getId();
        List<ContaReceber> emAberto = contaReceberRepository.buscarFiadoEmAberto(empresaId, clienteId);
        BigDecimal restante = valor;
        for (ContaReceber titulo : emAberto) {
            if (restante.signum() == 0) {
                break;
            }
            BigDecimal parcela = restante.min(titulo.getValorSaldo());
            contaReceberService.registrarBaixa(titulo.getId(), new BaixaRequest(
                    dataLancamento, parcela, null, null, contaTerminalId, null, null,
                    "Pagamento de fiado no PDV (registro #" + registroId + ")", null));
            restante = restante.subtract(parcela);
        }
        if (restante.signum() > 0) {
            throw new BusinessException(FinanceiroErrorCode.PAGAMENTO_FIADO_MAIOR_QUE_DIVIDA,
                    "Pagamento de fiado de R$ " + valor.toPlainString() + " do cliente #" + clienteId
                            + " excede a dívida de fiado em aberto no ERP em R$ " + restante.toPlainString());
        }
    }

    private void movimentar(Long empresaId, JsonNode mensagem, TipoMovimentoCaixa tipo, OrigemMovimentoCaixa origem,
            BigDecimal valor, LocalDate dataMovimento, String historico, Long registroId) {
        if (valor.signum() == 0) {
            return;
        }
        String observacao = texto(mensagem, "observacao");
        movimentoService.registrar(contaDoTerminal(empresaId, mensagem).getId(), tipo, origem, registroId,
                valor, dataMovimento, observacao != null ? truncar(historico + " - " + observacao, 250) : historico, null);
    }

    /** Conta {@code CAIXA_INTERNO} do terminal (empresa + dispositivo + terminal), criada na primeira vez — ver
     * {@link ContaTerminalPdvService}. O {@code dispositivoId} vem do relay do sincronizacao-service. */
    ContaBancaria contaDoTerminal(Long empresaId, JsonNode mensagem) {
        String dispositivoId = texto(mensagem, "dispositivoId");
        if (dispositivoId == null) {
            throw new IllegalArgumentException("Mensagem do PDV sem dispositivoId — não dá para identificar o terminal");
        }
        return contaTerminalPdvService.obter(empresaId, dispositivoId, mensagem.get("pdvCaixaId").asLong(), texto(mensagem, "pdvCaixaNome"));
    }

    /**
     * Dinheiro líquido de uma venda do PDV (dinheiro recebido − troco) entrando no caixa do terminal —
     * chamado pelo {@code vendas-service} ao registrar a {@code VendaPdv}, de forma síncrona e dentro da
     * transação dele. Idempotente pela mesma tabela dos demais registros do PDV: se o vendas-service
     * reprocessar a mensagem (falha no commit dele depois desta chamada), não lança de novo.
     *
     * @return {@code false} se já tinha sido lançado antes
     */
    @Transactional
    public boolean registrarDinheiroVenda(VendaPdvDinheiroRequest request) {
        if (processadoRepository.existsById(request.registroId())) {
            return false;
        }
        if (request.valor().signum() > 0) {
            Long contaId = contaTerminalPdvService.obter(request.empresaId(), request.pdvDispositivoId(), request.pdvCaixaId(), request.pdvCaixaNome()).getId();
            movimentoService.registrar(contaId, TipoMovimentoCaixa.ENTRADA, OrigemMovimentoCaixa.VENDA_PDV, request.registroId(),
                    request.valor(), request.dataVenda(), truncar("Venda PDV em dinheiro (líquido de troco) - " + request.descricao(), 250), null);
        }
        processadoRepository.save(new SincronizacaoPdvProcessado(request.registroId(), "VENDA", Instant.now()));
        return true;
    }

    /**
     * Desfaz o dinheiro de uma venda do PDV cancelada depois de fechada: SAÍDA do mesmo valor líquido na conta do
     * terminal. Espelha o PDV, cuja conferência só soma vendas FECHADAS: cancelada, a venda some do "esperado" da
     * gaveta. Idempotente por {@code registroId} (o do cancelamento, não o da venda).
     */
    @Transactional
    public boolean estornarDinheiroVenda(VendaPdvDinheiroRequest request) {
        if (processadoRepository.existsById(request.registroId())) {
            return false;
        }
        if (request.valor().signum() > 0) {
            Long contaId = contaTerminalPdvService.obter(request.empresaId(), request.pdvDispositivoId(), request.pdvCaixaId(), request.pdvCaixaNome()).getId();
            movimentoService.registrar(contaId, TipoMovimentoCaixa.SAIDA, OrigemMovimentoCaixa.CANCELAMENTO_VENDA_PDV, request.registroId(),
                    request.valor(), request.dataVenda(), truncar("Cancelamento de venda PDV (estorno do dinheiro) - " + request.descricao(), 250), null);
        }
        processadoRepository.save(new SincronizacaoPdvProcessado(request.registroId(), "CANCELAMENTO_VENDA", Instant.now()));
        return true;
    }

    private BigDecimal decimal(JsonNode mensagem, String campo) {
        return new BigDecimal(mensagem.get(campo).asString());
    }

    private LocalDate data(JsonNode mensagem, String campo) {
        return LocalDate.parse(mensagem.get(campo).asString());
    }

    private String texto(JsonNode mensagem, String campo) {
        JsonNode valor = mensagem.get(campo);
        return valor == null || valor.isNull() || valor.asString().isBlank() ? null : valor.asString();
    }

    private String truncar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max);
    }
}
