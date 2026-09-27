package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Espelha o subconjunto do {@code VendaResponse} (vendas-service) necessário pra montar
 * uma NF-e — cópia deliberada, não uma dependência Java entre os dois serviços (limite de
 * serviço). Campos extras da resposta real são ignorados pelo Jackson (comportamento
 * padrão do Spring Boot, {@code FAIL_ON_UNKNOWN_PROPERTIES=false}).
 */
public record VendaClienteDTO(
        Long id,
        Long empresaId,
        Long clienteId,
        Long transportadoraId,
        Long formaPagamentoId,
        LocalDate dataVenda,
        BigDecimal valorSubtotal,
        BigDecimal valorDesconto,
        BigDecimal valorTotal,
        String situacao,
        List<VendaItemClienteDTO> itens) {
}
