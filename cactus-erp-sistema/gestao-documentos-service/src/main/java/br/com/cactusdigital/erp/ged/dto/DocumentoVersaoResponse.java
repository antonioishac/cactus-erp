package br.com.cactusdigital.erp.ged.dto;

import br.com.cactusdigital.erp.ged.entity.TipoAcaoVersao;
import java.time.Instant;

/** {@code chaveArmazenamento} deliberadamente não exposto — é detalhe interno de
 * {@code DocumentoStorageService}, nunca vaza pra fora da API. */
public record DocumentoVersaoResponse(
        Long id,
        Long documentoId,
        Long usuarioId,
        Integer numeroVersao,
        TipoAcaoVersao tipoAcao,
        String hashArquivo,
        String nomeArquivoOriginal,
        Long tamanhoBytes,
        String contentType,
        String observacao,
        Instant dataCriacao) {
}
