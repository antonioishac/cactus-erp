package br.com.cactusdigital.erp.crm.dto;

import br.com.cactusdigital.erp.crm.entity.OrigemLead;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeadRequest(
        @NotBlank @Size(max = 120) String nome,
        @Size(max = 120) String nomeEmpresa,
        @Email @Size(max = 120) String email,
        @Size(max = 20) String telefone,
        OrigemLead origem,
        Long vendedorId,
        @Size(max = 500) String observacao) {
}
