package br.com.cactusdigital.erp.cadastro.service;

import br.com.cactusdigital.erp.cadastro.dto.ContatoDTO;
import br.com.cactusdigital.erp.cadastro.dto.EnderecoDTO;
import br.com.cactusdigital.erp.cadastro.dto.TelefoneDTO;
import br.com.cactusdigital.erp.cadastro.entity.Contato;
import br.com.cactusdigital.erp.cadastro.entity.Endereco;
import br.com.cactusdigital.erp.cadastro.entity.Pessoa;
import br.com.cactusdigital.erp.cadastro.entity.PessoaFisica;
import br.com.cactusdigital.erp.cadastro.entity.PessoaJuridica;
import br.com.cactusdigital.erp.cadastro.entity.Telefone;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Monta/atualiza o agregado {@link Pessoa} (especialização Física/Jurídica + listas de
 * endereço/contato/telefone) — extraído pra ser reaproveitado por qualquer serviço que
 * tenha uma {@code Pessoa} por trás ({@code ClienteService}, {@code ColaboradorService},
 * e futuramente {@code FornecedorService}), em vez de duplicar essa lógica em cada um.
 */
@Component
public class PessoaAssembler {

    public void aplicarEspecializacao(
            Pessoa pessoa, TipoPessoa tipoPessoa,
            String cpf, String rg, LocalDate dataNascimento,
            String cnpj, String nomeFantasia, String inscricaoEstadual, String inscricaoMunicipal) {
        if (tipoPessoa == TipoPessoa.FISICA) {
            PessoaFisica pessoaFisica = pessoa.getPessoaFisica() != null ? pessoa.getPessoaFisica() : new PessoaFisica();
            pessoaFisica.setPessoa(pessoa);
            pessoaFisica.setCpf(cpf);
            pessoaFisica.setRg(rg);
            pessoaFisica.setDataNascimento(dataNascimento);
            pessoa.setPessoaFisica(pessoaFisica);
        } else {
            PessoaJuridica pessoaJuridica = pessoa.getPessoaJuridica() != null ? pessoa.getPessoaJuridica() : new PessoaJuridica();
            pessoaJuridica.setPessoa(pessoa);
            pessoaJuridica.setCnpj(cnpj);
            pessoaJuridica.setNomeFantasia(nomeFantasia);
            pessoaJuridica.setInscricaoEstadual(inscricaoEstadual);
            pessoaJuridica.setInscricaoMunicipal(inscricaoMunicipal);
            pessoa.setPessoaJuridica(pessoaJuridica);
        }
    }

    /**
     * Substitui o conteúdo das coleções sem trocar a referência da lista — necessário
     * pra {@code orphanRemoval=true} funcionar (Hibernate rastreia a MESMA coleção
     * gerenciada; reatribuir pra uma {@code List} nova quebra esse rastreamento).
     */
    public void substituirListas(Pessoa pessoa, List<EnderecoDTO> enderecos, List<ContatoDTO> contatos, List<TelefoneDTO> telefones) {
        pessoa.getEnderecos().clear();
        for (EnderecoDTO dto : enderecos != null ? enderecos : List.<EnderecoDTO>of()) {
            pessoa.getEnderecos().add(Endereco.builder()
                    .pessoa(pessoa)
                    .logradouro(dto.logradouro())
                    .numero(dto.numero())
                    .complemento(dto.complemento())
                    .bairro(dto.bairro())
                    .cep(dto.cep())
                    .municipio(dto.municipio())
                    .uf(dto.uf())
                    .codigoIbgeMunicipio(dto.codigoIbgeMunicipio())
                    .codigoIbgeUf(dto.codigoIbgeUf())
                    .principal(dto.principal())
                    .entrega(dto.entrega())
                    .cobranca(dto.cobranca())
                    .correspondencia(dto.correspondencia())
                    .build());
        }

        pessoa.getContatos().clear();
        for (ContatoDTO dto : contatos != null ? contatos : List.<ContatoDTO>of()) {
            pessoa.getContatos().add(Contato.builder()
                    .pessoa(pessoa)
                    .tipoContato(dto.tipoContato())
                    .valor(dto.valor())
                    .principal(dto.principal())
                    .build());
        }

        pessoa.getTelefones().clear();
        for (TelefoneDTO dto : telefones != null ? telefones : List.<TelefoneDTO>of()) {
            pessoa.getTelefones().add(Telefone.builder()
                    .pessoa(pessoa)
                    .tipoTelefone(dto.tipoTelefone())
                    .numero(dto.numero())
                    .principal(dto.principal())
                    .build());
        }
    }
}
