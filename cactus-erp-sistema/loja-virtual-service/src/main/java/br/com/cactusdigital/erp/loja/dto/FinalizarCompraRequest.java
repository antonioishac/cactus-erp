package br.com.cactusdigital.erp.loja.dto;

import br.com.cactusdigital.erp.loja.entity.FormaPagamentoLoja;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Guest checkout — captura a identidade do comprador inline, sem exigir conta prévia (ver Javadoc de {@code ClienteLoja}). */
public record FinalizarCompraRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 120) String email,
        @Size(max = 20) String telefone,
        @Size(max = 11) String cpf,
        @Size(max = 150) String logradouroEntrega,
        @Size(max = 20) String numeroEntrega,
        @Size(max = 60) String complementoEntrega,
        @Size(max = 80) String bairroEntrega,
        @Size(max = 8) String cepEntrega,
        @Size(max = 80) String municipioEntrega,
        @Size(max = 2) String ufEntrega,
        @NotNull FormaPagamentoLoja formaPagamento) {
}
