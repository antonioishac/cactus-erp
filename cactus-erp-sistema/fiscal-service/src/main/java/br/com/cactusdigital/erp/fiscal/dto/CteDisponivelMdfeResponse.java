package br.com.cactusdigital.erp.fiscal.dto;

import java.math.BigDecimal;

/**
 * CT-e que pode entrar num MDF-e novo: {@code AUTORIZADA}, da empresa informada e fora de
 * qualquer MDF-e não cancelado. Projeção enxuta pro seletor da tela de geração — o MDF-e só
 * precisa saber qual CT-e escolher, o resto (carga, peso, destinatário) o backend resolve.
 */
public record CteDisponivelMdfeResponse(
        Long id,
        String serie,
        Long numero,
        Long vendaId,
        BigDecimal valorTotalCarga,
        BigDecimal pesoBruto) {
}
