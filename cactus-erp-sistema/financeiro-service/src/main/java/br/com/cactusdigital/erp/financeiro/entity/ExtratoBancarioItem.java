package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Uma linha (transação) do extrato bancário importado — {@code valor} preserva o SINAL
 * como veio do OFX (negativo = saída, positivo = entrada), diferente de
 * {@link MovimentoCaixaBanco#getValor()} que é sempre positivo (aqui é reflexo de um
 * dado externo, lá é o ledger interno com sinal decidido pelo {@code tipoMovimento}).
 * <p>
 * {@code fitId} é a chave de dedup entre importações do mesmo arquivo (ou arquivos
 * sobrepostos, ex: reimportar um mês que já tinha sido importado parcialmente) — ver
 * {@code ConciliacaoBancariaService#importar}. {@code movimentoCaixaBancoId} é preenchido
 * quando esta linha é conciliada (automática ou manualmente) com um lançamento interno,
 * ou quando um ajuste é gerado a partir dela (ver {@code gerarAjuste}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "extrato_bancario_item")
public class ExtratoBancarioItem extends BaseEntity {

    @Column(name = "importacao_id", nullable = false)
    private Long importacaoId;

    @Column(name = "conta_bancaria_id", nullable = false)
    private Long contaBancariaId;

    @Column(name = "data_movimento", nullable = false)
    private LocalDate dataMovimento;

    @Column(name = "historico", length = 250)
    private String historico;

    @Column(name = "tipo_ofx", length = 20)
    private String tipoOfx;

    /** Sinal preservado como veio do OFX — ver Javadoc da classe. */
    @Column(name = "valor", nullable = false, precision = 18, scale = 6)
    private BigDecimal valor;

    /** Chave de dedup entre importações — {@code null} quando o OFX não informou (bancos
     * raros/mal formatados), nesse caso a linha nunca é deduplicada automaticamente. */
    @Column(name = "fit_id", length = 100)
    private String fitId;

    @Builder.Default
    @Column(name = "conciliado", nullable = false)
    private boolean conciliado = false;

    @Column(name = "movimento_caixa_banco_id")
    private Long movimentoCaixaBancoId;
}
