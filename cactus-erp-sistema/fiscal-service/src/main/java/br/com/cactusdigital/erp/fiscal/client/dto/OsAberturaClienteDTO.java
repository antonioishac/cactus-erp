package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;
import java.util.List;

/** Espelha o subconjunto do {@code OsAberturaResponse} (os-service) necessário pra montar uma NFS-e — cópia deliberada, limite de serviço. */
public record OsAberturaClienteDTO(
        Long id,
        Long empresaId,
        Long clienteId,
        boolean orcamentoAprovado,
        BigDecimal valorTotal,
        List<OsItemClienteDTO> itens) {
}
