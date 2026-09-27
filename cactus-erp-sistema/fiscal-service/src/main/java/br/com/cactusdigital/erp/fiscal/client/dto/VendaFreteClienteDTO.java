package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;

/**
 * Espelha o subconjunto do {@code VendaFreteResponse} (vendas-service) necessário pra
 * compor um CT-e — cópia deliberada, não uma dependência Java entre os dois serviços
 * (limite de serviço, mesmo padrão de {@code VendaClienteDTO}).
 */
public record VendaFreteClienteDTO(
        Long id,
        Long vendaId,
        Long transportadoraId,
        String placa,
        String ufPlaca,
        BigDecimal quantidadeVolume,
        BigDecimal pesoBruto) {
}
