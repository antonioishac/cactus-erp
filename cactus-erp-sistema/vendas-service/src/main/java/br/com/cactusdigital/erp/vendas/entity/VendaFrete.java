package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Dados de transporte de uma {@link VendaCabecalho} — validado contra
 * {@code fenix.sql.VENDA_FRETE}. Modelado 1:1 com a venda (simplificação deliberada: o
 * fenix não tem constraint de unicidade na FK, permitindo tecnicamente N fretes por
 * venda, mas nenhum outro campo distingue as linhas — na prática é sempre 1:1, e
 * múltiplos despachos por venda ficariam para um futuro módulo de logística/expedição).
 * {@code transportadoraId} é referência SOLTA pro cadastro-base-service.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_frete")
public class VendaFrete extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false, unique = true)
    private VendaCabecalho venda;

    @Column(name = "transportadora_id")
    private Long transportadoraId;

    @Column(name = "conhecimento", length = 20)
    private String conhecimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "responsavel", length = 3)
    private TipoFrete responsavel;

    @Column(name = "placa", length = 7)
    private String placa;

    @Column(name = "uf_placa", length = 2)
    private String ufPlaca;

    @Column(name = "selo_fiscal", length = 20)
    private String seloFiscal;

    @Column(name = "quantidade_volume", precision = 18, scale = 6)
    private BigDecimal quantidadeVolume;

    @Column(name = "marca_volume", length = 50)
    private String marcaVolume;

    @Column(name = "especie_volume", length = 20)
    private String especieVolume;

    @Column(name = "peso_bruto", precision = 18, scale = 6)
    private BigDecimal pesoBruto;

    @Column(name = "peso_liquido", precision = 18, scale = 6)
    private BigDecimal pesoLiquido;
}
