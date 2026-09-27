package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Colaborador — referencia {@link Pessoa}, igual {@code Cliente}. Validado contra
 * {@code fenix.sql.COLABORADOR} (RH básico: cargo/setor/matrícula/admissão-demissão/CTPS)
 * e {@code BANCO_PDV.md.COLABORADOR} (comissão à vista/a prazo, veículo de entregador —
 * é esse conjunto que precisa sincronizar pro PDV).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "colaborador")
public class Colaborador extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pessoa_id", nullable = false, unique = true)
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @Column(name = "matricula", length = 10)
    private String matricula;

    @Column(name = "data_admissao")
    private LocalDate dataAdmissao;

    @Column(name = "data_demissao")
    private LocalDate dataDemissao;

    @Column(name = "ctps_numero", length = 10)
    private String ctpsNumero;

    @Column(name = "ctps_serie", length = 10)
    private String ctpsSerie;

    @Column(name = "ctps_data_expedicao")
    private LocalDate ctpsDataExpedicao;

    @Column(name = "ctps_uf", length = 2)
    private String ctpsUf;

    @Column(name = "comissao_vista", precision = 18, scale = 6)
    private BigDecimal comissaoVista;

    @Column(name = "comissao_prazo", precision = 18, scale = 6)
    private BigDecimal comissaoPrazo;

    @Enumerated(EnumType.STRING)
    @Column(name = "entregador_veiculo", length = 20)
    private TipoVeiculoEntregador entregadorVeiculo;

    @Column(name = "observacao", length = 250)
    private String observacao;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
