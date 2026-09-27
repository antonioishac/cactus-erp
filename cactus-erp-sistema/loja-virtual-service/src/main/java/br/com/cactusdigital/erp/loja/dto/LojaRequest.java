package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.loja.entity.PlanoAssinatura;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LojaRequest(
        @NotBlank @Size(max = 120) String nome,
        @Size(max = 150) String razaoSocial,
        @Size(max = 14) String cnpj,
        /** FK solta pro Empresa do cadastro-base-service — necessária pra emitir NFC-e dos pedidos desta loja, ver Javadoc de {@code Loja}. */
        Long empresaId,
        @NotBlank @Size(max = 60) @Pattern(regexp = "^[a-z0-9-]+$", message = "deve conter só letras minúsculas, números e hífen") String subdominio,
        @Email @Size(max = 120) String emailContato,
        @Size(max = 20) String telefone,
        PlanoAssinatura planoAssinatura) {
}
