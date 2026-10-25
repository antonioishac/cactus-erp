package br.com.cactusdigital.erp.vendas.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.common.jpa.PaginatedFetchSupport;
import br.com.cactusdigital.erp.vendas.client.FinanceiroClient;
import br.com.cactusdigital.erp.vendas.client.VendaPdvDinheiroClientRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaPdvResponse;
import br.com.cactusdigital.erp.vendas.entity.OrigemVendaPdv;
import br.com.cactusdigital.erp.vendas.entity.SituacaoVendaPdv;
import br.com.cactusdigital.erp.vendas.entity.VendaPdv;
import br.com.cactusdigital.erp.vendas.entity.VendaPdvDevolucao;
import br.com.cactusdigital.erp.vendas.entity.VendaPdvItem;
import br.com.cactusdigital.erp.vendas.entity.VendaPdvPagamento;
import br.com.cactusdigital.erp.vendas.exception.VendasErrorCode;
import br.com.cactusdigital.erp.vendas.mapper.VendaPdvMapper;
import br.com.cactusdigital.erp.vendas.repository.VendaPdvDevolucaoRepository;
import br.com.cactusdigital.erp.vendas.repository.VendaPdvRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;

/**
 * Registra vendas e devoluções do PDV (Fase 6-B, itens 5-7) chegando pela fila {@code vendas.pdv.q}.
 *
 * <h3>O que a venda faz e o que ela não faz — padrão de ERP de varejo</h3>
 * <ul>
 *   <li><b>Estoque: nada.</b> O PDV já grava a saída de cada item (e a reentrada da devolução) no próprio
 *   {@code ESTOQUE_MOVIMENTO}, que chega ao controle-estoque-service por conta própria. O ledger de estoque
 *   tem uma fonte só; baixar também daqui contaria cada item duas vezes.</li>
 *   <li><b>Caixa: só o dinheiro líquido</b> (pagamentos tPag 01 − troco) entra na conta do terminal, via
 *   financeiro-service. É exatamente o que o PDV soma como "esperado" de dinheiro na conferência (fundo +
 *   suprimentos + vendas em dinheiro − sangrias − troco), então o saldo do terminal no ERP bate com a gaveta.</li>
 *   <li><b>Cartão/PIX/outros:</b> ficam registrados nos pagamentos da venda (base de BI e conciliação), mas
 *   ainda não viram título a receber da operadora: isso exige cadastro de adquirente, taxa e prazo de
 *   repasse, que não existe — gerar prazo/taxa "no chute" seria pior que não gerar.</li>
 *   <li><b>Fiado (tPag 05):</b> nada aqui — a dívida já chega pelo {@code FIADO_LANCAMENTO} tipo C.</li>
 * </ul>
 *
 * <h3>Consistência</h3>
 * Venda {@code FECHADA} só é gravada se a soma dos pagamentos não estornados bater com o recebido, e se
 * recebido − troco bater com o valor final (tolerância de 1 centavo, arredondamento do PDV). Uma venda
 * inconsistente vai pra revisão ({@code ERRO} no sincronizacao-service) em vez de entrar torta no ERP.
 *
 * <h3>Idempotência</h3>
 * Pelo {@code registroSincronizacaoId} (UNIQUE). Reentrega só reconfirma. A chamada ao financeiro roda
 * dentro desta transação e também é idempotente: se ela passar e o commit daqui falhar, o retry reaplica
 * a venda e o financeiro reconhece o registro sem lançar de novo.
 */
@Service
@RequiredArgsConstructor
public class VendaPdvService {

    private static final BigDecimal TOLERANCIA = new BigDecimal("0.01");

    private final VendaPdvRepository repository;
    private final VendaPdvDevolucaoRepository devolucaoRepository;
    private final FinanceiroClient financeiroClient;
    private final VendaPdvMapper mapper;

