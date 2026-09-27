package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Vendedor — validado contra {@code fenix.sql.VENDEDOR}. {@code colaboradorId} é
 * referência SOLTA pro {@code cadastro-base-service} (cross-serviço, mesmo padrão de
 * {@code produtoId}/{@code clienteId} em todo o monorepo — sem FK de banco, sem
 * validação de existência na v1). {@code comissaoPerfilId} É FK de verdade, pois
 * {@link ComissaoPerfil} mora neste mesmo schema.
 * <p>
 * Diferente do {@code Colaborador.comissaoVista}/{@code comissaoPrazo} flat do
 * cadastro-base (Fase 1a, campos vindos direto do PDV) — este é o enriquecimento
 * "Vendedor" registrado como pendente na ANÁLISE: HIERARQUIA DE USUÁRIOS do CLAUDE.md,
 * construído agora que o domínio de Vendas existe: perfil de comissão + meta por
 * produto (via {@link ComissaoObjetivo}), não só uma taxa flat.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "vendedor")
public class Vendedor extends BaseEntity {

    @Column(name = "colaborador_id", nullable = false, unique = true)
    private Long colaboradorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comissao_perfil_id")
    private ComissaoPerfil comissaoPerfil;

    /** Percentual de comissão padrão (usado quando não há {@link ComissaoObjetivo} específico pro produto vendido). */
    @Column(name = "comissao_percentual", precision = 18, scale = 6)
    private BigDecimal comissaoPercentual;

    @Column(name = "meta_venda", precision = 18, scale = 6)
    private BigDecimal metaVenda;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
