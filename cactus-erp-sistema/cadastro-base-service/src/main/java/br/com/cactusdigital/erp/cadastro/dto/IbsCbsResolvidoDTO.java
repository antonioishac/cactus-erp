package br.com.cactusdigital.erp.cadastro.dto;

import java.math.BigDecimal;

/**
 * IBS/CBS resolvido para a célula: o que o emissor precisa para montar o grupo IBSCBS do item.
 * {@code exigeGrupoTributacao=false} (isenção/imunidade) = informa só CST e cClassTrib, sem valores.
 */
public record IbsCbsResolvidoDTO(
        String cst,
        String classificacaoTributaria,
        boolean exigeGrupoTributacao,
        boolean reducaoAliquota,
        BigDecimal percentualReducaoIbs,
        BigDecimal percentualReducaoCbs) {
}
