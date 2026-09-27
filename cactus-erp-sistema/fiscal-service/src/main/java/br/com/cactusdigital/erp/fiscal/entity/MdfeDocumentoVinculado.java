package br.com.cactusdigital.erp.fiscal.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Vínculo entre um {@link MdfeCabecalho} e um {@link CteCabecalho} já {@code AUTORIZADA} —
 * um CT-e só pode estar vinculado a um MDF-e por vez ({@code uk_mdfe_documento_cte}, mesmo
 * critério de {@code CteCabecalho.vendaId unique=true}). {@code municipioDescarga*} é
 * snapshot resolvido a partir do endereço do destinatário do CT-e no momento da geração do
 * MDF-e — mesmo espírito de outros snapshots já usados no monorepo
 * ({@code ItemPedido.skuSnapshot}), sobrevive mesmo que o cadastro do cliente mude depois.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "mdfe_documento_vinculado")
public class MdfeDocumentoVinculado extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mdfe_cabecalho_id", nullable = false)
    private MdfeCabecalho mdfeCabecalho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cte_cabecalho_id", nullable = false, unique = true)
    private CteCabecalho cteCabecalho;

    @Column(name = "municipio_descarga_codigo_ibge")
    private Integer municipioDescargaCodigoIbge;

    @Column(name = "municipio_descarga_nome", length = 60)
    private String municipioDescargaNome;
}
