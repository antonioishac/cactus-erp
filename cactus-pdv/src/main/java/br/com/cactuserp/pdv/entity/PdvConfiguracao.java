package br.com.cactuserp.pdv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Configurações operacionais do PDV, geralmente uma linha por caixa.
 *
 * <p><b>Nota:</b> a tabela {@code PDV_CONFIGURACAO} tem ~44 colunas (ECF,
 * TEF, marketing, NFC-e, integração, etc). Esta entidade mapeia apenas os
 * campos que estão atualmente em uso pelo PDV. Demais campos serão
 * adicionados conforme outras telas forem implementadas.
 */
@Entity
@Table(name = "PDV_CONFIGURACAO")
public class PdvConfiguracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PDV_CAIXA")
    private PdvCaixa caixa;

    // === Limites de desconto/acréscimo (item 13 do roadmap) ============

    @Column(name = "LIMITE_DESCONTO_ITEM_PERCENTUAL", precision = 5, scale = 2)
    private BigDecimal limiteDescontoItemPercentual;

    @Column(name = "LIMITE_DESCONTO_VENDA_PERCENTUAL", precision = 5, scale = 2)
    private BigDecimal limiteDescontoVendaPercentual;

    @Column(name = "LIMITE_DESCONTO_VENDA_VALOR", precision = 18, scale = 6)
    private BigDecimal limiteDescontoVendaValor;

    @Column(name = "LIMITE_ACRESCIMO_ITEM_PERCENTUAL", precision = 5, scale = 2)
    private BigDecimal limiteAcrescimoItemPercentual;

    @Column(name = "LIMITE_ACRESCIMO_VENDA_PERCENTUAL", precision = 5, scale = 2)
    private BigDecimal limiteAcrescimoVendaPercentual;

    @Column(name = "PERMITE_DESCONTO_ACIMA_LIMITE", length = 1)
    private String permiteDescontoAcimaLimite;

    // === Licenciamento / trava de mensalidade (ver LICENCIAMENTO.md) ===

    /** M=Mensal | S=Semestral | A=Anual. */
    @Column(name = "PLANO", length = 1, columnDefinition = "CHAR(1)")
    private String plano;

    @Column(name = "PLANO_VALOR", precision = 18, scale = 6)
    private BigDecimal planoValor;

    @Enumerated(EnumType.STRING)
    @Column(name = "PLANO_SITUACAO", length = 1, columnDefinition = "CHAR(1)")
    private PlanoSituacao planoSituacao;

    // === Fiscal — integração ACBr Monitor (ver fase 6 do ROADMAP.md) ===

    @Column(name = "ACBR_MONITOR_ENDERECO", length = 250)
    private String acbrMonitorEndereco;

    @Column(name = "ACBR_MONITOR_PORTA")
    private Integer acbrMonitorPorta;

    /** NFC-E | SAT | MFE — ver constantes em EmissaoFiscalService. */
    @Column(name = "MODULO_FISCAL_PRINCIPAL", length = 5, columnDefinition = "CHAR(5)")
    private String moduloFiscalPrincipal;

    /** NFC-E | SAT | MFE — usado quando o principal falha. */
    @Column(name = "MODULO_FISCAL_CONTINGENCIA", length = 5, columnDefinition = "CHAR(5)")
    private String moduloFiscalContingencia;

    /** Nome da impressora (cadastrada no SO) usada pela impressão térmica ESC/POS. */
    @Column(name = "NOME_IMPRESSORA_CUPOM", length = 150)
    private String nomeImpressoraCupom;

    /** S=SIM | N=NAO — coluna já existia no schema (PDV.md), mapeada na fase 7. */
    @Column(name = "GAVETA_SINAL_INVERTIDO", length = 1, columnDefinition = "CHAR(1)")
    private String gavetaSinalInvertido;

    /** 0=Sem gaveta | 1=Com gaveta ENTER/ESC | 2=Com gaveta Exclusivo. */
    @Column(name = "GAVETA_UTILIZACAO")
    private Integer gavetaUtilizacao;

    /** URL do cliente TEF local (PayGo/SiTef Hiperlink) — ver TefHiperlinkClient. */
    @Column(name = "TEF_HIPERLINK_URL", length = 250)
    private String tefHiperlinkUrl;

    // === Marketing na tela (fase 8, ver MarketingService) ==============

    /** Pasta local com as imagens do slideshow — ver {@link #marketingEstaAtivo()}. */
    @Column(name = "CAMINHO_IMAGENS_MARKETING", length = 250)
    private String caminhoImagensMarketing;

    /** S=SIM | N=NAO — coluna já existia no schema (PDV.md), mapeada na fase 8. */
    @Column(name = "MARKETING_ATIVO", length = 1, columnDefinition = "CHAR(1)")
    private String marketingAtivo;

    // ===================================================================
    //  Defaults seguros se a linha de configuração não estiver completa
    // ===================================================================

    /** 5% — limite tradicional do mercado para desconto em item. */
    public static final BigDecimal DEFAULT_LIMITE_DESCONTO_ITEM_PERCENTUAL = new BigDecimal("5.00");
    /** 10% — limite tradicional para desconto sobre o total da venda. */
    public static final BigDecimal DEFAULT_LIMITE_DESCONTO_VENDA_PERCENTUAL = new BigDecimal("10.00");
    /** R$ 50,00 — alternativa absoluta. */
    public static final BigDecimal DEFAULT_LIMITE_DESCONTO_VENDA_VALOR = new BigDecimal("50.00");
    /** 2% — acréscimo em item, conservador. */
    public static final BigDecimal DEFAULT_LIMITE_ACRESCIMO_ITEM_PERCENTUAL = new BigDecimal("2.00");
    /** 2% — acréscimo no total. */
    public static final BigDecimal DEFAULT_LIMITE_ACRESCIMO_VENDA_PERCENTUAL = new BigDecimal("2.00");

    // ===================================================================
    //  Getters / setters
    // ===================================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PdvCaixa getCaixa() { return caixa; }
    public void setCaixa(PdvCaixa caixa) { this.caixa = caixa; }

    public BigDecimal getLimiteDescontoItemPercentual() { return limiteDescontoItemPercentual; }
    public void setLimiteDescontoItemPercentual(BigDecimal v) { this.limiteDescontoItemPercentual = v; }

    public BigDecimal getLimiteDescontoVendaPercentual() { return limiteDescontoVendaPercentual; }
    public void setLimiteDescontoVendaPercentual(BigDecimal v) { this.limiteDescontoVendaPercentual = v; }

    public BigDecimal getLimiteDescontoVendaValor() { return limiteDescontoVendaValor; }
    public void setLimiteDescontoVendaValor(BigDecimal v) { this.limiteDescontoVendaValor = v; }

    public BigDecimal getLimiteAcrescimoItemPercentual() { return limiteAcrescimoItemPercentual; }
    public void setLimiteAcrescimoItemPercentual(BigDecimal v) { this.limiteAcrescimoItemPercentual = v; }

    public BigDecimal getLimiteAcrescimoVendaPercentual() { return limiteAcrescimoVendaPercentual; }
    public void setLimiteAcrescimoVendaPercentual(BigDecimal v) { this.limiteAcrescimoVendaPercentual = v; }

    public String getPermiteDescontoAcimaLimite() { return permiteDescontoAcimaLimite; }
    public void setPermiteDescontoAcimaLimite(String v) { this.permiteDescontoAcimaLimite = v; }

    public String getPlano() { return plano; }
    public void setPlano(String plano) { this.plano = plano; }

    public BigDecimal getPlanoValor() { return planoValor; }
    public void setPlanoValor(BigDecimal planoValor) { this.planoValor = planoValor; }

    public PlanoSituacao getPlanoSituacao() { return planoSituacao; }
    public void setPlanoSituacao(PlanoSituacao planoSituacao) { this.planoSituacao = planoSituacao; }

    public String getAcbrMonitorEndereco() { return acbrMonitorEndereco; }
    public void setAcbrMonitorEndereco(String v) { this.acbrMonitorEndereco = v; }

    public Integer getAcbrMonitorPorta() { return acbrMonitorPorta; }
    public void setAcbrMonitorPorta(Integer v) { this.acbrMonitorPorta = v; }

    public String getModuloFiscalPrincipal() { return moduloFiscalPrincipal; }
    public void setModuloFiscalPrincipal(String v) { this.moduloFiscalPrincipal = v; }

    public String getModuloFiscalContingencia() { return moduloFiscalContingencia; }
    public void setModuloFiscalContingencia(String v) { this.moduloFiscalContingencia = v; }

    public String getNomeImpressoraCupom() { return nomeImpressoraCupom; }
    public void setNomeImpressoraCupom(String v) { this.nomeImpressoraCupom = v; }

    public String getGavetaSinalInvertido() { return gavetaSinalInvertido; }
    public void setGavetaSinalInvertido(String v) { this.gavetaSinalInvertido = v; }

    public Integer getGavetaUtilizacao() { return gavetaUtilizacao; }
    public void setGavetaUtilizacao(Integer v) { this.gavetaUtilizacao = v; }

    public String getTefHiperlinkUrl() { return tefHiperlinkUrl; }
    public void setTefHiperlinkUrl(String v) { this.tefHiperlinkUrl = v; }

    public String getCaminhoImagensMarketing() { return caminhoImagensMarketing; }
    public void setCaminhoImagensMarketing(String v) { this.caminhoImagensMarketing = v; }

    public String getMarketingAtivo() { return marketingAtivo; }
    public void setMarketingAtivo(String v) { this.marketingAtivo = v; }

    /** {@code true} só se explicitamente marcado 'S' — default é desativado (ao contrário do plano). */
    public boolean marketingEstaAtivo() {
        return "S".equalsIgnoreCase(marketingAtivo);
    }

    public boolean temGaveta() {
        return gavetaUtilizacao != null && gavetaUtilizacao > 0;
    }

    /**
     * Situação do plano com fallback seguro: instalações antigas/locais que
     * ainda não têm essa coluna preenchida são tratadas como ATIVAS
     * (fail-open) — a trava só entra em vigor quando alguém explicitamente
     * marcar a situação. Ver LICENCIAMENTO.md.
     */
    public PlanoSituacao planoSituacaoOuAtivo() {
        return planoSituacao != null ? planoSituacao : PlanoSituacao.A;
    }

    // ===================================================================
    //  Helpers que respeitam null com fallback para os defaults
    // ===================================================================

    public BigDecimal limiteDescontoItemPercentualOuPadrao() {
        return limiteDescontoItemPercentual != null
                ? limiteDescontoItemPercentual
                : DEFAULT_LIMITE_DESCONTO_ITEM_PERCENTUAL;
    }

    public BigDecimal limiteDescontoVendaPercentualOuPadrao() {
        return limiteDescontoVendaPercentual != null
                ? limiteDescontoVendaPercentual
                : DEFAULT_LIMITE_DESCONTO_VENDA_PERCENTUAL;
    }

    public BigDecimal limiteDescontoVendaValorOuPadrao() {
        return limiteDescontoVendaValor != null
                ? limiteDescontoVendaValor
                : DEFAULT_LIMITE_DESCONTO_VENDA_VALOR;
    }

    public BigDecimal limiteAcrescimoItemPercentualOuPadrao() {
        return limiteAcrescimoItemPercentual != null
                ? limiteAcrescimoItemPercentual
                : DEFAULT_LIMITE_ACRESCIMO_ITEM_PERCENTUAL;
    }

    public BigDecimal limiteAcrescimoVendaPercentualOuPadrao() {
        return limiteAcrescimoVendaPercentual != null
                ? limiteAcrescimoVendaPercentual
                : DEFAULT_LIMITE_ACRESCIMO_VENDA_PERCENTUAL;
    }

    /**
     * Verifica se a loja permite passar do limite com autorização gerencial.
     * Default = true (S) se não configurado.
     */
    public boolean permiteAcimaLimiteComGerente() {
        return permiteDescontoAcimaLimite == null
                || permiteDescontoAcimaLimite.equalsIgnoreCase("S");
    }
}