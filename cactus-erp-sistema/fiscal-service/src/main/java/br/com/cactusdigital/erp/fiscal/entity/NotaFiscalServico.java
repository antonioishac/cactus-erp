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
 * NFS-e composta a partir de uma {@code OsAbertura} (os-service, Fase 10) com
 * {@code orcamentoAprovado=true} — validado contra {@code fenix.sql.NFSE_CABECALHO}/
 * {@code NFSE_DETALHE}. Só os itens {@code tipo=SERVICO} da OS entram na NFS-e (itens
 * {@code tipo=PRODUTO}, peças, vão pra uma NF-e separada — mesmo padrão de mercado de
 * oficina: nota de peça e nota de serviço são documentos fiscais distintos).
 * <p>
 * <b>Emissão real via ACBr Monitor não validada nesta fase</b> — diferente do NF-e (Fase
 * 8, que reaproveitou um cliente TCP já testado pelo cactus-pdv), NFS-e via ACBr é um
 * fluxo em 2 comandos ({@code NFSe.AdicionarRPS} + envio do lote) sem nenhuma referência
 * já validada neste ecossistema, e o `ACBrMonitor.ini` local nem tem a seção
 * {@code [NFSe]} configurada (município/provedor zerados). Ver Javadoc de
 * {@code NfseIniBuilder}/{@code NotaFiscalServicoService} pro detalhe da incerteza.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "nota_fiscal_servico")
public class NotaFiscalServico extends BaseEntity {

    @Column(name = "os_abertura_id", nullable = false, unique = true)
    private Long osAberturaId;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "numero_rps", length = 15)
    private String numeroRps;

    @Column(name = "serie_rps", length = 5)
    private String serieRps;

    @Column(name = "numero", length = 15)
    private String numero;

    @Column(name = "codigo_verificacao", length = 9)
    private String codigoVerificacao;

    /** Competência no formato {@code AAAAMM} — mês de referência da prestação do serviço, não da emissão. */
    @Column(name = "competencia", length = 6)
    private String competencia;

    @Column(name = "data_emissao")
    private Instant dataEmissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "ambiente", length = 15)
    private AmbienteFiscal ambiente;

    @Builder.Default
    @Column(name = "valor_servicos", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorServicos = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "valor_iss", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorIss = BigDecimal.ZERO;

    @Column(name = "valor_liquido", precision = 18, scale = 6)
    private BigDecimal valorLiquido;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoNotaFiscal situacao;

    @Column(name = "mensagem_sefaz", length = 500)
    private String mensagemSefaz;

    @Builder.Default
    @OneToMany(mappedBy = "notaFiscalServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<NotaFiscalServicoItem> itens = new ArrayList<>();
}
