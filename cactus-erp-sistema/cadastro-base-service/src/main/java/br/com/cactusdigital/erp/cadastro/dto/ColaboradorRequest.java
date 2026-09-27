package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.entity.TipoVeiculoEntregador;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * CPF é obrigatório quando {@code tipoPessoa=FISICA} (caso normal — colaborador quase
 * sempre é PF), CNPJ quando {@code JURIDICA} (colaborador PJ, ex: prestador de serviço
 * terceirizado) — mesma regra do {@code ClienteRequest}.
 */
public record ColaboradorRequest(
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
        Long cargoId,
        Long setorId,
        @Size(max = 10) String matricula,
        LocalDate dataAdmissao,
        LocalDate dataDemissao,
        @Size(max = 10) String ctpsNumero,
        @Size(max = 10) String ctpsSerie,
        LocalDate ctpsDataExpedicao,
        @Size(min = 2, max = 2) String ctpsUf,
        BigDecimal comissaoVista,
        BigDecimal comissaoPrazo,
        TipoVeiculoEntregador entregadorVeiculo,
        @Size(max = 250) String observacao,
        @NotNull Situacao situacao) {
}
