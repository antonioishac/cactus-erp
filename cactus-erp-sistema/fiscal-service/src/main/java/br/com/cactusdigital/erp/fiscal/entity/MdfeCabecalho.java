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
 * MDF-e (Manifesto Eletrônico de Documentos Fiscais, modelo 58) — agrupa N {@link CteCabecalho}
 * já {@code AUTORIZADA} (Fase 11) numa única viagem, via ACBr Monitor. Greenfield puro: nem
 * {@code fenix.sql} nem {@code BANCO_PDV.md} têm qualquer tabela de MDF-e (confirmado por grep
 * exaustivo, ver ANÁLISE: MÓDULO CONTÁBIL E FISCAL no CLAUDE.md raiz) — desenho por pesquisa
 * geral do leiaute oficial (SPED/RFB) e do {@code MDFe.INI}/comando do ACBr Monitor, mesmo nível
 * de honestidade já usado em {@code NfeIniBuilder}/{@code CteIniBuilder}/{@code NfseIniBuilder}.
 * <p>
 * <b>Recorte de escopo, registrado — mesma disciplina de corte pragmático do CT-e (Fase 11)</b>:
 * <ul>
 *   <li>Só modal Rodoviário ({@code modal="01"});</li>
 *   <li>Só viagem <b>intra-UF</b> — {@link #uf} é a UF de início E fim, sem percurso por outras
 *   UFs. Decorre diretamente da simplificação já em vigor no CT-e desta sessão (só CFOP interno,
 *   5353, nunca interestadual) — se todo CT-e vinculado nasceu intra-UF, o MDF-e que os agrupa
 *   também é;</li>
 *   <li>Um único veículo de tração, sem reboque/carreta nem troca de condutor/veículo em trânsito;</li>
 *   <li>Sem seguro de carga, sem produto perigoso, sem vale-pedágio, sem lacres;</li>
 *   <li>Sem <b>encerramento</b> (evento {@code MDFE.EncerrarMDFe}, legalmente obrigatório ao fim
 *   da viagem) nem cancelamento/complementar — fica registrado como próxima sub-fase, não
 *   meio-implementado agora;</li>
 *   <li>Nunca invocado contra o ACBr Monitor real nesta sessão — mesma decisão de segurança já em
 *   vigor pras Fases 8/9/11 (certificado de produção configurado, sem homologação disponível).</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "mdfe_cabecalho")
public class MdfeCabecalho extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Builder.Default
    @Column(name = "modelo", length = 2, nullable = false)
    private String modelo = "58";

    @Builder.Default
    @Column(name = "modal", length = 2, nullable = false)
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

    /** UF de início E fim da viagem — ver corte de escopo "só intra-UF" no Javadoc da classe. */
    @Column(name = "uf", length = 2, nullable = false)
    private String uf;

    @Column(name = "rntrc", length = 8)
    private String rntrc;

    @Column(name = "veiculo_placa", length = 7)
    private String veiculoPlaca;

    @Column(name = "veiculo_uf", length = 2)
    private String veiculoUf;

    @Column(name = "veiculo_renavam", length = 11)
    private String veiculoRenavam;

    @Column(name = "veiculo_tara_kg", precision = 18, scale = 6)
    private BigDecimal veiculoTaraKg;

    @Column(name = "veiculo_capacidade_kg", precision = 18, scale = 6)
    private BigDecimal veiculoCapacidadeKg;

    @Column(name = "condutor_nome", length = 60)
    private String condutorNome;

    @Column(name = "condutor_cpf", length = 11)
    private String condutorCpf;

    @Column(name = "municipio_carregamento_codigo_ibge")
    private Integer municipioCarregamentoCodigoIbge;

    @Column(name = "municipio_carregamento_nome", length = 60)
    private String municipioCarregamentoNome;

    @Column(name = "data_inicio_viagem")
    private LocalDate dataInicioViagem;

    @Column(name = "valor_total_carga", precision = 18, scale = 6)
    private BigDecimal valorTotalCarga;

    @Column(name = "peso_bruto_total", precision = 18, scale = 6)
    private BigDecimal pesoBrutoTotal;

    /** {@code cUnid} da SEFAZ: 01=KG (default, único suportado nesta fase); 02=TON não modelado. */
    @Builder.Default
    @Column(name = "unidade_medida_carga", length = 2, nullable = false)
    private String unidadeMedidaCarga = "01";

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoNotaFiscal situacao;

    @Column(name = "mensagem_sefaz", length = 500)
    private String mensagemSefaz;

    // --- Evento de Encerramento (110112) ---
    @Column(name = "data_encerramento")
    private LocalDate dataEncerramento;

    @Column(name = "municipio_encerramento_codigo_ibge")
    private Integer municipioEncerramentoCodigoIbge;

    @Column(name = "protocolo_encerramento", length = 30)
    private String protocoloEncerramento;

    // --- Evento de Cancelamento (110111) ---
    @Column(name = "data_cancelamento")
    private Instant dataCancelamento;

    @Column(name = "justificativa_cancelamento", length = 255)
    private String justificativaCancelamento;

    @Column(name = "protocolo_cancelamento", length = 30)
    private String protocoloCancelamento;

    @Builder.Default
    @OneToMany(mappedBy = "mdfeCabecalho", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MdfeDocumentoVinculado> documentos = new ArrayList<>();
}
