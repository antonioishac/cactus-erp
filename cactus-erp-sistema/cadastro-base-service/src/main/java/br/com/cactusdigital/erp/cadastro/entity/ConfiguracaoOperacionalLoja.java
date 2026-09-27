package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Config operacional de terminal/loja — hoje só existe hard-coded no PDV
 * ({@code LOJA_CONFIGURACAO} pros toggles de mesa/comanda/delivery, {@code PDV_CONFIGURACAO}
 * pros limites de desconto — o comentário do DDL original diz "política da matriz").
 * Nasce aqui, é empurrada pro PDV no provisionamento do terminal (Fase 6).
 * <p>
 * {@code modoComanda}: no PDV é {@code CHAR(1)} livre, default {@code 'V'} — o
 * {@code BANCO_PDV.md} não documenta os demais valores possíveis, então mantive como
 * string opaca (não inventei um enum com significados que eu não confirmei).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "configuracao_operacional_loja")
public class ConfiguracaoOperacionalLoja extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false, unique = true)
    private Empresa empresa;

    @Column(name = "permite_mesas", nullable = false)
    private boolean permiteMesas;

    @Column(name = "permite_comandas", nullable = false)
    private boolean permiteComandas;

    @Column(name = "permite_delivery", nullable = false)
    private boolean permiteDelivery;

    @Column(name = "modo_comanda", length = 1)
    private String modoComanda;

    @Column(name = "permite_mesa_avulsa", nullable = false)
    private boolean permiteMesaAvulsa;

    /** Percentual/valor máximo sem autorização gerencial — acima disso exige aprovação nominal (ver ANÁLISE: HIERARQUIA no CLAUDE.md). */
    @Column(name = "limite_desconto_item_percentual", precision = 5, scale = 2)
    private BigDecimal limiteDescontoItemPercentual;

    @Column(name = "limite_desconto_venda_percentual", precision = 5, scale = 2)
    private BigDecimal limiteDescontoVendaPercentual;

    @Column(name = "limite_desconto_venda_valor", precision = 18, scale = 6)
    private BigDecimal limiteDescontoVendaValor;

    @Column(name = "limite_acrescimo_item_percentual", precision = 5, scale = 2)
    private BigDecimal limiteAcrescimoItemPercentual;

    @Column(name = "limite_acrescimo_venda_percentual", precision = 5, scale = 2)
    private BigDecimal limiteAcrescimoVendaPercentual;

    /** N = nem gerente pode autorizar acima do limite (política da matriz). */
    @Column(name = "permite_desconto_acima_limite", nullable = false)
    private boolean permiteDescontoAcimaLimite;
}
