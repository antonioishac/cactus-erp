package br.com.cactusdigital.erp.fiscal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * XML processado ({@code nfeProc}) de uma NF-e autorizada ou denegada. Separado de {@link NotaFiscalEletronica} de
 * propósito: são dezenas de KB por nota, e a listagem paginada nunca deve carregá-los. Imutável: o XML autorizado é o
 * documento fiscal de fato, não se edita.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "nota_fiscal_eletronica_xml")
public class NotaFiscalEletronicaXml {

    @Id
    @Column(name = "nota_fiscal_id")
    private Long notaFiscalId;

    @Column(name = "xml_processado", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String xmlProcessado;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private Instant dataCriacao;

    public NotaFiscalEletronicaXml(Long notaFiscalId, String xmlProcessado) {
        this.notaFiscalId = notaFiscalId;
        this.xmlProcessado = xmlProcessado;
        this.dataCriacao = Instant.now();
    }
}
