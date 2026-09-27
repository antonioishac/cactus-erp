package br.com.cactusdigital.erp.common.report;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Monta a resposta HTTP de download de um relatório já exportado por {@link ReportEngine}
 * — {@code Content-Type}/{@code Content-Disposition} corretos por formato, mesmo padrão
 * usado pra download de documento no {@code gestao-documentos-service} (GED).
 */
public final class ReportHttpSupport {

    private ReportHttpSupport() {
    }

    /** @param nomeArquivoSemExtensao ex: {@code "curva-abc"} — a extensão é adicionada a partir do {@link ReportFormat}. */
    public static ResponseEntity<byte[]> download(byte[] conteudo, ReportFormat formato, String nomeArquivoSemExtensao) {
        String nomeArquivo = nomeArquivoSemExtensao + "." + formato.extensao();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(nomeArquivo).build());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(formato.contentType()))
                .body(conteudo);
    }
}
