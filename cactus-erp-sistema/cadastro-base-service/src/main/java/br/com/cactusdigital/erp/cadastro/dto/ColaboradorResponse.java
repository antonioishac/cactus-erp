package br.com.cactusdigital.erp.cadastro.dto;

import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.entity.TipoVeiculoEntregador;
import br.com.cactusdigital.erp.common.domain.Situacao;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ColaboradorResponse(
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
        Long cargoId,
        String cargoNome,
        Long setorId,
        String setorNome,
        String matricula,
        LocalDate dataAdmissao,
        LocalDate dataDemissao,
        String ctpsNumero,
        String ctpsSerie,
        LocalDate ctpsDataExpedicao,
        String ctpsUf,
        BigDecimal comissaoVista,
        BigDecimal comissaoPrazo,
        TipoVeiculoEntregador entregadorVeiculo,
        String observacao,
        Situacao situacao,
        Instant dataCriacao,
        Instant dataAtualizacao) {
}
