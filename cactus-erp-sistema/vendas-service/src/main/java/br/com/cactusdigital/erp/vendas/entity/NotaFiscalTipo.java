package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Tipo de NF usado pela empresa, controla série/numeração — validado contra
 * {@code fenix.sql.NOTA_FISCAL_TIPO}. {@code ultimoNumero} é só um contador de
 * referência nesta fase (a numeração oficial de verdade, com reserva/inutilização, é
 * responsabilidade do {@code fiscal-service}, Fase 8).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "nota_fiscal_tipo")
public class NotaFiscalTipo extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_fiscal_modelo_id", nullable = false)
    private NotaFiscalModelo notaFiscalModelo;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Column(name = "serie", length = 3)
    private String serie;

    @Column(name = "serie_scan", length = 3)
    private String serieScan;

    @Builder.Default
    @Column(name = "ultimo_numero", nullable = false)
    private Integer ultimoNumero = 0;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
