package br.com.cactusdigital.erp.sincronizacao.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Ledger de auditoria de sincronização — fonte da verdade sobre um registro de push do
 * PDV, sobrevive independente do RabbitMQ (a fila é complementar, ver
 * FASE6_SINCRONIZACAO_PDV.md seção 6.3). {@code payload} guarda o JSON original
 * recebido do PDV (necessário pro reprocessamento manual — ver
 * {@code ReprocessamentoService} — republicar sem precisar que o PDV reenvie).
 * <p>
 * {@code chaveIdempotencia} (= {@code lojaId:dispositivoId:tipoRegistro:idOrigem}) é
 * {@code UNIQUE} — é isso que resolve o cenário de reenvio duplicado (seção 5.3/8.3):
 * reenviar o mesmo {@code idOrigem} devolve o resultado já registrado, sem duplicar
 * processamento.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "registro_sincronizacao",
        uniqueConstraints = @UniqueConstraint(name = "uk_registro_sincronizacao_idempotencia", columnNames = "chave_idempotencia"))
public class RegistroSincronizacao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteSincronizacao lote;

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @Column(name = "dispositivo_id", nullable = false, length = 30)
    private String dispositivoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro", nullable = false, length = 30)
    private TipoRegistroSincronizacao tipoRegistro;

    @Column(name = "id_origem", nullable = false, length = 60)
    private String idOrigem;

    @Column(name = "chave_idempotencia", nullable = false, length = 150)
    private String chaveIdempotencia;

    // Sem @Lob: achado ao subir ao vivo — @Lob numa String sem `length` explícito faz o
    // Hibernate assumir o length padrão do JPA (255) pra decidir o tipo CLOB equivalente,
    // e o MySQLDialect mapeia isso pra TINYTEXT — batendo com a migration (LONGTEXT) só
    // por acaso em bases pequenas, e falhando a validação de schema aqui. columnDefinition
    // explícito remove a ambiguidade.
    @Column(name = "payload", nullable = false, columnDefinition = "LONGTEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private StatusRegistroSincronizacao status;

    @Builder.Default
    @Column(name = "tentativas", nullable = false)
    private int tentativas = 0;

    @Column(name = "motivo_erro", length = 500)
    private String motivoErro;

    @Column(name = "usuario_reprocessou_id")
    private Long usuarioReprocessouId;

    @Column(name = "data_reprocessamento")
    private Instant dataReprocessamento;
}
