package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record FornecedorResponse(
        Long id,
        Long pessoaId,
        String nome,
        TipoPessoa tipoPessoa,
        String documento,
        String rg,
        LocalDate dataNascimento,
        String nomeFantasia,
        String inscricaoEstadual,
        String inscricaoMunicipal,
        String email,
        List<EnderecoDTO> enderecos,
        List<ContatoDTO> contatos,
        List<TelefoneDTO> telefones,
        LocalDate fornecedorDesde,
        Integer prazoEntregaDias,
        String observacao,
        LocalDate dataCadastro,
        Situacao situacao,
        Instant dataCriacao,
        Instant dataAtualizacao) {
}
