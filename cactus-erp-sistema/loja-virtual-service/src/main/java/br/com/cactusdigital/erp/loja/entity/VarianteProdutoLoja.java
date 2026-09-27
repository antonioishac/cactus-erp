package br.com.cactusdigital.erp.loja.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Variante — o SKU vendável de fato (ex: "Vestido Floral Midi — Tamanho M, Azul",
 * "Vinho Tinto Reserva 2021 — 750ml"). Carrega preço/estoque/GTIN PRÓPRIOS; a
 * combinação de valores em {@link VarianteAtributoValor} é o que a diferencia de
 * outras variantes do mesmo {@link ProdutoLoja}.
 * <p>
 * <b>Estoque simplificado, decisão deliberada desta fatia</b>: {@code estoqueQuantidade}
 * é um contador flat (não o ledger append-only + múltiplos depósitos do
 * {@code controle-estoque-service}) — coerente com o "ERP mínimo embutido" do modo
 * standalone (ver CLAUDE.md, ANÁLISE: LOJA VIRTUAL B2C): um lojista pequeno não precisa
 * de multi-depósito/lote/série pra vender online. Quando o modo canal for construído
 * (lojista com ERP completo por trás), o saldo real passa a vir do
 * {@code controle-estoque-service} do cliente — este campo fica reservado pro modo
 * standalone.
 * <p>
 * <b>Reserva real (Fase 13, sub-fase 4)</b>: {@code estoqueReservado} é a quantidade
 * comprometida por carrinho {@code ABERTO} ou {@code Pedido} ainda não cancelado/entregue
 * — {@link #getEstoqueDisponivel()} é sempre {@code estoqueQuantidade - estoqueReservado}.
 * Toda mutação de um dos dois campos passa por {@code VarianteProdutoLojaService}, nunca
 * um {@code setEstoqueQuantidade}/{@code setEstoqueReservado} direto de fora do pacote —
 * e sempre sob {@code VarianteProdutoLojaRepository#buscarParaAtualizarEstoque} (lock
 * pessimista), mesmo critério de dinheiro já usado em
 * {@code financeiro-service.ContaBancariaRepository#buscarParaAtualizarSaldo}, pra não
 * permitir lost-update entre dois carrinhos concorrentes disputando a mesma variante.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "variante_produto_loja")
public class VarianteProdutoLoja extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_loja_id", nullable = false)
    private ProdutoLoja produtoLoja;

    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @Column(name = "gtin", length = 20)
    private String gtin;

    @Column(name = "preco_venda", nullable = false, precision = 18, scale = 6)
    private BigDecimal precoVenda;

    @Column(name = "preco_promocional", precision = 18, scale = 6)
    private BigDecimal precoPromocional;

    @Builder.Default
    @Column(name = "estoque_quantidade", nullable = false, precision = 18, scale = 6)
    private BigDecimal estoqueQuantidade = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "estoque_reservado", nullable = false, precision = 18, scale = 6)
    private BigDecimal estoqueReservado = BigDecimal.ZERO;

    @Column(name = "peso_kg", precision = 10, scale = 3)
    private BigDecimal pesoKg;

    /**
     * Campos fiscais mínimos pra emitir NFC-e (Fase 13, sub-fase 3) — deliberadamente
     * rasos comparado ao motor tributário completo do {@code cadastro-base-service}
     * (sem ICMS-ST/DIFAL/redução de base): o modo standalone assume Simples Nacional
     * (o regime da esmagadora maioria dos pequenos lojistas de e-commerce), então só
     * {@code csosn} é usado — nunca CST de regime normal. Nullable: uma variante sem
     * esses campos preenchidos simplesmente não pode gerar NFC-e ainda (validado em
     * {@code NotaFiscalConsumidorService}).
     */
    @Column(name = "ncm", length = 8)
    private String ncm;

    @Column(name = "csosn", length = 3)
    private String csosn;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;

    @Builder.Default
    @OneToMany(mappedBy = "varianteProdutoLoja", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VarianteAtributoValor> valoresAtributos = new ArrayList<>();

    /** Derivado, não persistido — MapStruct mapeia pra {@code VarianteProdutoLojaResponse.estoqueDisponivel} por nome de getter. */
    public BigDecimal getEstoqueDisponivel() {
        return estoqueQuantidade.subtract(estoqueReservado);
    }
}
