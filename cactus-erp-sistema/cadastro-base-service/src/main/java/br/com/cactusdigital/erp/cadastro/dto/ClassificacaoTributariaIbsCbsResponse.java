package br.com.cactusdigital.erp.cadastro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** {@code motivoNaoSuportada} nulo = pode ser usada numa configuração tributária. */
public record ClassificacaoTributariaIbsCbsResponse(
        String codigo,
        String cst,
        boolean exigeGrupoTributacao,
        boolean reducaoAliquota,
        String descricao,
        String nomeReduzido,
        BigDecimal percentualReducaoIbs,
        BigDecimal percentualReducaoCbs,
        boolean tributacaoRegular,
        boolean aceitaNfe,
        boolean aceitaNfce,
        String urlLegislacao,
        LocalDate inicioVigencia,
        LocalDate fimVigencia,
        String motivoNaoSuportada) {
}
