package br.com.cactusdigital.erp.fiscal.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Pedido de inutilização de uma faixa de numeração de NF-e (modelo 55) — o jeito legal de regularizar número que foi
 * reservado e nunca virou documento (ver {@code NumeroFiscalPulado} no cadastro-base-service). Toda tentativa fica
 * registrada, homologada ou não: é trilha de auditoria fiscal, não se apaga.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "inutilizacao_numeracao_nfe")
public class InutilizacaoNumeracaoNfe extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Builder.Default
    @Column(name = "modelo", nullable = false, length = 2)
    private String modelo = "55";

    @Column(name = "serie", nullable = false, length = 3)
    private String serie;

    @Column(name = "numero_inicial", nullable = false)
    private Long numeroInicial;

    @Column(name = "numero_final", nullable = false)
    private Long numeroFinal;

    /** Ano com 2 dígitos, como vai no pedido. */
    @Column(name = "ano", nullable = false)
    private Short ano;

    @Enumerated(EnumType.STRING)
    @Column(name = "ambiente", nullable = false, length = 15)
    private AmbienteFiscal ambiente;

    @Column(name = "justificativa", nullable = false, length = 255)
    private String justificativa;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoInutilizacao situacao;

    @Column(name = "protocolo", length = 20)
    private String protocolo;

    @Column(name = "codigo_status_sefaz", length = 3)
    private String codigoStatusSefaz;

    @Column(name = "mensagem_sefaz", length = 500)
    private String mensagemSefaz;

    @Column(name = "data_homologacao")
    private Instant dataHomologacao;

    /** retInutNFe assinado pela SEFAZ. Lazy: a listagem não precisa dele. */
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "xml_retorno", columnDefinition = "TEXT")
    private String xmlRetorno;
}
