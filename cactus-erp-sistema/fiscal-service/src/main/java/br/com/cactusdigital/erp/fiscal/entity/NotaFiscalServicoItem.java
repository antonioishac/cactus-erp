package br.com.cactusdigital.erp.fiscal.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Item de {@link NotaFiscalServico} — validado contra {@code fenix.sql.NFSE_DETALHE}. {@code discriminacao} vem do {@code complemento} do item SERVICO da OS de origem. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "nota_fiscal_servico_item")
public class NotaFiscalServicoItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_fiscal_servico_id", nullable = false)
    private NotaFiscalServico notaFiscalServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_servico_id", nullable = false)
    private ListaServicoMunicipal listaServico;

    @Column(name = "discriminacao", length = 500)
    private String discriminacao;

    @Column(name = "codigo_tributacao_municipio", length = 20)
    private String codigoTributacaoMunicipio;

    @Column(name = "valor_servicos", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorServicos;

    @Column(name = "aliquota_iss", precision = 18, scale = 6)
    private BigDecimal aliquotaIss;

    @Column(name = "valor_iss", precision = 18, scale = 6)
    private BigDecimal valorIss;
}
