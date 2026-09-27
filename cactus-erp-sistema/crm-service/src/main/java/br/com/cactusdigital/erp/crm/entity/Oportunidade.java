package br.com.cactusdigital.erp.crm.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Oportunidade de venda — nasce de um {@link Lead} qualificado OU direto de um
 * {@code Cliente} já existente (venda adicional pra carteira ativa, sem passar por
 * lead). Por isso {@code leadId} é opcional (FK de verdade quando presente) e
 * {@code clienteId} é solto/opcional (obrigatório preencher em algum momento antes de
 * fechar como {@code GANHA}, mas não validado nesta v1 — decisão registrada, mesmo
 * espírito de "sem validação de existência cross-serviço na v1" já usado em todo o
 * monorepo). {@code estagioFunil} é FK obrigatória pro catálogo livre
 * {@link EstagioFunil}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "oportunidade")
public class Oportunidade extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "vendedor_id")
    private Long vendedorId;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "valor_estimado", precision = 18, scale = 6)
    private BigDecimal valorEstimado;

    @Column(name = "probabilidade_percentual", precision = 5, scale = 2)
    private BigDecimal probabilidadePercentual;

    @Column(name = "data_previsao_fechamento")
    private LocalDate dataPrevisaoFechamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estagio_funil_id", nullable = false)
    private EstagioFunil estagioFunil;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 10)
    private SituacaoOportunidade situacao = SituacaoOportunidade.ABERTA;

    @Column(name = "motivo_perda", length = 250)
    private String motivoPerda;

    @Column(name = "data_fechamento")
    private Instant dataFechamento;

    @Column(name = "observacao", length = 500)
    private String observacao;
}
