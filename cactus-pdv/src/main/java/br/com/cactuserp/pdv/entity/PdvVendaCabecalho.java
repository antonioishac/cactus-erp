package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cabeçalho de uma venda (cupom) no PDV.
 *
 * <p>Mapeia a tabela {@code PDV_VENDA_CABECALHO}. Um cupom nasce com status
 * {@link StatusVenda#A} ao primeiro bip de produto, recebe N itens
 * ({@link PdvVendaDetalhe}) e termina com {@link StatusVenda#F} (finalizada)
 * ou {@link StatusVenda#C} (cancelada).
 *
 * <p>Esta entidade é o agregado central da operação fiscal — toda venda
 * passa por aqui, e o {@code HASH_REGISTRO} torna o registro auditável
 * para o fisco (PAF-NFC-e).
 */
@Entity
@Table(name = "PDV_VENDA_CABECALHO")
public class PdvVendaCabecalho {

    // ====== Identificação ====================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // ====== Relacionamentos ==================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COLABORADOR")
    private Colaborador colaborador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PDV_MOVIMENTO")
    private PdvMovimento pdvMovimento;

    @OneToMany(mappedBy = "pdvVendaCabecalho",
            cascade = CascadeType.ALL,
            orphanRemoval = false,
            fetch = FetchType.LAZY)
    private List<PdvVendaDetalhe> detalhes = new ArrayList<>();

    // ====== Identificação fiscal =============================================
    @Column(name = "CFOP")
    private Integer cfop;

    @Column(name = "COO")
    private Integer coo;

    @Column(name = "CCF")
    private Integer ccf;

    // ====== Data e hora da venda =============================================
    @Column(name = "DATA_VENDA")
    private LocalDate dataVenda;

    @Column(name = "HORA_VENDA", length = 8)
    private String horaVenda;

    // ====== Valores principais ===============================================
    @Column(name = "VALOR_VENDA", precision = 18, scale = 6)
    private BigDecimal valorVenda;

    @Column(name = "TAXA_DESCONTO", precision = 18, scale = 6)
    private BigDecimal taxaDesconto;

    @Column(name = "VALOR_DESCONTO", precision = 18, scale = 6)
    private BigDecimal valorDesconto;

    @Column(name = "TAXA_ACRESCIMO", precision = 18, scale = 6)
    private BigDecimal taxaAcrescimo;

    @Column(name = "VALOR_ACRESCIMO", precision = 18, scale = 6)
    private BigDecimal valorAcrescimo;

    @Column(name = "VALOR_FINAL", precision = 18, scale = 6)
    private BigDecimal valorFinal;

    @Column(name = "VALOR_RECEBIDO", precision = 18, scale = 6)
    private BigDecimal valorRecebido;

    @Column(name = "VALOR_TROCO", precision = 18, scale = 6)
    private BigDecimal valorTroco;

    @Column(name = "VALOR_CANCELADO", precision = 18, scale = 6)
    private BigDecimal valorCancelado;

    @Column(name = "VALOR_TOTAL_PRODUTOS", precision = 18, scale = 6)
    private BigDecimal valorTotalProdutos;

    @Column(name = "VALOR_TOTAL_DOCUMENTO", precision = 18, scale = 6)
    private BigDecimal valorTotalDocumento;

    // ====== Bases e valores tributários ======================================
    @Column(name = "VALOR_BASE_ICMS", precision = 18, scale = 6)
    private BigDecimal valorBaseIcms;

    @Column(name = "VALOR_ICMS", precision = 18, scale = 6)
    private BigDecimal valorIcms;

    @Column(name = "VALOR_ICMS_OUTRAS", precision = 18, scale = 6)
    private BigDecimal valorIcmsOutras;

    @Column(name = "VALOR_ISSQN", precision = 18, scale = 6)
    private BigDecimal valorIssqn;

    @Column(name = "VALOR_PIS", precision = 18, scale = 6)
    private BigDecimal valorPis;

    @Column(name = "VALOR_COFINS", precision = 18, scale = 6)
    private BigDecimal valorCofins;

    @Column(name = "VALOR_ACRESCIMO_ITENS", precision = 18, scale = 6)
    private BigDecimal valorAcrescimoItens;

    @Column(name = "VALOR_DESCONTO_ITENS", precision = 18, scale = 6)
    private BigDecimal valorDescontoItens;

    // ====== Status e flags ===================================================
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_VENDA", length = 1, columnDefinition = "CHAR(1)")
    private StatusVenda statusVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "CUPOM_CANCELADO", length = 1, columnDefinition = "CHAR(1)")
    private SimNao cupomCancelado;

    // ====== Dados do cliente desnormalizados (para o cupom impresso) ========
    @Column(name = "NOME_CLIENTE", length = 100)
    private String nomeCliente;

    @Column(name = "CPF_CNPJ_CLIENTE", length = 14)
    private String cpfCnpjCliente;

    // ====== Auditoria fiscal (PAF-NFC-e) ====================================
    /** Hash de integridade calculado pelo VendaService antes de persistir. */
    @Column(name = "HASH_REGISTRO", length = 64)
    private String hashRegistro;

    @Column(name = "TIPO_OPERACAO", length = 3, columnDefinition = "CHAR(3)")
    private String tipoOperacao; // NFC, SAT, MFE, REC

    // === Fiscal — resultado da emissão via ACBr (ver EmissaoFiscalService) ===
    // NULL = venda anterior a essa feature, ainda não processada.

    /** P=Pendente reprocessamento | E=Emitido | N=Não configurado (sem ACBr). */
    @Column(name = "STATUS_FISCAL", length = 1, columnDefinition = "CHAR(1)")
    private String statusFiscal;

    @Column(name = "CHAVE_ACESSO_FISCAL", length = 44)
    private String chaveAcessoFiscal;

    @Column(name = "PROTOCOLO_AUTORIZACAO", length = 30)
    private String protocoloAutorizacao;

    @Column(name = "MENSAGEM_FISCAL", length = 500)
    private String mensagemFiscal;

    @Column(name = "ID_GERADO_DISPOSITIVO")
    private Integer idGeradoDispositivo;

    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    // ====== Rastreabilidade — origem da venda (Fase 1.2) ===================
    /**
     * De onde veio essa venda: Balcão, Mesa, Comanda, Delivery ou Externa.
     * Default B (balcão) — todas as vendas existentes ficam como Balcão.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ORIGEM_VENDA", length = 1, columnDefinition = "CHAR(1)", nullable = false)
    private OrigemVenda origemVenda = OrigemVenda.B;

    /**
     * Vínculo com a conta aberta (mesa/comanda/delivery) de origem.
     * NULL quando origem = B (balcão direto).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTA_ABERTA")
    private ContaAberta contaAberta;

    /**
     * ID do garçom responsável pela venda. Reservado pra quando criarmos
     * tabela GARCOM no próximo módulo (app mobile). Hoje fica null.
     */
    @Column(name = "ID_GARCOM")
    private Integer idGarcom;


    // ====== Construtores =====================================================
    public PdvVendaCabecalho() {}

    // ====== Helpers de negócio ==============================================
    /** Adiciona um detalhe e configura o relacionamento inverso. */
    public void adicionarDetalhe(PdvVendaDetalhe detalhe) {
        detalhes.add(detalhe);
        detalhe.setPdvVendaCabecalho(this);
    }

    /** Atalho — true quando o cupom ainda pode receber itens. */
    public boolean estaAberto() {
        return statusVenda == StatusVenda.A;
    }

    // ====== Getters / Setters ===============================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Colaborador getColaborador() { return colaborador; }
    public void setColaborador(Colaborador colaborador) { this.colaborador = colaborador; }

    public PdvMovimento getPdvMovimento() { return pdvMovimento; }
    public void setPdvMovimento(PdvMovimento pdvMovimento) { this.pdvMovimento = pdvMovimento; }

    public List<PdvVendaDetalhe> getDetalhes() { return detalhes; }
    public void setDetalhes(List<PdvVendaDetalhe> detalhes) { this.detalhes = detalhes; }

    public Integer getCfop() { return cfop; }
    public void setCfop(Integer cfop) { this.cfop = cfop; }

    public Integer getCoo() { return coo; }
    public void setCoo(Integer coo) { this.coo = coo; }

    public Integer getCcf() { return ccf; }
    public void setCcf(Integer ccf) { this.ccf = ccf; }

    public LocalDate getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDate dataVenda) { this.dataVenda = dataVenda; }

    public String getHoraVenda() { return horaVenda; }
    public void setHoraVenda(String horaVenda) { this.horaVenda = horaVenda; }

    public BigDecimal getValorVenda() { return valorVenda; }
    public void setValorVenda(BigDecimal v) { this.valorVenda = v; }

    public BigDecimal getTaxaDesconto() { return taxaDesconto; }
    public void setTaxaDesconto(BigDecimal v) { this.taxaDesconto = v; }

    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal v) { this.valorDesconto = v; }

    public BigDecimal getTaxaAcrescimo() { return taxaAcrescimo; }
    public void setTaxaAcrescimo(BigDecimal v) { this.taxaAcrescimo = v; }

    public BigDecimal getValorAcrescimo() { return valorAcrescimo; }
    public void setValorAcrescimo(BigDecimal v) { this.valorAcrescimo = v; }

    public BigDecimal getValorFinal() { return valorFinal; }
    public void setValorFinal(BigDecimal v) { this.valorFinal = v; }

    public BigDecimal getValorRecebido() { return valorRecebido; }
    public void setValorRecebido(BigDecimal v) { this.valorRecebido = v; }

    public BigDecimal getValorTroco() { return valorTroco; }
    public void setValorTroco(BigDecimal v) { this.valorTroco = v; }

    public BigDecimal getValorCancelado() { return valorCancelado; }
    public void setValorCancelado(BigDecimal v) { this.valorCancelado = v; }

    public BigDecimal getValorTotalProdutos() { return valorTotalProdutos; }
    public void setValorTotalProdutos(BigDecimal v) { this.valorTotalProdutos = v; }

    public BigDecimal getValorTotalDocumento() { return valorTotalDocumento; }
    public void setValorTotalDocumento(BigDecimal v) { this.valorTotalDocumento = v; }

    public BigDecimal getValorBaseIcms() { return valorBaseIcms; }
    public void setValorBaseIcms(BigDecimal v) { this.valorBaseIcms = v; }

    public BigDecimal getValorIcms() { return valorIcms; }
    public void setValorIcms(BigDecimal v) { this.valorIcms = v; }

    public BigDecimal getValorIcmsOutras() { return valorIcmsOutras; }
    public void setValorIcmsOutras(BigDecimal v) { this.valorIcmsOutras = v; }

    public BigDecimal getValorIssqn() { return valorIssqn; }
    public void setValorIssqn(BigDecimal v) { this.valorIssqn = v; }

    public BigDecimal getValorPis() { return valorPis; }
    public void setValorPis(BigDecimal v) { this.valorPis = v; }

    public BigDecimal getValorCofins() { return valorCofins; }
    public void setValorCofins(BigDecimal v) { this.valorCofins = v; }

    public BigDecimal getValorAcrescimoItens() { return valorAcrescimoItens; }
    public void setValorAcrescimoItens(BigDecimal v) { this.valorAcrescimoItens = v; }

    public BigDecimal getValorDescontoItens() { return valorDescontoItens; }
    public void setValorDescontoItens(BigDecimal v) { this.valorDescontoItens = v; }

    public StatusVenda getStatusVenda() { return statusVenda; }
    public void setStatusVenda(StatusVenda statusVenda) { this.statusVenda = statusVenda; }

    public SimNao getCupomCancelado() { return cupomCancelado; }
    public void setCupomCancelado(SimNao cupomCancelado) { this.cupomCancelado = cupomCancelado; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getCpfCnpjCliente() { return cpfCnpjCliente; }
    public void setCpfCnpjCliente(String cpfCnpjCliente) { this.cpfCnpjCliente = cpfCnpjCliente; }

    public String getHashRegistro() { return hashRegistro; }
    public void setHashRegistro(String hashRegistro) { this.hashRegistro = hashRegistro; }

    public String getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(String tipoOperacao) { this.tipoOperacao = tipoOperacao; }

    public String getStatusFiscal() { return statusFiscal; }
    public void setStatusFiscal(String statusFiscal) { this.statusFiscal = statusFiscal; }

    public String getChaveAcessoFiscal() { return chaveAcessoFiscal; }
    public void setChaveAcessoFiscal(String chaveAcessoFiscal) { this.chaveAcessoFiscal = chaveAcessoFiscal; }

    public String getProtocoloAutorizacao() { return protocoloAutorizacao; }
    public void setProtocoloAutorizacao(String protocoloAutorizacao) { this.protocoloAutorizacao = protocoloAutorizacao; }

    public String getMensagemFiscal() { return mensagemFiscal; }
    public void setMensagemFiscal(String mensagemFiscal) { this.mensagemFiscal = mensagemFiscal; }

    public Integer getIdGeradoDispositivo() { return idGeradoDispositivo; }
    public void setIdGeradoDispositivo(Integer idGeradoDispositivo) { this.idGeradoDispositivo = idGeradoDispositivo; }

    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String idDispositivo) { this.idDispositivo = idDispositivo; }

    public OrigemVenda getOrigemVenda() { return origemVenda; }
    public void setOrigemVenda(OrigemVenda v) { this.origemVenda = v; }

    public ContaAberta getContaAberta() { return contaAberta; }
    public void setContaAberta(ContaAberta c) { this.contaAberta = c; }

    public Integer getIdGarcom() { return idGarcom; }
    public void setIdGarcom(Integer idGarcom) { this.idGarcom = idGarcom; }
}