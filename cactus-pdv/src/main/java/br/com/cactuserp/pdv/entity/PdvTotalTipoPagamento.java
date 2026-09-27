package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Pagamento individual lançado no encerramento de uma venda.
 *
 * <p>Mapeia a tabela {@code PDV_TOTAL_TIPO_PAGAMENTO}. Cada linha aqui
 * representa UMA entrada de pagamento que o operador adicionou na tela
 * de encerramento — se o cliente paga R$ 100 em dinheiro + R$ 50 em
 * cartão, gera 2 registros distintos.
 *
 * <p>A relação com o cupom é feita por {@code ID_PDV_VENDA_CABECALHO}
 * (campo simples Long, não @ManyToOne) por dois motivos:
 * <ul>
 *   <li><b>Performance</b>: evita carregar o cabeçalho inteiro quando
 *       precisamos só do ID na auditoria.</li>
 *   <li><b>Imutabilidade fiscal</b>: o pagamento, uma vez gravado, é
 *       um snapshot — não precisa de proxy para o cupom.</li>
 * </ul>
 *
 * <p>O {@code HASH_REGISTRO} é calculado pelo {@code VendaService} antes
 * de persistir, igual aos outros registros fiscais.
 */
@Entity
@Table(name = "PDV_TOTAL_TIPO_PAGAMENTO")
public class PdvTotalTipoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ID_PDV_VENDA_CABECALHO", nullable = false)
    private Long idPdvVendaCabecalho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PDV_TIPO_PAGAMENTO", nullable = false)
    private TipoPagamento pdvTipoPagamento;

    @Column(name = "DATA_VENDA")
    private LocalDate dataVenda;

    @Column(name = "HORA_VENDA", length = 8)
    private String horaVenda;

    @Column(name = "COO")
    private Integer coo;

    @Column(name = "CCF")
    private Integer ccf;

    @Column(name = "GNF")
    private Integer gnf;

    @Column(name = "VALOR", precision = 18, scale = 6)
    private BigDecimal valor;

    /** Número Sequencial Único — usado quando o pagamento foi por TEF. */
    @Column(name = "NSU", length = 30)
    private String nsu;

    /** S=Sim | N=Não — flag de estorno (uso futuro em TEF). */
    @Column(name = "ESTORNO", length = 1, columnDefinition = "CHAR(1)")
    private String estorno;

    @Column(name = "REDE", length = 10)
    private String rede;

    /** D=Débito | C=Crédito — quando o pagamento é por cartão. */
    @Column(name = "CARTAO_DC", length = 1, columnDefinition = "CHAR(1)")
    private String cartaoDc;

    /** SHA-256 de integridade. Calculado pelo VendaService antes de salvar. */
    @Column(name = "HASH_REGISTRO", length = 64)
    private String hashRegistro;

    @Column(name = "ID_GERADO_DISPOSITIVO")
    private Integer idGeradoDispositivo;

    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    // ====== Construtores ===============================================
    public PdvTotalTipoPagamento() {}

    // ====== Getters / Setters ==========================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdPdvVendaCabecalho() { return idPdvVendaCabecalho; }
    public void setIdPdvVendaCabecalho(Long v) { this.idPdvVendaCabecalho = v; }

    public TipoPagamento getPdvTipoPagamento() { return pdvTipoPagamento; }
    public void setPdvTipoPagamento(TipoPagamento v) { this.pdvTipoPagamento = v; }

    public LocalDate getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDate v) { this.dataVenda = v; }

    public String getHoraVenda() { return horaVenda; }
    public void setHoraVenda(String v) { this.horaVenda = v; }

    public Integer getCoo() { return coo; }
    public void setCoo(Integer coo) { this.coo = coo; }

    public Integer getCcf() { return ccf; }
    public void setCcf(Integer ccf) { this.ccf = ccf; }

    public Integer getGnf() { return gnf; }
    public void setGnf(Integer gnf) { this.gnf = gnf; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal v) { this.valor = v; }

    public String getNsu() { return nsu; }
    public void setNsu(String nsu) { this.nsu = nsu; }

    public String getEstorno() { return estorno; }
    public void setEstorno(String v) { this.estorno = v; }

    public String getRede() { return rede; }
    public void setRede(String rede) { this.rede = rede; }

    public String getCartaoDc() { return cartaoDc; }
    public void setCartaoDc(String v) { this.cartaoDc = v; }

    public String getHashRegistro() { return hashRegistro; }
    public void setHashRegistro(String v) { this.hashRegistro = v; }

    public Integer getIdGeradoDispositivo() { return idGeradoDispositivo; }
    public void setIdGeradoDispositivo(Integer v) { this.idGeradoDispositivo = v; }

    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String v) { this.idDispositivo = v; }
}