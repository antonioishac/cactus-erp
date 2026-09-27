package br.com.cactusdigital.erp.crm.dto;

import br.com.cactusdigital.erp.crm.entity.OrigemLead;
import br.com.cactusdigital.erp.crm.entity.SituacaoLead;

public record LeadResponse(
        Long id,
        String nome,
        String nomeEmpresa,
        String email,
        String telefone,
        OrigemLead origem,
        Long vendedorId,
        Long clienteId,
        SituacaoLead situacao,
        String motivoDesqualificacao,
        String observacao) {
}
