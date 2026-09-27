package br.com.cactusdigital.erp.cadastro.mapper;

import br.com.cactusdigital.erp.cadastro.dto.ClienteResponse;
import br.com.cactusdigital.erp.cadastro.dto.ContatoDTO;
import br.com.cactusdigital.erp.cadastro.dto.EnderecoDTO;
import br.com.cactusdigital.erp.cadastro.dto.TelefoneDTO;
import br.com.cactusdigital.erp.cadastro.entity.Cliente;
import br.com.cactusdigital.erp.cadastro.entity.Contato;
import br.com.cactusdigital.erp.cadastro.entity.Endereco;
import br.com.cactusdigital.erp.cadastro.entity.Pessoa;
import br.com.cactusdigital.erp.cadastro.entity.PessoaFisica;
import br.com.cactusdigital.erp.cadastro.entity.PessoaJuridica;
import br.com.cactusdigital.erp.cadastro.entity.Telefone;
import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapeamento de leitura (Cliente+Pessoa+especialização -> {@link ClienteResponse}). A
 * direção de escrita (request -> entidades) fica em {@link ClienteServiceImpl}, que
 * decide se monta {@code PessoaFisica} ou {@code PessoaJuridica} conforme o
 * {@code tipoPessoa} — lógica condicional demais pra um mapper declarativo.
 */
@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "pessoaId", source = "pessoa.id")
    @Mapping(target = "nome", source = "pessoa.nome")
    @Mapping(target = "tipoPessoa", source = "pessoa.tipoPessoa")
    @Mapping(target = "email", source = "pessoa.email")
    @Mapping(target = "documento", expression = "java(cliente.getPessoa().getDocumento())")
    @Mapping(target = "rg", expression = "java(rg(cliente.getPessoa()))")
    @Mapping(target = "dataNascimento", expression = "java(dataNascimento(cliente.getPessoa()))")
    @Mapping(target = "nomeFantasia", expression = "java(nomeFantasia(cliente.getPessoa()))")
    @Mapping(target = "inscricaoEstadual", expression = "java(inscricaoEstadual(cliente.getPessoa()))")
    @Mapping(target = "inscricaoMunicipal", expression = "java(inscricaoMunicipal(cliente.getPessoa()))")
    @Mapping(target = "enderecos", source = "pessoa.enderecos")
    @Mapping(target = "contatos", source = "pessoa.contatos")
    @Mapping(target = "telefones", source = "pessoa.telefones")
    ClienteResponse toResponse(Cliente cliente);

    EnderecoDTO toDTO(Endereco endereco);

    ContatoDTO toDTO(Contato contato);

    TelefoneDTO toDTO(Telefone telefone);

    default String rg(Pessoa pessoa) {
        return pessoa.getPessoaFisica() != null ? pessoa.getPessoaFisica().getRg() : null;
    }

    default LocalDate dataNascimento(Pessoa pessoa) {
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
