package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Especialização de {@link Pessoa} para {@link TipoPessoa#FISICA}. Chave primária
 * compartilhada com {@code Pessoa} via {@code @MapsId} — por isso estende {@link
 * AuditMetadata} (só as colunas de auditoria) em vez de {@link
 * br.com.cactusdigital.erp.common.domain.BaseEntity}, cujo id é sempre autogerado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "pessoa_fisica")
public class PessoaFisica extends AuditMetadata {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Pessoa pessoa;

    @Column(name = "cpf", nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(name = "rg", length = 20)
    private String rg;

    @Column(name = "orgao_rg", length = 20)
    private String orgaoRg;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_emissao_rg")
    private LocalDate dataEmissaoRg;

    @Column(name = "sexo", length = 1)
    private String sexo;
}
