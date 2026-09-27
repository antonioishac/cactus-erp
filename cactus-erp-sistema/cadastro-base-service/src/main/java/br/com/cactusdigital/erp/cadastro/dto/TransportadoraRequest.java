package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * CPF é obrigatório quando {@code tipoPessoa=FISICA}, CNPJ quando {@code JURIDICA} —
 * validado em {@code TransportadoraServiceImpl}, mesmo padrão de {@code FornecedorRequest}.
 */
public record TransportadoraRequest(
        @NotBlank @Size(max = 150) String nome,
        @NotNull TipoPessoa tipoPessoa,
        @Size(max = 11) String cpf,
        @Size(max = 20) String rg,
        LocalDate dataNascimento,
        @Size(max = 14) String cnpj,
        @Size(max = 150) String nomeFantasia,
        @Size(max = 20) String inscricaoEstadual,
        @Size(max = 20) String inscricaoMunicipal,
        @Email @Size(max = 250) String email,
        @NotEmpty @Valid List<EnderecoDTO> enderecos,
        @Valid List<ContatoDTO> contatos,
        @Valid List<TelefoneDTO> telefones,
        @Size(max = 20) String rntrc,
        @Size(max = 250) String observacao,
        @NotNull Situacao situacao) {
}
