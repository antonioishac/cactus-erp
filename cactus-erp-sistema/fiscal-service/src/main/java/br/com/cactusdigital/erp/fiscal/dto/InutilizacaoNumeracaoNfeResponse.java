package br.com.cactusdigital.erp.fiscal.dto;

import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.SituacaoInutilizacao;
import java.time.Instant;

/** Sem o XML (baixado à parte, só quando homologada). */
public record InutilizacaoNumeracaoNfeResponse(
        Long id,
        Long empresaId,
        String modelo,
        String serie,
        Long numeroInicial,
        Long numeroFinal,
        Short ano,
        AmbienteFiscal ambiente,
        String justificativa,
        SituacaoInutilizacao situacao,
        String protocolo,
        String codigoStatusSefaz,
        String mensagemSefaz,
        Instant dataHomologacao,
        Instant dataCriacao) {
}
