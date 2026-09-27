package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Master data que o {@code PDV_TIPO_PAGAMENTO} do cactus-pdv espera receber da
 * retaguarda — o próprio DDL do PDV comenta "dados devem ser importados da retaguarda",
 * e eu nunca tinha modelado isso no cadastro-base. Fica em {@code cadastro-base}
 * (não em compras/financeiro) porque é dado mestre compartilhado, igual Produto/Cliente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "forma_pagamento")
public class FormaPagamento extends BaseEntity {

    @Column(name = "codigo", nullable = false, length = 3, unique = true)
    private String codigo;

    @Column(name = "descricao", nullable = false, length = 20)
    private String descricao;

    @Column(name = "icone", length = 50)
    private String icone;

    @Column(name = "tef", nullable = false)
    private boolean tef;

    @Column(name = "imprime_vinculado", nullable = false)
    private boolean imprimeVinculado;

    @Column(name = "permite_troco", nullable = false)
    private boolean permiteTroco;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "tef_tipo_gp", length = 20)
    private TefTipoGp tefTipoGp = TefTipoGp.NENHUM;

    @Column(name = "gera_parcelas", nullable = false)
    private boolean geraParcelas;

    @Column(name = "codigo_pagamento_nfce", length = 2)
    private String codigoPagamentoNfce;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
