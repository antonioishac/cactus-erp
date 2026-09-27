package br.com.cactusdigital.erp.loja.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Loja — o TENANT da plataforma white-label multi-nicho (ver CLAUDE.md, ANÁLISE: LOJA
 * VIRTUAL B2C). Diferente de {@code Empresa} (cadastro-base-service, que cobre
 * matriz/filial de UM cliente-ERP), {@code Loja} isola lojistas DIFERENTES e
 * mutuamente estranhos dentro de uma implantação compartilhada — toda tabela de
 * catálogo/pedido deste serviço carrega {@code loja_id}, mesmo padrão de
 * discriminador que {@code empresa_id} já cumpre hoje pra multi-filial.
 * <p>
 * {@code subdominio} é a chave de roteamento white-label (ex: "boutique-maria" →
 * boutique-maria.cactusloja.com.br) — resolução de tenant por subdomínio/domínio
 * próprio fica pra quando o BFF de storefront for construído (fora de escopo desta
 * fatia, que só cobre administração de catálogo).
 * <p>
 * <b>Modelo de monetização (decisão do usuário, 2026-09-04)</b>: SaaS por assinatura
 * fixa — {@code planoAssinatura}/{@code situacaoAssinatura} são metadado simples,
 * SEM motor de cobrança recorrente nem split de pagamento (a Cactus não participa do
 * valor de cada venda; o pagamento do cliente final vai 100% pro lojista via as
 * credenciais de gateway PRÓPRIAS dele — configuração de gateway fica pra quando o
 * checkout for construído, fora de escopo desta fatia).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "loja")
public class Loja extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "razao_social", length = 150)
    private String razaoSocial;

    @Column(name = "cnpj", length = 14)
    private String cnpj;

    /**
     * Referência SOLTA pro {@code Empresa} do cadastro-base-service — resolve qual
     * {@code EmpresaConfiguracaoFiscal} (certificado/série/ambiente) usar pra emitir
     * NFC-e dos pedidos desta loja (Fase 13, sub-fase 3). Nullable — nem toda loja tem
     * emissão fiscal configurada ainda (o modo standalone puro, catálogo+carrinho, não
     * exige isso pra funcionar).
     */
    @Column(name = "empresa_id")
    private Long empresaId;

    /** Chave de roteamento white-label — ver Javadoc da classe. */
    @Column(name = "subdominio", nullable = false, unique = true, length = 60)
    private String subdominio;

    @Column(name = "email_contato", length = 120)
    private String emailContato;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "plano_assinatura", nullable = false, length = 15)
    private PlanoAssinatura planoAssinatura = PlanoAssinatura.BASICO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_assinatura", nullable = false, length = 15)
    private SituacaoAssinatura situacaoAssinatura = SituacaoAssinatura.ATIVA;

    @Column(name = "data_inicio_assinatura")
    private LocalDate dataInicioAssinatura;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
