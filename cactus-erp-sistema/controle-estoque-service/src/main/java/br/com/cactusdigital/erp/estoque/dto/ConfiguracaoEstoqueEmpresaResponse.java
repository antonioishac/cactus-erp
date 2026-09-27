package br.com.cactusdigital.erp.estoque.dto;

import br.com.cactusdigital.erp.estoque.entity.MetodoCusteio;
import java.math.BigDecimal;

public record ConfiguracaoEstoqueEmpresaResponse(
        Long id,
        Long empresaId,
        MetodoCusteio metodoCusteio,
        BigDecimal limiteAprovacaoAjuste) {
}
