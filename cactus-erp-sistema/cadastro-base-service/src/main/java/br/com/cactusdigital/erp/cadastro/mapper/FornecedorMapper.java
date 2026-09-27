package br.com.cactusdigital.erp.cadastro.mapper;

import br.com.cactusdigital.erp.cadastro.dto.ContatoDTO;
import br.com.cactusdigital.erp.cadastro.dto.EnderecoDTO;
import br.com.cactusdigital.erp.cadastro.dto.FornecedorResponse;
import br.com.cactusdigital.erp.cadastro.dto.TelefoneDTO;
import br.com.cactusdigital.erp.cadastro.entity.Contato;
import br.com.cactusdigital.erp.cadastro.entity.Endereco;
import br.com.cactusdigital.erp.cadastro.entity.Fornecedor;
import br.com.cactusdigital.erp.cadastro.entity.Pessoa;
import br.com.cactusdigital.erp.cadastro.entity.Telefone;
import java.time.LocalDate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapeamento de leitura (Fornecedor+Pessoa+especialização -> {@link FornecedorResponse}) —
 * mesmo padrão de {@code ClienteMapper}. A direção de escrita fica em
 * {@code FornecedorServiceImpl}, que decide PessoaFisica/PessoaJuridica via
 * {@code PessoaAssembler}.
 */
@Mapper(componentModel = "spring")
public interface FornecedorMapper {

    @Mapping(target = "pessoaId", source = "pessoa.id")
    @Mapping(target = "nome", source = "pessoa.nome")
    @Mapping(target = "tipoPessoa", source = "pessoa.tipoPessoa")
    @Mapping(target = "email", source = "pessoa.email")
    @Mapping(target = "documento", expression = "java(fornecedor.getPessoa().getDocumento())")
    @Mapping(target = "rg", expression = "java(rg(fornecedor.getPessoa()))")
    @Mapping(target = "dataNascimento", expression = "java(dataNascimento(fornecedor.getPessoa()))")
    @Mapping(target = "nomeFantasia", expression = "java(nomeFantasia(fornecedor.getPessoa()))")
    @Mapping(target = "inscricaoEstadual", expression = "java(inscricaoEstadual(fornecedor.getPessoa()))")
    @Mapping(target = "inscricaoMunicipal", expression = "java(inscricaoMunicipal(fornecedor.getPessoa()))")
    @Mapping(target = "enderecos", source = "pessoa.enderecos")
    @Mapping(target = "contatos", source = "pessoa.contatos")
    @Mapping(target = "telefones", source = "pessoa.telefones")
    FornecedorResponse toResponse(Fornecedor fornecedor);

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
