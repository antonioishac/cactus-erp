package br.com.cactusdigital.erp.ged.dto;

import org.springframework.core.io.Resource;

/** Empacota o {@link Resource} de conteúdo junto do nome/tipo original do arquivo — o
 * controller usa isso pra montar os headers HTTP de download (Content-Disposition/
 * Content-Type), sem o service precisar conhecer nada de HTTP. */
public record ConteudoDocumentoResponse(Resource conteudo, String nomeArquivoOriginal, String contentType) {
}
