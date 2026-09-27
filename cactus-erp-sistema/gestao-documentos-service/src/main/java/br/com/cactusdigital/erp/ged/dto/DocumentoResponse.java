package br.com.cactusdigital.erp.ged.dto;

import br.com.cactusdigital.erp.common.domain.Situacao;
import java.time.LocalDate;
import java.util.List;

public record DocumentoResponse(
        Long id,
        Long pastaId,
        String pastaNome,
        Long tipoDocumentoId,
        String tipoDocumentoNome,
        String nome,
        String descricao,
        String palavrasChave,
        String entidadeTipo,
        Long entidadeId,
        boolean permiteExclusao,
        boolean permiteAlteracao,
        boolean assinado,
        LocalDate dataFimVigencia,
        Situacao situacao,
        List<DocumentoVersaoResponse> versoes) {
}
