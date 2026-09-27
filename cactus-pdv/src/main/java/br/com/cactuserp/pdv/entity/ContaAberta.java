package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Conta aberta — agregado unificado para mesa, comanda ou pedido de delivery.
 *
 * <p>O campo {@link #tipo} discrimina qual conjunto de campos é obrigatório
 * (mesa via {@code idMesa}, comanda via {@code codigoComanda}, delivery
 * via campos de endereço).
 *
 * <h3>Ciclo de vida</h3>
 * <pre>
 *  A → E → F   (caminho normal: aberta → em atendimento no PDV → fechada/paga)
 *      → X    (cancelamento permitido até a finalização)
 * </pre>
 *
 * <p>Quando a conta é fechada com sucesso, {@link #idPdvVendaCabecalho}
 * aponta para o cupom gerado.
 */
@Entity
@Table(name = "CONTA_ABERTA")
public class ContaAberta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private TipoContaAberta tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private StatusContaAberta status;

    // ===== Vínculos por tipo =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MESA")
    private Mesa mesa;

    @Column(name = "CODIGO_COMANDA", length = 20)
    private String codigoComanda;

    @Column(name = "NUMERO_PEDIDO", length = 20)
    private String numeroPedido;

    // ===== Cliente =====
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private Cliente cliente;

    @Column(name = "NOME_CLIENTE_AVULSO", length = 100)
    private String nomeClienteAvulso;

    // ===== Operadores =====
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_OPERADOR_ABERTURA", nullable = false)
    private PdvOperador operadorAbertura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_OPERADOR_FECHAMENTO")
    private PdvOperador operadorFechamento;

    // ===== Datas / horas =====
    @Column(name = "DATA_ABERTURA", nullable = false)
    private LocalDate dataAbertura;

    @Column(name = "HORA_ABERTURA", length = 8, nullable = false)
    private String horaAbertura;

    @Column(name = "DATA_FECHAMENTO")
    private LocalDate dataFechamento;

    @Column(name = "HORA_FECHAMENTO", length = 8)
    private String horaFechamento;

    // ===== Totais (cache, recalculados a partir dos itens) =====
    @Column(name = "VALOR_TOTAL", precision = 18, scale = 6, nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "VALOR_DESCONTO", precision = 18, scale = 6, nullable = false)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(name = "VALOR_ACRESCIMO", precision = 18, scale = 6, nullable = false)
    private BigDecimal valorAcrescimo = BigDecimal.ZERO;

    @Column(name = "VALOR_TAXA_ENTREGA", precision = 18, scale = 6, nullable = false)
    private BigDecimal valorTaxaEntrega = BigDecimal.ZERO;

    // ===== Vínculo com a venda quando fechada =====
    @Column(name = "ID_PDV_VENDA_CABECALHO")
    private Long idPdvVendaCabecalho;

    // ===== Delivery (snapshot do endereço) =====
    @Column(name = "ENDERECO_ENTREGA", length = 200)
    private String enderecoEntrega;

    @Column(name = "NUMERO_ENTREGA", length = 20)
    private String numeroEntrega;

    @Column(name = "COMPLEMENTO_ENTREGA", length = 100)
    private String complementoEntrega;

    @Column(name = "BAIRRO_ENTREGA", length = 80)
    private String bairroEntrega;

    @Column(name = "CIDADE_ENTREGA", length = 80)
    private String cidadeEntrega;

    @Column(name = "UF_ENTREGA", length = 2)
    private String ufEntrega;

    @Column(name = "CEP_ENTREGA", length = 9)
    private String cepEntrega;

    @Column(name = "TELEFONE_CONTATO", length = 20)
    private String telefoneContato;

    @Column(name = "OBSERVACAO_ENTREGA", length = 255)
    private String observacaoEntrega;

    // ===== Observação geral =====
    @Column(name = "OBSERVACAO", length = 500)
    private String observacao;

    // ===== Itens =====
    @OneToMany(mappedBy = "contaAberta",
            cascade = CascadeType.ALL,
            orphanRemoval = false,
            fetch = FetchType.LAZY)
    private List<ContaAbertaItem> itens = new ArrayList<>();

    public ContaAberta() {}

    /** Adiciona um item e configura o relacionamento inverso. */
    public void adicionarItem(ContaAbertaItem item) {
        itens.add(item);
        item.setContaAberta(this);
    }

    /** True quando a conta ainda pode receber itens. */
    public boolean estaAberta() {
        return status == StatusContaAberta.A;
    }

    // ====== Getters / Setters ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoContaAberta getTipo() { return tipo; }
    public void setTipo(TipoContaAberta tipo) { this.tipo = tipo; }

    public StatusContaAberta getStatus() { return status; }
    public void setStatus(StatusContaAberta status) { this.status = status; }

    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }

    public String getCodigoComanda() { return codigoComanda; }
    public void setCodigoComanda(String v) { this.codigoComanda = v; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String v) { this.numeroPedido = v; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getNomeClienteAvulso() { return nomeClienteAvulso; }
    public void setNomeClienteAvulso(String v) { this.nomeClienteAvulso = v; }

    public PdvOperador getOperadorAbertura() { return operadorAbertura; }
    public void setOperadorAbertura(PdvOperador v) { this.operadorAbertura = v; }

    public PdvOperador getOperadorFechamento() { return operadorFechamento; }
    public void setOperadorFechamento(PdvOperador v) { this.operadorFechamento = v; }

    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate v) { this.dataAbertura = v; }

    public String getHoraAbertura() { return horaAbertura; }
    public void setHoraAbertura(String v) { this.horaAbertura = v; }

    public LocalDate getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDate v) { this.dataFechamento = v; }

    public String getHoraFechamento() { return horaFechamento; }
    public void setHoraFechamento(String v) { this.horaFechamento = v; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal v) { this.valorTotal = v; }

    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal v) { this.valorDesconto = v; }

    public BigDecimal getValorAcrescimo() { return valorAcrescimo; }
    public void setValorAcrescimo(BigDecimal v) { this.valorAcrescimo = v; }

    public BigDecimal getValorTaxaEntrega() { return valorTaxaEntrega; }
    public void setValorTaxaEntrega(BigDecimal v) { this.valorTaxaEntrega = v; }

    public Long getIdPdvVendaCabecalho() { return idPdvVendaCabecalho; }
    public void setIdPdvVendaCabecalho(Long v) { this.idPdvVendaCabecalho = v; }

    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String v) { this.enderecoEntrega = v; }

    public String getNumeroEntrega() { return numeroEntrega; }
    public void setNumeroEntrega(String v) { this.numeroEntrega = v; }

    public String getComplementoEntrega() { return complementoEntrega; }
    public void setComplementoEntrega(String v) { this.complementoEntrega = v; }

    public String getBairroEntrega() { return bairroEntrega; }
    public void setBairroEntrega(String v) { this.bairroEntrega = v; }

    public String getCidadeEntrega() { return cidadeEntrega; }
    public void setCidadeEntrega(String v) { this.cidadeEntrega = v; }

    public String getUfEntrega() { return ufEntrega; }
    public void setUfEntrega(String v) { this.ufEntrega = v; }

    public String getCepEntrega() { return cepEntrega; }
    public void setCepEntrega(String v) { this.cepEntrega = v; }

    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String v) { this.telefoneContato = v; }

    public String getObservacaoEntrega() { return observacaoEntrega; }
    public void setObservacaoEntrega(String v) { this.observacaoEntrega = v; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public List<ContaAbertaItem> getItens() { return itens; }
    public void setItens(List<ContaAbertaItem> itens) { this.itens = itens; }
}