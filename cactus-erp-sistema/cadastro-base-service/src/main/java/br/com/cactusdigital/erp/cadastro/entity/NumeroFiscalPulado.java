package br.com.cactusdigital.erp.cadastro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Ledger append-only — registra um número de documento fiscal que foi RESERVADO (via
 * {@code EmpresaConfiguracaoFiscalService#reservarProximoNumeroXxx}) mas nunca chegou a
 * virar um documento real, porque um passo POSTERIOR da saga que o consome
 * (ver {@code common.saga.SagaOrchestrator} em {@code fiscal-service.NotaFiscalEletronicaService})
 * falhou.
 * <p>
 * <b>Deliberadamente NÃO tenta "devolver" o número pro contador</b> (nunca decrementa
 * {@code proximoNumeroNfe}/etc.) — isso seria arriscado de verdade: se uma segunda
 * reserva concorrente já aconteceu depois desta, decrementar causaria uma COLISÃO (duas
 * notas fiscais reais com o mesmo número), um problema muito mais grave que um número
 * pulado. Além disso, a legislação fiscal brasileira já exige que número pulado seja
 * regularizado via evento formal de <b>inutilização</b> junto à SEFAZ — não um reuso
 * silencioso. Esta tabela é o registro que torna esse gap CONSULTÁVEL (pra alguém
 * disparar a inutilização manualmente) em vez de só existir como uma linha de log
 * perdida — mesmo espírito de {@code RegistroSincronizacao}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "numero_fiscal_pulado")
public class NumeroFiscalPulado extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private TipoDocumentoFiscal tipoDocumento;

    @Column(name = "serie", length = 3)
    private String serie;

    @Column(name = "numero", nullable = false)
    private Long numero;

    @Column(name = "motivo", length = 500)
    private String motivo;
}
