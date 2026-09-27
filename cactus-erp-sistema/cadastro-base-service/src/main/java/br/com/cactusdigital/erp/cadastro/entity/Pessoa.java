package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.cadastro.service.ClienteService;
import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Pessoa — base comum de Cliente, Fornecedor e Colaborador (estes ainda não
 * implementados nesta fase, mas já referenciarão {@code Pessoa} do mesmo jeito que
 * {@link br.com.cactusdigital.erp.cadastro.cliente.Cliente} faz). Especializada em
 * {@link PessoaFisica} ou {@link PessoaJuridica} conforme {@link #tipoPessoa} — só uma
 * das duas existe para cada Pessoa (garantido pela camada de serviço, não pelo banco).
 * <p>
 * Endereços/contatos/telefones são geridos como parte deste agregado (cascade ALL +
 * orphanRemoval) — sempre através do serviço que monta a Pessoa (ex: ClienteService),
 * nunca por um repositório/controller próprio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "pessoa")
public class Pessoa extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "tipo_pessoa", nullable = false, length = 1)
    private TipoPessoa tipoPessoa;

    @Column(name = "email", length = 250)
    private String email;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PessoaFisica pessoaFisica;

    @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private PessoaJuridica pessoaJuridica;

    @Builder.Default
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Endereco> enderecos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Contato> contatos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Telefone> telefones = new ArrayList<>();

    /** Documento (CPF ou CNPJ) da especialização vigente — atalho de leitura. */
    public String getDocumento() {
        if (pessoaFisica != null) {
            return pessoaFisica.getCpf();
        }
        if (pessoaJuridica != null) {
            return pessoaJuridica.getCnpj();
        }
        return null;
    }
}
