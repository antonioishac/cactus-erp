package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoDocumentoFiscal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Faixa inutilizada (homologada pela SEFAZ) que regulariza os números pulados pendentes dentro dela. */
public record RegularizarNumerosFiscaisPuladosRequest(
        @NotNull TipoDocumentoFiscal tipoDocumento,
        @Size(max = 3) String serie,
        @NotNull @Positive Long numeroInicial,
        @NotNull @Positive Long numeroFinal,
        @NotBlank @Size(max = 20) String protocoloInutilizacao) {
}
