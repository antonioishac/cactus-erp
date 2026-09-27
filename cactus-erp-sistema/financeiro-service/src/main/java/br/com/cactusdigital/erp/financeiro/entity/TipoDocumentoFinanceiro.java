package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Catálogo GLOBAL (sem {@code empresaId} — mesmo critério de {@code Ncm}/{@code Cfop}
 * no cadastro-base-service: tipo de documento fiscal não varia por empresa, é
 * terminologia padrão de mercado). Espelha {@code FIN_DOCUMENTO_ORIGEM} do
 * {@code fenix.sql} — <b>não confundir com {@code OrigemContaPagar}/{@code OrigemContaReceber}</b>
 * (aquele enum descreve DE ONDE o lançamento veio dentro do próprio ERP — venda, compra,
 * manual —, este catálogo descreve QUE DOCUMENTO FISCAL o título representa: nota fiscal,
 * boleto, recibo, duplicata etc — os dois são ortogonais, um título gerado a partir de uma
 * Venda pode ter {@code tipoDocumento=NFE} ou {@code tipoDocumento=BOL} dependendo da
 * condição de pagamento).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "tipo_documento_financeiro")
public class TipoDocumentoFinanceiro extends BaseEntity {

    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(name = "descricao", nullable = false, length = 100)
    private String descricao;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
}
