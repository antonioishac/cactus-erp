package br.com.cactuserp.pdv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Número de NFC-e que não ficou com o cupom e precisa ser regularizado na SEFAZ:
 * <ul>
 *   <li>transmitido sem resposta (timeout) e substituído por uma NFC-e em contingência offline com outro número — se a
 *       SEFAZ autorizou a tentativa, ela é cancelada por substituição (110112, referenciando a substituta); se não,
 *       o número é inutilizado;</li>
 *   <li>reservado e abandonado (falha antes de assinar, venda cancelada antes da emissão).</li>
 * </ul>
 * Resolvido por {@code RegularizacaoNumeracaoNfceService}. Registro de auditoria: nunca se apaga.
 */
@Entity
@Table(name = "PDV_NFCE_NUMERO_PENDENTE")
public class PdvNfceNumeroPendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ID_PDV_CAIXA", nullable = false)
    private Long idPdvCaixa;

    /** Cupom que ficou com outro número (a NFC-e substituta); nulo se o número foi só abandonado. */
    @Column(name = "ID_PDV_VENDA_CABECALHO")
    private Long idPdvVendaCabecalho;

    @Column(name = "AMBIENTE", length = 1, columnDefinition = "CHAR(1)", nullable = false)
    private String ambiente;

    @Column(name = "SERIE", nullable = false)
    private Integer serie;

    @Column(name = "NUMERO", nullable = false)
    private Long numero;

    /** Chave da tentativa sem resposta; nula = o número nunca chegou a ser transmitido. */
    @Column(name = "CHAVE_ACESSO", length = 44)
    private String chaveAcesso;

    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO", length = 1, columnDefinition = "CHAR(1)", nullable = false)
    private SituacaoNumeroPendente situacao = SituacaoNumeroPendente.V;

    @Column(name = "PROTOCOLO", length = 30)
    private String protocolo;

    @Column(name = "MENSAGEM", length = 500)
    private String mensagem;

    @Column(name = "TENTATIVAS", nullable = false)
    private Integer tentativas = 0;

    @Column(name = "DATA_REGISTRO", nullable = false)
    private LocalDateTime dataRegistro;

    @Column(name = "DATA_RESOLUCAO")
    private LocalDateTime dataResolucao;

    public PdvNfceNumeroPendente() {
    }

    public PdvNfceNumeroPendente(Long idPdvCaixa, Long idPdvVendaCabecalho, String ambiente, Integer serie, Long numero,
                                 String chaveAcesso, String motivo) {
        this.idPdvCaixa = idPdvCaixa;
        this.idPdvVendaCabecalho = idPdvVendaCabecalho;
        this.ambiente = ambiente;
        this.serie = serie;
        this.numero = numero;
        this.chaveAcesso = chaveAcesso;
        this.mensagem = truncar(motivo);
        this.dataRegistro = LocalDateTime.now();
    }

    public void resolver(SituacaoNumeroPendente situacao, String protocolo, String mensagem) {
        this.situacao = situacao;
        this.protocolo = protocolo;
        this.mensagem = truncar(mensagem);
        this.dataResolucao = LocalDateTime.now();
    }

    /** Nova tentativa sem sucesso: continua {@code V} (ou vira {@code E} se {@code desistir}). */
    public void registrarTentativa(String mensagem, boolean desistir) {
        this.tentativas = this.tentativas + 1;
        this.mensagem = truncar(mensagem);
        if (desistir) {
            this.situacao = SituacaoNumeroPendente.E;
        }
    }

    private static String truncar(String texto) {
        return texto == null || texto.length() <= 500 ? texto : texto.substring(0, 500);
    }

    public Long getId() { return id; }
    public Long getIdPdvCaixa() { return idPdvCaixa; }
    public Long getIdPdvVendaCabecalho() { return idPdvVendaCabecalho; }
    public String getAmbiente() { return ambiente; }
    public Integer getSerie() { return serie; }
    public Long getNumero() { return numero; }
    public String getChaveAcesso() { return chaveAcesso; }
    public SituacaoNumeroPendente getSituacao() { return situacao; }
    public String getProtocolo() { return protocolo; }
    public String getMensagem() { return mensagem; }
    public Integer getTentativas() { return tentativas; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public LocalDateTime getDataResolucao() { return dataResolucao; }
}
