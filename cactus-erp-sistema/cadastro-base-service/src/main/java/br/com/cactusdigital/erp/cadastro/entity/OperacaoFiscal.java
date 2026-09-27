package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Tipo de operação de venda (ex: "venda a consumidor final", "venda a revendedor",
 * "venda interestadual") — validado contra {@code fenix.sql.TRIBUT_OPERACAO_FISCAL}.
 * Combinada com {@link GrupoTributario} forma a célula {@link ConfiguracaoTributaria}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "operacao_fiscal")
public class OperacaoFiscal extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "descricao_na_nf", length = 100)
    private String descricaoNaNf;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cfop_id")
    private Cfop cfop;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;

    /** ISS é 1:1 direto com a Operação Fiscal (não com a célula Grupo×Operação) — só faz sentido pra serviço. */
    @OneToOne(mappedBy = "operacaoFiscal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private IssConfiguracao issConfiguracao;
}
