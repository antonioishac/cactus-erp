package br.com.cactusdigital.erp.vendas.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Venda de balcão do PDV ({@code PDV_VENDA_CABECALHO} + itens + pagamentos), chegando pela
 * sincronização (Fase 6-B, itens 5-7). Entidade PRÓPRIA, separada de {@link VendaCabecalho}
 * (decisão do usuário, 2026-10-24): a venda B2B exige vendedor cadastrado, condição de pagamento e
 * tipo de nota, nada disso existe no balcão; aqui há operador, terminal, pagamento fracionado e um
 * documento fiscal (NFC-e/SAT) já emitido pelo próprio PDV.
 * <p>
 * Imutável depois de registrada (é o espelho de um fato do PDV, não um documento editado no ERP) —
 * só ganha devoluções. <b>Não baixa estoque</b>: o PDV já manda a saída pelo {@code ESTOQUE_MOVIMENTO};
 * baixar daqui também contaria cada item duas vezes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "venda_pdv")
public class VendaPdv extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "registro_sincronizacao_id", nullable = false, unique = true)
    private Long registroSincronizacaoId;

    @Column(name = "id_origem", nullable = false, length = 100)
    private String idOrigem;

    @Column(name = "pdv_caixa_id", nullable = false)
    private Long pdvCaixaId;

    /** Instalação do PDV que enviou a venda — com {@code pdvCaixaId}, identifica o terminal. Nulo só em vendas
     * recebidas antes da V6. */
    @Column(name = "pdv_dispositivo_id", length = 30)
    private String pdvDispositivoId;

    @Column(name = "pdv_caixa_nome", length = 60)
    private String pdvCaixaNome;

    @Column(name = "pdv_operador_id")
    private Long pdvOperadorId;

    @Column(name = "cliente_id")
    private Long clienteId;

    @Column(name = "colaborador_id")
    private Long colaboradorId;

    @Column(name = "nome_cliente", length = 100)
    private String nomeCliente;

    @Column(name = "cpf_cnpj_cliente", length = 14)
    private String cpfCnpjCliente;

    @Column(name = "data_hora_venda", nullable = false)
    private Instant dataHoraVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem_venda", nullable = false, length = 10)
    private OrigemVendaPdv origemVenda;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 10)
    private SituacaoVendaPdv situacao;

    @Column(name = "tipo_operacao_fiscal", length = 3)
    private String tipoOperacaoFiscal;

    @Column(name = "status_fiscal", length = 1)
    private String statusFiscal;

    @Column(name = "chave_acesso_fiscal", length = 44)
    private String chaveAcessoFiscal;

    @Column(name = "protocolo_autorizacao", length = 30)
    private String protocoloAutorizacao;

    @Column(name = "coo")
    private Integer coo;

    @Column(name = "ccf")
    private Integer ccf;

    @Column(name = "valor_total_produtos", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotalProdutos;

    @Builder.Default
    @Column(name = "valor_desconto", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "valor_acrescimo", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorAcrescimo = BigDecimal.ZERO;

    @Column(name = "valor_final", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorFinal;

    @Column(name = "valor_recebido", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorRecebido;

    @Builder.Default
    @Column(name = "valor_troco", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTroco = BigDecimal.ZERO;

    @Column(name = "valor_base_icms", precision = 18, scale = 6)
    private BigDecimal valorBaseIcms;

    @Column(name = "valor_icms", precision = 18, scale = 6)
    private BigDecimal valorIcms;

    @Column(name = "valor_pis", precision = 18, scale = 6)
    private BigDecimal valorPis;

    @Column(name = "valor_cofins", precision = 18, scale = 6)
    private BigDecimal valorCofins;

    @Column(name = "valor_issqn", precision = 18, scale = 6)
    private BigDecimal valorIssqn;

    /** Dinheiro (tPag 01) − troco: o que entrou de fato na gaveta. É o que vai pro caixa do terminal. */
    @Builder.Default
    @Column(name = "valor_dinheiro_liquido", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorDinheiroLiquido = BigDecimal.ZERO;

    /** Preenchidos só quando a venda chegou FECHADA e depois foi cancelada ({@code CANCELAMENTO_VENDA_PDV}). Uma venda
     * que já chegou cancelada (cupom abortado antes de fechar) não tem nada disso. */
    @Column(name = "registro_cancelamento_id", unique = true)
    private Long registroCancelamentoId;

    @Column(name = "data_hora_cancelamento")
    private Instant dataHoraCancelamento;

    @Column(name = "pdv_operador_cancelamento_id")
    private Long pdvOperadorCancelamentoId;

    @Column(name = "pdv_gerente_cancelamento_id")
    private Long pdvGerenteCancelamentoId;

    @Column(name = "justificativa_cancelamento", length = 255)
    private String justificativaCancelamento;

    @Column(name = "protocolo_cancelamento", length = 30)
    private String protocoloCancelamento;

    @Builder.Default
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VendaPdvItem> itens = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VendaPdvPagamento> pagamentos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VendaPdvDevolucao> devolucoes = new ArrayList<>();
}