    /** Fuso da loja: o PDV manda data/hora local, o ERP guarda {@link Instant}. */
    @Value("${cactus.vendas.pdv.fuso-horario:America/Sao_Paulo}")
    private String fusoHorario;

    @Transactional(readOnly = true)
    public Page<VendaPdvResponse> listar(Long empresaId, LocalDate dataInicio, LocalDate dataFim, Long pdvCaixaId, Pageable pageable) {
        ZoneId fuso = ZoneId.of(fusoHorario);
        Page<VendaPdv> pagina = repository.buscarPorPeriodo(empresaId, dataInicio.atStartOfDay(fuso).toInstant(),
                dataFim.plusDays(1).atStartOfDay(fuso).toInstant(), pdvCaixaId, pageable);
        return PaginatedFetchSupport.comColecoesCarregadas(pagina, VendaPdv::getId, this::hidratar, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VendaPdvResponse buscarPorId(Long id) {
        return hidratar(List.of(id)).stream().findFirst().map(mapper::toResponse)
                .orElseThrow(() -> NotFoundException.of("Venda do PDV", id));
    }

    /** @return {@code false} se já tinha sido registrada antes (reentrega) — nada foi feito de novo. */
    @Transactional
    public boolean registrarVenda(JsonNode m) {
        Long registroId = m.get("registroId").asLong();
        if (repository.existsByRegistroSincronizacaoId(registroId)) {
            return false;
        }
        Long empresaId = m.get("lojaId").asLong();
        SituacaoVendaPdv situacao = "C".equals(texto(m, "situacao")) ? SituacaoVendaPdv.CANCELADA : SituacaoVendaPdv.FECHADA;

        VendaPdv venda = VendaPdv.builder()
                .empresaId(empresaId)
                .registroSincronizacaoId(registroId)
                .idOrigem(texto(m, "idOrigem"))
                .pdvCaixaId(m.get("pdvCaixaId").asLong())
                .pdvCaixaNome(texto(m, "pdvCaixaNome"))
                .pdvOperadorId(inteiroLongo(m, "pdvOperadorId"))
                .clienteId(inteiroLongo(m, "clienteId"))
                .colaboradorId(inteiroLongo(m, "colaboradorId"))
                .nomeCliente(texto(m, "nomeCliente"))
                .cpfCnpjCliente(texto(m, "cpfCnpjCliente"))
                .dataHoraVenda(instante(m, "dataHoraVenda"))
                .origemVenda(origem(texto(m, "origemVenda")))
                .situacao(situacao)
                .tipoOperacaoFiscal(texto(m, "tipoOperacaoFiscal"))
                .statusFiscal(texto(m, "statusFiscal"))
                .chaveAcessoFiscal(texto(m, "chaveAcessoFiscal"))
                .protocoloAutorizacao(texto(m, "protocoloAutorizacao"))
                .coo(inteiro(m, "coo"))
                .ccf(inteiro(m, "ccf"))
                .valorTotalProdutos(decimal(m, "valorTotalProdutos", decimal(m, "valorFinal", null)))
                .valorDesconto(decimal(m, "valorDesconto", BigDecimal.ZERO))
                .valorAcrescimo(decimal(m, "valorAcrescimo", BigDecimal.ZERO))
                .valorFinal(decimal(m, "valorFinal", null))
                .valorRecebido(decimal(m, "valorRecebido", null))
                .valorTroco(decimal(m, "valorTroco", BigDecimal.ZERO))
                .valorBaseIcms(decimal(m, "valorBaseIcms", null))
                .valorIcms(decimal(m, "valorIcms", null))
                .valorPis(decimal(m, "valorPis", null))
                .valorCofins(decimal(m, "valorCofins", null))
                .valorIssqn(decimal(m, "valorIssqn", null))
                .build();

        for (JsonNode i : m.get("itens")) {
            venda.getItens().add(VendaPdvItem.builder()
                    .venda(venda)
                    .numeroItem(i.get("numeroItem").asInt())
                    .produtoId(i.get("produtoId").asLong())
                    .gtin(texto(i, "gtin"))
                    .quantidade(decimal(i, "quantidade", null))
                    .valorUnitario(decimal(i, "valorUnitario", null))
                    .valorDesconto(decimal(i, "valorDesconto", BigDecimal.ZERO))
                    .valorAcrescimo(decimal(i, "valorAcrescimo", BigDecimal.ZERO))
                    .valorTotal(decimal(i, "valorTotal", null))
                    .cfop(inteiro(i, "cfop"))
                    .cst(texto(i, "cst"))
                    .aliquotaIcms(decimal(i, "aliquotaIcms", null))
                    .valorIcms(decimal(i, "valorIcms", null))
                    .valorPis(decimal(i, "valorPis", null))
                    .valorCofins(decimal(i, "valorCofins", null))
                    .cancelado(i.has("cancelado") && i.get("cancelado").asBoolean())
                    .build());
        }
        JsonNode pagamentos = m.get("pagamentos");
        if (pagamentos != null) {
            for (JsonNode p : pagamentos) {
                venda.getPagamentos().add(VendaPdvPagamento.builder()
                        .venda(venda)
                        .formaPagamentoId(inteiroLongo(p, "formaPagamentoId"))
                        .codigoPagamentoNfce(texto(p, "codigoPagamentoNfce"))
                        .descricao(texto(p, "descricao"))
                        .valor(decimal(p, "valor", null))
                        .nsu(texto(p, "nsu"))
                        .rede(texto(p, "rede"))
                        .cartaoDebitoCredito(texto(p, "cartaoDebitoCredito"))
                        .estornado(p.has("estornado") && p.get("estornado").asBoolean())
                        .build());
            }
        }

        if (situacao == SituacaoVendaPdv.FECHADA) {
            validarConsistencia(venda);
            venda.setValorDinheiroLiquido(dinheiroLiquido(venda));
        }
        repository.saveAndFlush(venda);

        if (venda.getValorDinheiroLiquido().signum() > 0) {
            financeiroClient.registrarDinheiroVendaPdv(new VendaPdvDinheiroClientRequest(
                    registroId, empresaId, venda.getPdvCaixaId(), venda.getPdvCaixaNome(), venda.getValorDinheiroLiquido(),
                    LocalDateTime.ofInstant(venda.getDataHoraVenda(), ZoneId.of(fusoHorario)).toLocalDate(),
                    "venda " + venda.getIdOrigem()));
        }
        return true;
    }

    /**
     * Registra a devolução de um item. A venda é localizada pelo {@code idOrigem} que o PDV usou ao enviá-la.
     * Se a venda ainda não chegou (lotes fora de ordem), lança erro TÉCNICO de propósito: é transitório, o retry
     * da fila tenta de novo, e só depois de esgotar vai pra {@code ERRO} (reprocessável).
     */
    @Transactional
    public boolean registrarDevolucao(JsonNode m) {
        Long registroId = m.get("registroId").asLong();
        if (devolucaoRepository.existsByRegistroSincronizacaoId(registroId)) {
            return false;
        }
        Long empresaId = m.get("lojaId").asLong();
        String vendaIdOrigem = texto(m, "vendaIdOrigem");
        VendaPdv venda = repository.findByEmpresaIdAndIdOrigem(empresaId, vendaIdOrigem)
                .orElseThrow(() -> new IllegalStateException(
                        "Venda do PDV '" + vendaIdOrigem + "' ainda não recebida pelo ERP — a devolução será retentada"));
        if (venda.getSituacao() == SituacaoVendaPdv.CANCELADA) {
            throw new BusinessException(VendasErrorCode.VENDA_PDV_CANCELADA_NAO_ADMITE_DEVOLUCAO);
        }
        int numeroItem = m.get("numeroItem").asInt();
        VendaPdvItem item = venda.getItens().stream()
                .filter(i -> i.getNumeroItem() == numeroItem && !i.isCancelado())
                .findFirst()
                .orElseThrow(() -> new BusinessException(VendasErrorCode.VENDA_PDV_ITEM_NAO_ENCONTRADO,
                        "Item " + numeroItem + " não existe (ou foi cancelado) na venda do PDV '" + vendaIdOrigem + "'"));

        BigDecimal quantidade = decimal(m, "quantidadeDevolvida", null);
        BigDecimal disponivel = item.getQuantidade().subtract(item.getQuantidadeDevolvida());
        if (quantidade.compareTo(disponivel) > 0) {
            throw new BusinessException(VendasErrorCode.VENDA_PDV_DEVOLUCAO_ACIMA_DO_VENDIDO,
                    "Devolução de " + quantidade.toPlainString() + " do item " + numeroItem + ", mas só restam "
                            + disponivel.toPlainString() + " na venda do PDV '" + vendaIdOrigem + "'");
        }
        item.setQuantidadeDevolvida(item.getQuantidadeDevolvida().add(quantidade));

        venda.getDevolucoes().add(devolucaoRepository.save(VendaPdvDevolucao.builder()
                .venda(venda)
                .item(item)
                .registroSincronizacaoId(registroId)
                .quantidadeDevolvida(quantidade)
                .valorDevolvido(decimal(m, "valorDevolvido", null))
                .motivo(texto(m, "motivo"))
                .pdvOperadorId(inteiroLongo(m, "pdvOperadorId"))
                .pdvGerenteAutorizadorId(m.get("pdvGerenteAutorizadorId").asLong())
                .dataHoraDevolucao(instante(m, "dataHoraDevolucao"))
                .build()));
        return true;
    }

    /**
     * Cancela uma venda que chegou FECHADA. Só registra o fato e desfaz o dinheiro no caixa do terminal: o estoque
     * volta pelo {@code ESTOQUE_MOVIMENTO} de reentrada que o próprio PDV gera (mesma regra da venda, uma fonte só
     * para o ledger), e relatórios/BI já ignoram vendas canceladas. Venda ainda não recebida = erro técnico
     * (retentado), igual à devolução.
     */
    @Transactional
    public boolean registrarCancelamento(JsonNode m) {
        Long registroId = m.get("registroId").asLong();
        if (repository.existsByRegistroCancelamentoId(registroId)) {
            return false;
        }
        Long empresaId = m.get("lojaId").asLong();
        String vendaIdOrigem = texto(m, "vendaIdOrigem");
        VendaPdv venda = repository.findByEmpresaIdAndIdOrigem(empresaId, vendaIdOrigem)
                .orElseThrow(() -> new IllegalStateException(
                        "Venda do PDV '" + vendaIdOrigem + "' ainda não recebida pelo ERP — o cancelamento será retentado"));
        if (venda.getSituacao() == SituacaoVendaPdv.CANCELADA) {
            throw new BusinessException(VendasErrorCode.VENDA_PDV_JA_CANCELADA,
                    "A venda do PDV '" + vendaIdOrigem + "' já está cancelada");
        }

        Instant dataHoraCancelamento = instante(m, "dataHoraCancelamento");
        venda.setSituacao(SituacaoVendaPdv.CANCELADA);
        venda.setRegistroCancelamentoId(registroId);
        venda.setDataHoraCancelamento(dataHoraCancelamento);
        venda.setPdvOperadorCancelamentoId(inteiroLongo(m, "pdvOperadorId"));
        venda.setPdvGerenteCancelamentoId(m.get("pdvGerenteAutorizadorId").asLong());
        venda.setJustificativaCancelamento(texto(m, "justificativa").trim());
        venda.setProtocoloCancelamento(texto(m, "protocoloCancelamento"));
        repository.saveAndFlush(venda);

        if (venda.getValorDinheiroLiquido().signum() > 0) {
            financeiroClient.estornarDinheiroVendaPdv(new VendaPdvDinheiroClientRequest(
                    registroId, empresaId, venda.getPdvCaixaId(), venda.getPdvCaixaNome(), venda.getValorDinheiroLiquido(),
                    LocalDateTime.ofInstant(dataHoraCancelamento, ZoneId.of(fusoHorario)).toLocalDate(),
                    "venda " + venda.getIdOrigem()));
        }
        return true;
    }

    private void validarConsistencia(VendaPdv venda) {
        BigDecimal somaPagamentos = venda.getPagamentos().stream()
                .filter(p -> !p.isEstornado())
                .map(VendaPdvPagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (somaPagamentos.subtract(venda.getValorRecebido()).abs().compareTo(TOLERANCIA) > 0) {
            throw new BusinessException(VendasErrorCode.VENDA_PDV_PAGAMENTOS_DIVERGEM_DO_RECEBIDO,
                    "Pagamentos somam R$ " + somaPagamentos.toPlainString() + ", recebido R$ "
                            + venda.getValorRecebido().toPlainString() + " (venda do PDV '" + venda.getIdOrigem() + "')");
        }
        BigDecimal liquido = venda.getValorRecebido().subtract(venda.getValorTroco());
        if (liquido.subtract(venda.getValorFinal()).abs().compareTo(TOLERANCIA) > 0) {
            throw new BusinessException(VendasErrorCode.VENDA_PDV_RECEBIDO_MENOS_TROCO_DIVERGE_DO_FINAL,
                    "Recebido − troco = R$ " + liquido.toPlainString() + ", valor final R$ "
                            + venda.getValorFinal().toPlainString() + " (venda do PDV '" + venda.getIdOrigem() + "')");
        }
    }

    /** Dinheiro que fica na gaveta: soma do tPag 01 menos o troco (o troco sempre sai do dinheiro). Nunca negativo. */
    private BigDecimal dinheiroLiquido(VendaPdv venda) {
        BigDecimal dinheiro = venda.getPagamentos().stream()
                .filter(p -> !p.isEstornado() && VendaPdvPagamento.TPAG_DINHEIRO.equals(p.getCodigoPagamentoNfce()))
                .map(VendaPdvPagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return dinheiro.subtract(venda.getValorTroco()).max(BigDecimal.ZERO);
    }

    private List<VendaPdv> hidratar(List<Long> ids) {
        repository.hidratarPagamentos(ids);
        repository.hidratarDevolucoes(ids);
        return repository.hidratarItens(ids);
    }

    private OrigemVendaPdv origem(String codigo) {
        if (codigo == null) {
            return OrigemVendaPdv.BALCAO;
        }
        return switch (codigo) {
            case "M" -> OrigemVendaPdv.MESA;
            case "C" -> OrigemVendaPdv.COMANDA;
            case "D" -> OrigemVendaPdv.DELIVERY;
            case "E" -> OrigemVendaPdv.EXTERNA;
            default -> OrigemVendaPdv.BALCAO;
        };
    }

    private Instant instante(JsonNode m, String campo) {
        return LocalDateTime.parse(m.get(campo).asString()).atZone(ZoneId.of(fusoHorario)).toInstant();
    }

    private BigDecimal decimal(JsonNode m, String campo, BigDecimal padrao) {
        JsonNode v = m.get(campo);
        return v == null || v.isNull() ? padrao : new BigDecimal(v.asString());
    }

    private Integer inteiro(JsonNode m, String campo) {
        JsonNode v = m.get(campo);
        return v == null || v.isNull() ? null : v.asInt();
    }

    private Long inteiroLongo(JsonNode m, String campo) {
        JsonNode v = m.get(campo);
        return v == null || v.isNull() ? null : v.asLong();
    }

    private String texto(JsonNode m, String campo) {
        JsonNode v = m.get(campo);
        return v == null || v.isNull() || v.asString().isBlank() ? null : v.asString();
    }
}
