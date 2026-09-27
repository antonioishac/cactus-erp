package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * 1 título relatado num arquivo de retorno CNAB 240 (par de segmentos T+U por título) —
 * {@code contaReceberId} é resolvido por {@code nossoNumero} contra
 * {@link RemessaBancariaItem} (não pelo número de documento, que é de controle do
 * cliente, não do banco). {@code processado=true} quando o {@code codigoMovimento}
 * disparou baixa automática — ver {@code OcorrenciaRetornoCnab240#geraBaixaAutomatica}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "retorno_bancario_item")
public class RetornoBancarioItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retorno_id", nullable = false)
    private RetornoBancario retorno;

    @Column(name = "nosso_numero", length = 20)
    private String nossoNumero;

    @Column(name = "numero_documento", length = 15)
    private String numeroDocumento;

    @Column(name = "codigo_movimento", length = 2)
    private String codigoMovimento;

    @Column(name = "data_ocorrencia")
    private LocalDate dataOcorrencia;

    @Column(name = "valor_titulo", precision = 18, scale = 6)
    private BigDecimal valorTitulo;

    @Column(name = "valor_pago", precision = 18, scale = 6)
    private BigDecimal valorPago;

    @Column(name = "conta_receber_id")
    private Long contaReceberId;

    @Builder.Default
    @Column(name = "processado", nullable = false)
    private boolean processado = false;

    @Column(name = "observacao", length = 250)
    private String observacao;
}
