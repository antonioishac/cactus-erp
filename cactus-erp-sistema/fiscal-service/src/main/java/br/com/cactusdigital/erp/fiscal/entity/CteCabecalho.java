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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * CT-e (modelo 57, modal Rodoviário) composto a partir do frete de uma {@code
 * VendaCabecalho}+{@code VendaFrete} (vendas-service, Fase 7) — validado contra
 * {@code fenix.sql.CTE_CABECALHO} (73 colunas) + ~25 tabelas satélite na fonte; esta é
 * uma modelagem pragmática, mesmo critério já usado em {@code NotaFiscalEletronica}
 * (Fase 8) e {@code NotaFiscalServico} (Fase 9): cobre o cenário dominante de mercado
 * (transporte rodoviário de carga fracionada, prestador único, sem intermediários) e
 * deixa o resto registrado como fora de escopo.
 * <p>
 * <b>Fora de escopo desta fase, registrado, não improvisado</b>: modais Aéreo/
 * Aquaviário/Ferroviário/Dutoviário/Multimodal (só Rodoviário — {@code CTE_RODOVIARIO}
 * — está coberto; os outros 4 modais do fenix, cada um com layout próprio, ficam pra
 * quando houver necessidade real); Expedidor/Recebedor (intermediários — só Remetente
 * (=empresa emitente) e Destinatário (=cliente da venda) são modelados, cobrindo
 * transporte direto remetente→destinatário, o caso dominante); CTE_FATURA/CTE_DUPLICATA
 * (cobrança — mesmo critério já usado pra NF-e, sem seção {@code [cobr]}); CTE_SEGURO/
 * CTE_PERIGOSO/CTE_VEICULO_NOVO/CTE_DOCUMENTO_ANTERIOR/CTE_INFORMACAO_NF_* (vínculo
 * com a(s) NF-e(s) transportada(s) — relevante, mas outro corte de escopo); cancelamento/
 * CT-e complementar/anulação/substituto ({@code tipoCte} sempre Normal); EPEC/
 * contingência.
 * <p>
 * {@code vendaId}/{@code empresaId}/{@code clienteId}/{@code transportadoraId} são
 * referências SOLTAS pro {@code vendas-service}/{@code cadastro-base-service}. Campos de
 * {@code CTE_RODOVIARIO} (rntrc/dataPrevistaEntrega/indicadorLotacao/ciot) e de placa/peso/
 * volume (que no fenix vivem em tabelas satélite do modal) estão FLATTENED direto nesta
 * entidade — mesma simplificação já aplicada aos itens de {@code NotaFiscalEletronica}
 * (que achataram ICMS/IPI/PIS/COFINS em vez de tabelas satélite por imposto), porque só
 * um modal é suportado, não há motivo pra uma entidade 1:1 separada.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cte_cabecalho")
public class CteCabecalho extends BaseEntity {

    @Column(name = "venda_id", nullable = false, unique = true)
    private Long vendaId;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    /** Cliente da venda — destinatário da carga. */
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "transportadora_id", nullable = false)
    private Long transportadoraId;

    @Builder.Default
    @Column(name = "modelo", nullable = false, length = 2)
    private String modelo = "57";

    /** Só Rodoviário suportado nesta fase — ver Javadoc da classe. */
    @Builder.Default
    @Column(name = "modal", nullable = false, length = 2)
    private String modal = "01";

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

    @Enumerated(EnumType.STRING)
    @Column(name = "tomador", nullable = false, length = 15)
    private TipoTomadorCte tomador;

    @Builder.Default
    @Column(name = "natureza_operacao", length = 60)
    private String naturezaOperacao = "Prestação de serviço de transporte";

    /** {@code null} = usa o padrão fixo do {@code CteIniBuilder} (5353, intra-UF). */
    @Column(name = "cfop", length = 4)
    private String cfop;

    @Column(name = "produto_predominante", length = 60)
    private String produtoPredominante;

    @Column(name = "observacoes_gerais", columnDefinition = "TEXT")
    private String observacoesGerais;

    // --- Rodoviário (CTE_RODOVIARIO, achatado — ver Javadoc) ---
    @Column(name = "rntrc", length = 8)
    private String rntrc;

    @Column(name = "data_prevista_entrega")
    private LocalDate dataPrevistaEntrega;

    /** 0-Não; 1-Sim — ver comentário de {@code CTE_RODOVIARIO.INDICADOR_LOTACAO} no fenix. */
    @Column(name = "indicador_lotacao", length = 1)
    private String indicadorLotacao;

    @Column(name = "ciot")
    private String ciot;

    @Column(name = "placa", length = 7)
    private String placa;

    @Column(name = "uf_placa", length = 2)
    private String ufPlaca;

    @Column(name = "peso_bruto", precision = 18, scale = 6)
    private BigDecimal pesoBruto;

    @Column(name = "quantidade_volume", precision = 18, scale = 6)
    private BigDecimal quantidadeVolume;

    // --- Valores/ICMS ---
    @Builder.Default
    @Column(name = "valor_total_servico", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotalServico = BigDecimal.ZERO;

    @Column(name = "valor_receber", precision = 18, scale = 6)
    private BigDecimal valorReceber;

    @Column(name = "valor_total_carga", precision = 18, scale = 6)
    private BigDecimal valorTotalCarga;

    @Column(name = "cst", length = 2)
    private String cst;

    @Column(name = "base_calculo_icms", precision = 18, scale = 6)
    private BigDecimal baseCalculoIcms;

    @Column(name = "aliquota_icms", precision = 18, scale = 6)
    private BigDecimal aliquotaIcms;

    @Column(name = "valor_icms", precision = 18, scale = 6)
    private BigDecimal valorIcms;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoNotaFiscal situacao;

    @Column(name = "mensagem_sefaz", length = 500)
    private String mensagemSefaz;

    @Builder.Default
    @OneToMany(mappedBy = "cteCabecalho", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CteComponente> componentes = new ArrayList<>();
}
