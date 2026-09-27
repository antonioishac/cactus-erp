package br.com.cactusdigital.erp.cadastro.mapper;

import br.com.cactusdigital.erp.cadastro.dto.ColaboradorResponse;
import br.com.cactusdigital.erp.cadastro.entity.Colaborador;
import br.com.cactusdigital.erp.cadastro.entity.Pessoa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Padrão idêntico ao {@code ClienteMapper}: ver lá o porquê dos métodos default (Pessoa/PF/PJ). */
@Mapper(componentModel = "spring")
public interface ColaboradorMapper {

    @Mapping(target = "pessoaId", source = "pessoa.id")
    @Mapping(target = "nome", source = "pessoa.nome")
    @Mapping(target = "tipoPessoa", source = "pessoa.tipoPessoa")
    @Mapping(target = "email", source = "pessoa.email")
    @Mapping(target = "documento", expression = "java(colaborador.getPessoa().getDocumento())")
    @Mapping(target = "rg", expression = "java(rg(colaborador.getPessoa()))")
    @Mapping(target = "dataNascimento", expression = "java(dataNascimento(colaborador.getPessoa()))")
    @Mapping(target = "nomeFantasia", expression = "java(nomeFantasia(colaborador.getPessoa()))")
    @Mapping(target = "inscricaoEstadual", expression = "java(inscricaoEstadual(colaborador.getPessoa()))")
    @Mapping(target = "inscricaoMunicipal", expression = "java(inscricaoMunicipal(colaborador.getPessoa()))")
    @Mapping(target = "enderecos", source = "pessoa.enderecos")
    @Mapping(target = "contatos", source = "pessoa.contatos")
    @Mapping(target = "telefones", source = "pessoa.telefones")
    @Mapping(target = "cargoId", source = "cargo.id")
    @Mapping(target = "cargoNome", source = "cargo.nome")
    @Mapping(target = "setorId", source = "setor.id")
    @Mapping(target = "setorNome", source = "setor.nome")
    ColaboradorResponse toResponse(Colaborador colaborador);

    default String rg(Pessoa pessoa) {
        return pessoa.getPessoaFisica() != null ? pessoa.getPessoaFisica().getRg() : null;
    }

    default java.time.LocalDate dataNascimento(Pessoa pessoa) {
        return pessoa.getPessoaFisica() != null ? pessoa.getPessoaFisica().getDataNascimento() : null;
    }

    default String nomeFantasia(Pessoa pessoa) {
        return pessoa.getPessoaJuridica() != null ? pessoa.getPessoaJuridica().getNomeFantasia() : null;
    }

    default String inscricaoEstadual(Pessoa pessoa) {
        return pessoa.getPessoaJuridica() != null ? pessoa.getPessoaJuridica().getInscricaoEstadual() : null;
    }

    default String inscricaoMunicipal(Pessoa pessoa) {
        return pessoa.getPessoaJuridica() != null ? pessoa.getPessoaJuridica().getInscricaoMunicipal() : null;
    }
}
