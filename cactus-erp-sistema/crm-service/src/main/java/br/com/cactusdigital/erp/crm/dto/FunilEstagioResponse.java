package br.com.cactusdigital.erp.crm.dto;

import java.math.BigDecimal;
import java.util.List;

/** Um "coluna" do Kanban de vendas — estágio + oportunidades ABERTAS nele + soma do valor estimado (visão rápida de saúde do funil). */
public record FunilEstagioResponse(
        Long estagioFunilId,
        String estagioFunilNome,
        Integer ordem,
        BigDecimal valorEstimadoTotal,
        List<OportunidadeResponse> oportunidades) {
}
