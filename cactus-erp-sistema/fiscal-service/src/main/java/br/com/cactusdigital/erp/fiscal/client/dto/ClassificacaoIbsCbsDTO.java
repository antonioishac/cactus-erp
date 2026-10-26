package br.com.cactusdigital.erp.fiscal.client.dto;

import java.math.BigDecimal;

/** Espelha {@code ClassificacaoTributariaIbsCbsResponse} do cadastro-base-service (limite de serviço). */
public record ClassificacaoIbsCbsDTO(
        String codigo,
        String cst,
        boolean exigeGrupoTributacao,
        boolean reducaoAliquota,
        BigDecimal percentualReducaoIbs,
        BigDecimal percentualReducaoCbs,
        String motivoNaoSuportada) {
}
