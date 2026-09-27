package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Catálogo dos modelos de documento fiscal (Ato Cotepe 46/10: 55=NF-e, 65=NFC-e,
 * 59=SAT/CF-e, 57=CT-e etc.) — validado contra {@code fenix.sql.NOTA_FISCAL_MODELO}.
 * Só referência/numeração nesta fase; a emissão real (XML/SEFAZ via ACBr) é Fase 8
 * ({@code fiscal-service}, ver CLAUDE.md).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "nota_fiscal_modelo")
public class NotaFiscalModelo extends BaseEntity {

    @Column(name = "codigo", length = 2)
    private String codigo;

    @Column(name = "descricao", length = 100)
    private String descricao;

    @Column(name = "modelo", length = 10)
    private String modelo;
}
