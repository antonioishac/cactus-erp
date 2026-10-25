package br.com.cactusdigital.erp.fiscal.dto;

public record MdfeDocumentoVinculadoResponse(
        Long id,
        Long cteCabecalhoId,
        String cteSerie,
        Long cteNumero,
        String cteChaveAcesso,
        Integer municipioDescargaCodigoIbge,
        String municipioDescargaNome) {
}
