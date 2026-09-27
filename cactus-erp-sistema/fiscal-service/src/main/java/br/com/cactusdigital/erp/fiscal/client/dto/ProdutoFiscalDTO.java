package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;

/** Espelha o subconjunto do {@code ProdutoResponse} (cadastro-base-service) necessário pra montar um item de NF-e — usa o "resolvido padrão" já cacheado (Fase 1b), não reinvoca o motor tributário. */
public record ProdutoFiscalDTO(
        Long id,
        String gtin,
        String codigoInterno,
        String nome,
        String descricaoPdv,
        String ncmCodigo,
        String produtoUnidadeSigla,
        String cstIcmsPadrao,
        String csosnPadrao,
        BigDecimal aliquotaIcmsPadrao,
        BigDecimal aliquotaIpiPadrao,
        BigDecimal aliquotaPisPadrao,
        BigDecimal aliquotaCofinsPadrao) {
}
