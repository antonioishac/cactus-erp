package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Configuração fiscal por filial — certificado digital, série/numeração de NFe/NFCe/NFSe,
 * ambiente SEFAZ e CSC (Código de Segurança do Contribuinte, usado no QRCode da NFC-e) —
 * ver ANÁLISE: EMPRESA MULTI-FILIAL no CLAUDE.md. Isso é o que a integração real com a
 * SEFAZ (via ACBr, que o cactus-pdv já usa) precisa; a emissão fiscal em si é módulo
 * futuro, aqui só fica o cadastro da configuração.
 * <p>
 * <b>Segurança</b>: só metadado fica aqui — {@code certificadoAlias} aponta pra uma
 * entrada num keystore/secret manager gerenciado fora do banco (mesmo padrão do
 * {@code cactus.security.jwt-keystore} do Authorization Server); a senha do certificado
 * NUNCA é persistida nesta tabela.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "empresa_configuracao_fiscal")
public class EmpresaConfiguracaoFiscal extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false, unique = true)
    private Empresa empresa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_certificado", length = 2)
    private TipoCertificadoDigital tipoCertificado;

    /** Alias da entrada no keystore/secret manager — não é a senha nem o arquivo do certificado. */
    @Column(name = "certificado_alias", length = 100)
    private String certificadoAlias;

    @Column(name = "certificado_validade")
    private LocalDate certificadoValidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "ambiente_fiscal", nullable = false, length = 20)
    private AmbienteFiscal ambienteFiscal;

    @Column(name = "serie_nfe", length = 3)
    private String serieNfe;

    @Column(name = "proximo_numero_nfe")
    private Long proximoNumeroNfe;

    @Column(name = "serie_nfce", length = 3)
    private String serieNfce;

    @Column(name = "proximo_numero_nfce")
    private Long proximoNumeroNfce;

    @Column(name = "serie_nfse", length = 3)
    private String serieNfse;

    @Column(name = "proximo_numero_nfse")
    private Long proximoNumeroNfse;

    /** Numeração de CT-e (modelo 57) — mesmo padrão de {@code serieNfe}/{@code proximoNumeroNfe}, consumido pelo {@code fiscal-service} (Fase 11). */
    @Column(name = "serie_cte", length = 3)
    private String serieCte;

    @Column(name = "proximo_numero_cte")
    private Long proximoNumeroCte;

    /** Numeração de MDF-e (modelo 58) — mesmo padrão de {@code serieCte}/{@code proximoNumeroCte}, consumido pelo {@code fiscal-service} (Fase 16). */
    @Column(name = "serie_mdfe", length = 3)
    private String serieMdfe;

    @Column(name = "proximo_numero_mdfe")
    private Long proximoNumeroMdfe;

    /** CSC (Código de Segurança do Contribuinte) usado no QRCode da NFC-e. */
    @Column(name = "csc", length = 100)
    private String csc;

    /** Id do CSC — a SEFAZ associa um identificador a cada código de segurança emitido. */
    @Column(name = "csc_id_token", length = 10)
    private String cscIdToken;
}
