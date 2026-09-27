package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;

/** Espelha o subconjunto do {@code OsProdutoServicoResponse} (os-service). {@code tipo} chega como String ("PRODUTO"/"SERVICO") — só os itens SERVICO entram na NFS-e, ver {@code NotaFiscalServicoService}. */
public record OsItemClienteDTO(
        Long id,
        String tipo,
        Long produtoId,
        String complemento,
        BigDecimal valorTotal) {
}
