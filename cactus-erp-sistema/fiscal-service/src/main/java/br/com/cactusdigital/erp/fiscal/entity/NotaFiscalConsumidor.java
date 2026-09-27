package br.com.cactusdigital.erp.fiscal.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * NFC-e (modelo 65) composta a partir de um {@code Pedido} do
 * {@code loja-virtual-service} já {@code PAGO} — o cenário "venda de balcão fora do
 * PDV" já desenhado na Fase 8 (ANÁLISE: MÓDULO COMERCIAL), aqui o "balcão" é o
 * checkout da Loja Virtual. Validado campo a campo contra
 * {@code cactus-pdv.AcbrComandoFiscalBuilder} — layout {@code NFCe.INI} JÁ TESTADO
 * contra um ACBrMonitorPLUS real em produção (mais confiável que NF-e/CT-e/NFS-e,
 * que foram portados/pesquisados sem essa validação direta).
 * <p>
 * <b>Só Simples Nacional</b> (`CSOSN`, nunca `CST` de regime normal) — decisão
 * deliberada: o modo standalone da Loja Virtual não tem motor tributário completo
 * (ver Javadoc de {@code VarianteProdutoLoja}), e a esmagadora maioria dos pequenos
 * lojistas de e-commerce é Simples Nacional. Regime normal fica fora de escopo,
 * registrado — mesmo critério já usado no `CteCabecalho` (ICMS normal, sem ICMSSN).
 * <p>
 * {@code pedidoId}/{@code lojaId}/{@code empresaId}/{@code clienteLojaId} são
 * referências SOLTAS pro {@code loja-virtual-service}/{@code cadastro-base-service}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "nota_fiscal_consumidor")
public class NotaFiscalConsumidor extends BaseEntity {

    @Column(name = "pedido_id", nullable = false, unique = true)
    private Long pedidoId;

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "cliente_loja_id")
    private Long clienteLojaId;

    @Builder.Default
    @Column(name = "modelo", nullable = false, length = 2)
    private String modelo = "65";

    @Column(name = "serie", length = 3)
    private String serie;

    @Column(name = "numero")
    private Long numero;

    @Column(name = "chave_acesso", length = 44)
    private String chaveAcesso;

    @Column(name = "protocolo_autorizacao", length = 30)
    private String protocoloAutorizacao;

    @Column(name = "data_emissao")
    private Instant dataEmissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "ambiente", length = 15)
    private AmbienteFiscal ambiente;

    @Builder.Default
    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoNotaFiscal situacao;

    @Column(name = "mensagem_sefaz", length = 500)
    private String mensagemSefaz;

    @Builder.Default
    @OneToMany(mappedBy = "notaFiscalConsumidor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NotaFiscalConsumidorItem> itens = new ArrayList<>();
}
