package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.FidelidadeAviso;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ClienteResponse(
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
        String url,
        List<EnderecoDTO> enderecos,
        List<ContatoDTO> contatos,
        List<TelefoneDTO> telefones,
        BigDecimal limiteCredito,
        BigDecimal taxaDesconto,
        FidelidadeAviso fidelidadeAviso,
        Integer fidelidadeQuantidade,
        BigDecimal fidelidadeValor,
        LocalDate dataCadastro,
        Situacao situacao,
        Instant dataCriacao,
        Instant dataAtualizacao) {
}
