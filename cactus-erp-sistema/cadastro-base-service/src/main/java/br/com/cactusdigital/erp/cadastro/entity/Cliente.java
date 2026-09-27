package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Cliente — referencia {@link Pessoa} (física ou jurídica) em vez de duplicar
 * nome/documento/endereço como o {@code CLIENTE} "achatado" do cactus-pdv.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cliente")
public class Cliente extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pessoa_id", nullable = false, unique = true)
    private Pessoa pessoa;

    @Column(name = "url", length = 250)
    private String url;

    @Column(name = "limite_credito", precision = 18, scale = 6)
    private BigDecimal limiteCredito;

    @Column(name = "taxa_desconto", precision = 5, scale = 2)
    private BigDecimal taxaDesconto;

    /** Validado contra {@code BANCO_PDV.md.CLIENTE}: programa de fidelidade, que eu tinha derrubado na 1ª passada. */
    @Enumerated(EnumType.STRING)
    @Column(name = "fidelidade_aviso", length = 20)
    private FidelidadeAviso fidelidadeAviso;

    @Column(name = "fidelidade_quantidade")
    private Integer fidelidadeQuantidade;

    @Column(name = "fidelidade_valor", precision = 18, scale = 6)
    private BigDecimal fidelidadeValor;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
