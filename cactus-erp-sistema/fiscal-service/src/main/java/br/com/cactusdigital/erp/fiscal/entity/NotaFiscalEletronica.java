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
 * NF-e (modelo 55) composta a partir de uma {@code VendaCabecalho} (vendas-service, Fase
 * 7) — validado contra {@code fenix.sql.NFE_CABECALHO}/{@code NFE_DETALHE} (82/38 colunas
 * na fonte; esta é uma modelagem pragmática cobrindo o essencial pra uma venda de
 * mercadoria comum: emitente/destinatário/itens/impostos/total, sem os cenários satélite
 * do fenix — importação, cana-de-açúcar, veículo, medicamento, armamento, combustível —
 * que ficam fora de escopo, registrados no CLAUDE.md).
 * <p>
 * {@code vendaId}/{@code empresaId}/{@code clienteId}/{@code transportadoraId} são
 * referências SOLTAS pro {@code vendas-service}/{@code cadastro-base-service} — resolvidas
 * uma única vez em {@code NotaFiscalEletronicaService#gerarDeVenda}, não a cada leitura.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "nota_fiscal_eletronica")
public class NotaFiscalEletronica extends BaseEntity {

    @Column(name = "venda_id", nullable = false, unique = true)
    private Long vendaId;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "transportadora_id")
    private Long transportadoraId;

    @Builder.Default
    @Column(name = "modelo", nullable = false, length = 2)
    private String modelo = "55";

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
    @OneToMany(mappedBy = "notaFiscal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NotaFiscalEletronicaItem> itens = new ArrayList<>();
}
