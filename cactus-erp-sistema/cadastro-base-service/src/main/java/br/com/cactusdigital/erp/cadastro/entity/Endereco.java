package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Endereço de uma {@link Pessoa} (1:N — uma pessoa pode ter mais de um, ex: cobrança e
 * entrega). Gerenciado sempre como parte do agregado {@link Pessoa} (cascade a partir
 * dela) — não tem repositório/controller próprio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "pessoa_endereco")
public class Endereco extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @Column(name = "logradouro", length = 150)
    private String logradouro;

    @Column(name = "numero", length = 10)
    private String numero;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "municipio", length = 100)
    private String municipio;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "codigo_ibge_municipio")
    private Integer codigoIbgeMunicipio;

    @Column(name = "codigo_ibge_uf")
    private Integer codigoIbgeUf;

    @Column(name = "principal", nullable = false)
    private boolean principal;

    /** Validado contra {@code fenix.sql.PESSOA_ENDERECO}: um endereço pode acumular papéis. */
    @Column(name = "entrega", nullable = false)
    private boolean entrega;

    @Column(name = "cobranca", nullable = false)
    private boolean cobranca;

    @Column(name = "correspondencia", nullable = false)
    private boolean correspondencia;
}
