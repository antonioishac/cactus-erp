package br.com.cactusdigital.erp.contabilidade.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Lançamento contábil — o cabeçalho de N {@link PartidaContabil} (débito/crédito) que
 * juntas formam UM fato contábil (o "documento" do Livro Diário, registro {@code I200}+
 * {@code I250} da ECD). Imutável depois de lançado (ver {@link SituacaoLancamentoContabil});
 * a validação de partida dobrada (soma débito = soma crédito) é responsabilidade de
 * {@code LancamentoContabilService#validarPartidaDobrada}, nunca confiada só ao
 * cliente — "não podemos admitir erros" aqui é literal: um lançamento desbalanceado
 * corrompe todo balancete/SPED gerado a partir dele.
 * <p>
 * {@link #estornoDeId}/{@link #estornadoPorId} formam o vínculo bidirecional entre um
 * lançamento e o estorno que o desfez — nunca uma edição/exclusão física, mesmo critério
 * de {@code BaixaContaReceber.estornado}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "lancamento_contabil")
public class LancamentoContabil extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(name = "historico", nullable = false, length = 250)
    private String historico;

    @Column(name = "numero_documento", length = 30)
    private String numeroDocumento;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "origem_tipo", nullable = false, length = 15)
    private OrigemLancamentoContabil origemTipo = OrigemLancamentoContabil.MANUAL;

    @Column(name = "origem_id")
    private Long origemId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 10)
    private SituacaoLancamentoContabil situacao = SituacaoLancamentoContabil.LANCADO;

    /** Preenchido quando ESTE lançamento é o estorno de outro — aponta pro original. */
    @Column(name = "estorno_de_id")
    private Long estornoDeId;

    /** Preenchido no ORIGINAL quando ele foi estornado — aponta pro lançamento de estorno gerado. */
    @Column(name = "estornado_por_id")
    private Long estornadoPorId;

    @Builder.Default
    @OneToMany(mappedBy = "lancamentoContabil", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PartidaContabil> partidas = new ArrayList<>();
}
