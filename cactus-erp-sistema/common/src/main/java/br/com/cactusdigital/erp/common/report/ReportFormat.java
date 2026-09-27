package br.com.cactusdigital.erp.common.report;

import org.springframework.http.MediaType;

/**
 * Formato de saída suportado por {@link ReportEngine} — ver ANÁLISE: MÓDULO DE
 * RELATÓRIOS no CLAUDE.md raiz. PDF cobre impressão/arquivamento/apresentação formal;
 * XLSX cobre análise/manipulação pelo usuário final (filtro, tabela dinâmica). JSON
 * continua sendo servido pelos endpoints já existentes de cada módulo, sem passar por
 * aqui — este enum só cobre os dois formatos de arquivo novos.
 */
public enum ReportFormat {

    PDF("pdf", MediaType.APPLICATION_PDF_VALUE),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String extensao;
    private final String contentType;

    ReportFormat(String extensao, String contentType) {
        this.extensao = extensao;
        this.contentType = contentType;
    }

    public String extensao() {
        return extensao;
    }

    public String contentType() {
        return contentType;
    }
}
