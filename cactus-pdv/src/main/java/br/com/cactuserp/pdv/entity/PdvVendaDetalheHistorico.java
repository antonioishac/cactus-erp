package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Trilha auditável de alterações de itens de venda (PAF-NFC-e).
 *
 * <p>Cada registro deste histórico representa UMA alteração feita num
 * {@link PdvVendaDetalhe}: agrupamento, cancelamento, desconto/acréscimo,
 * mudança de faixa de preço, etc.
 *
 * <p>Preserva o estado ANTERIOR e o estado NOVO do item, junto com o
 * hash de integridade de cada um. Isso atende dois requisitos:
 * <ul>
 *   <li><b>PAF-NFC-e</b>: auditoria fiscal completa de qualquer alteração</li>
 *   <li><b>Debug/Suporte</b>: rastreabilidade de "como esse item virou isso"</li>
 * </ul>
 *
 * <p>Os campos são todos opcionais (nullable) porque o {@code TIPO_ALTERACAO}
 * determina quais fazem sentido. Por exemplo, num histórico do tipo
 * {@code C}=Cancelado, normalmente só os campos {@code CANCELADO_*}
 * mudam — os outros podem ficar iguais.
 */
@Entity
@Table(name = "PDV_VENDA_DETALHE_HISTORICO")
public class PdvVendaDetalheHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ID_PDV_VENDA_DETALHE", nullable = false)
    private Long idPdvVendaDetalhe;

    @Column(name = "VERSAO", nullable = false)
    private Integer versao;

    @Column(name = "DATA_HORA", nullable = false)
    private LocalDateTime dataHora;

    /**
     * Tipo de alteração:
     * <ul>
     *   <li><b>A</b> = Adicionado (insert inicial)</li>
     *   <li><b>G</b> = AGRUPADO (mesmo produto bipado de novo, somou qtd)</li>
     *   <li><b>C</b> = Cancelado (F1)</li>
     *   <li><b>F</b> = Faixa de preço aplicada/alterada</li>
     *   <li><b>D</b> = Desconto/Acréscimo aplicado</li>
     * </ul>
     */
    @Column(name = "TIPO_ALTERACAO", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private String tipoAlteracao;

    // ====== Estado ANTERIOR (pré-alteração) ============================
    @Column(name = "QUANTIDADE_ANTERIOR", precision = 18, scale = 6)
    private BigDecimal quantidadeAnterior;

    @Column(name = "VALOR_UNITARIO_ANTERIOR", precision = 18, scale = 6)
    private BigDecimal valorUnitarioAnterior;

    @Column(name = "VALOR_TOTAL_ANTERIOR", precision = 18, scale = 6)
    private BigDecimal valorTotalAnterior;

    @Column(name = "VALOR_DESCONTO_ANTERIOR", precision = 18, scale = 6)
    private BigDecimal valorDescontoAnterior;

    @Column(name = "VALOR_ACRESCIMO_ANTERIOR", precision = 18, scale = 6)
    private BigDecimal valorAcrescimoAnterior;

    @Column(name = "CANCELADO_ANTERIOR", length = 1, columnDefinition = "CHAR(1)")
    private String canceladoAnterior;

    @Column(name = "HASH_ANTERIOR", length = 64)
    private String hashAnterior;

    // ====== Estado NOVO (pós-alteração) ================================
    @Column(name = "QUANTIDADE_NOVA", precision = 18, scale = 6)
    private BigDecimal quantidadeNova;

    @Column(name = "VALOR_UNITARIO_NOVO", precision = 18, scale = 6)
    private BigDecimal valorUnitarioNovo;

    @Column(name = "VALOR_TOTAL_NOVO", precision = 18, scale = 6)
    private BigDecimal valorTotalNovo;

    @Column(name = "VALOR_DESCONTO_NOVO", precision = 18, scale = 6)
    private BigDecimal valorDescontoNovo;

    @Column(name = "VALOR_ACRESCIMO_NOVO", precision = 18, scale = 6)
    private BigDecimal valorAcrescimoNovo;

    @Column(name = "CANCELADO_NOVO", length = 1, columnDefinition = "CHAR(1)")
    private String canceladoNovo;

    @Column(name = "HASH_NOVO", length = 64)
    private String hashNovo;

    // ====== Auditoria ==================================================
    @Column(name = "ID_DISPOSITIVO", length = 100)
    private String idDispositivo;

    @Column(name = "MOTIVO", length = 255)
    private String motivo;

    // ====== Construtores ===============================================
    public PdvVendaDetalheHistorico() {}

    // ====== Getters / Setters ==========================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdPdvVendaDetalhe() { return idPdvVendaDetalhe; }
    public void setIdPdvVendaDetalhe(Long v) { this.idPdvVendaDetalhe = v; }

    public Integer getVersao() { return versao; }
    public void setVersao(Integer versao) { this.versao = versao; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getTipoAlteracao() { return tipoAlteracao; }
    public void setTipoAlteracao(String t) { this.tipoAlteracao = t; }

    public BigDecimal getQuantidadeAnterior() { return quantidadeAnterior; }
    public void setQuantidadeAnterior(BigDecimal v) { this.quantidadeAnterior = v; }

    public BigDecimal getValorUnitarioAnterior() { return valorUnitarioAnterior; }
    public void setValorUnitarioAnterior(BigDecimal v) { this.valorUnitarioAnterior = v; }

    public BigDecimal getValorTotalAnterior() { return valorTotalAnterior; }
    public void setValorTotalAnterior(BigDecimal v) { this.valorTotalAnterior = v; }

    public BigDecimal getValorDescontoAnterior() { return valorDescontoAnterior; }
    public void setValorDescontoAnterior(BigDecimal v) { this.valorDescontoAnterior = v; }

    public BigDecimal getValorAcrescimoAnterior() { return valorAcrescimoAnterior; }
    public void setValorAcrescimoAnterior(BigDecimal v) { this.valorAcrescimoAnterior = v; }

    public String getCanceladoAnterior() { return canceladoAnterior; }
    public void setCanceladoAnterior(String v) { this.canceladoAnterior = v; }

    public String getHashAnterior() { return hashAnterior; }
    public void setHashAnterior(String v) { this.hashAnterior = v; }

    public BigDecimal getQuantidadeNova() { return quantidadeNova; }
    public void setQuantidadeNova(BigDecimal v) { this.quantidadeNova = v; }

    public BigDecimal getValorUnitarioNovo() { return valorUnitarioNovo; }
    public void setValorUnitarioNovo(BigDecimal v) { this.valorUnitarioNovo = v; }

    public BigDecimal getValorTotalNovo() { return valorTotalNovo; }
    public void setValorTotalNovo(BigDecimal v) { this.valorTotalNovo = v; }

    public BigDecimal getValorDescontoNovo() { return valorDescontoNovo; }
    public void setValorDescontoNovo(BigDecimal v) { this.valorDescontoNovo = v; }

    public BigDecimal getValorAcrescimoNovo() { return valorAcrescimoNovo; }
    public void setValorAcrescimoNovo(BigDecimal v) { this.valorAcrescimoNovo = v; }

    public String getCanceladoNovo() { return canceladoNovo; }
    public void setCanceladoNovo(String v) { this.canceladoNovo = v; }

    public String getHashNovo() { return hashNovo; }
    public void setHashNovo(String v) { this.hashNovo = v; }

    public String getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(String v) { this.idDispositivo = v; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}