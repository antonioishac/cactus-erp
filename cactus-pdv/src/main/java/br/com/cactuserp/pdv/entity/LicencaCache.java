package br.com.cactuserp.pdv.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Cache local do último contato bem-sucedido com o endpoint de
 * licenciamento da retaguarda (Cactus ERP).
 *
 * <p>Mapeia {@code LICENCA_CACHE}. Histórico append-only — {@code LicencaService}
 * sempre lê a linha mais recente. Ver {@code LICENCIAMENTO.md} na raiz do
 * projeto para o desenho completo do fluxo online/offline e da janela de
 * tolerância.
 */
@Entity
@Table(name = "LICENCA_CACHE")
public class LicencaCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "PLANO_SITUACAO", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private PlanoSituacao planoSituacao;

    @Column(name = "DATA_HORA_VERIFICACAO", nullable = false)
    private LocalDateTime dataHoraVerificacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORIGEM", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private OrigemLicencaCache origem;

    /**
     * Situação vigente imediatamente antes deste registro (última linha
     * conhecida de {@link LicencaCache}, qualquer origem). {@code null}
     * apenas na própria primeira linha já gravada na tabela.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "SITUACAO_ANTERIOR", length = 1, columnDefinition = "CHAR(1)")
    private PlanoSituacao situacaoAnterior;

    /** Hostname do dispositivo que originou a verificação (auditoria por máquina). */
    @Column(name = "DISPOSITIVO", length = 100)
    private String dispositivo;

    public LicencaCache() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PlanoSituacao getPlanoSituacao() { return planoSituacao; }
    public void setPlanoSituacao(PlanoSituacao planoSituacao) { this.planoSituacao = planoSituacao; }

    public LocalDateTime getDataHoraVerificacao() { return dataHoraVerificacao; }
    public void setDataHoraVerificacao(LocalDateTime dataHoraVerificacao) { this.dataHoraVerificacao = dataHoraVerificacao; }

    public OrigemLicencaCache getOrigem() { return origem; }
    public void setOrigem(OrigemLicencaCache origem) { this.origem = origem; }

    public PlanoSituacao getSituacaoAnterior() { return situacaoAnterior; }
    public void setSituacaoAnterior(PlanoSituacao situacaoAnterior) { this.situacaoAnterior = situacaoAnterior; }

    public String getDispositivo() { return dispositivo; }
    public void setDispositivo(String dispositivo) { this.dispositivo = dispositivo; }
}
