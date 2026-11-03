package br.com.cactuserp.pdv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * XMLs da NFC-e de um cupom ({@link PdvVendaCabecalho}), 1:1 e separados do cabeçalho (dezenas de KB cada).
 *
 * <p>{@code XML_LOTE_ASSINADO} é o documento exato que foi transmitido — ou, na contingência offline, que será: a
 * NFC-e offline tem de chegar à SEFAZ idêntica ao DANFE impresso (mesma chave, mesma assinatura, mesmo QR Code).
 * {@code XML_PROCESSADO} (nfeProc) e {@code XML_CANCELAMENTO} são a guarda obrigatória de 5 anos.
 */
@Entity
@Table(name = "PDV_NFCE_XML")
public class PdvNfceXml {

    @Id
    @Column(name = "ID_PDV_VENDA_CABECALHO")
    private Long idPdvVendaCabecalho;

    @Column(name = "XML_LOTE_ASSINADO", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String xmlLoteAssinado;

    @Column(name = "XML_PROCESSADO", columnDefinition = "MEDIUMTEXT")
    private String xmlProcessado;

    @Column(name = "XML_CANCELAMENTO", columnDefinition = "MEDIUMTEXT")
    private String xmlCancelamento;

    @Column(name = "QR_CODE", length = 1000, nullable = false)
    private String qrCode;

    @Column(name = "URL_CONSULTA", length = 250)
    private String urlConsulta;

    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    public PdvNfceXml() {
    }

    public PdvNfceXml(Long idPdvVendaCabecalho, String xmlLoteAssinado, String qrCode, String urlConsulta) {
        this.idPdvVendaCabecalho = idPdvVendaCabecalho;
        this.xmlLoteAssinado = xmlLoteAssinado;
        this.qrCode = qrCode;
        this.urlConsulta = urlConsulta;
        this.dataCriacao = LocalDateTime.now();
    }

    /** Novo documento para o mesmo cupom (ex.: rejeitada corrigida e reassinada). Descarta o processado anterior. */
    public void substituirDocumento(String xmlLoteAssinado, String qrCode, String urlConsulta) {
        this.xmlLoteAssinado = xmlLoteAssinado;
        this.qrCode = qrCode;
        this.urlConsulta = urlConsulta;
        this.xmlProcessado = null;
    }

    public Long getIdPdvVendaCabecalho() { return idPdvVendaCabecalho; }
    public String getXmlLoteAssinado() { return xmlLoteAssinado; }
    public String getXmlProcessado() { return xmlProcessado; }
    public void setXmlProcessado(String xmlProcessado) { this.xmlProcessado = xmlProcessado; }
    public String getXmlCancelamento() { return xmlCancelamento; }
    public void setXmlCancelamento(String xmlCancelamento) { this.xmlCancelamento = xmlCancelamento; }
    public String getQrCode() { return qrCode; }
    public String getUrlConsulta() { return urlConsulta; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}
