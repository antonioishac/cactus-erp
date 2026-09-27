package br.com.cactusdigital.erp.os.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Linha do tempo de atendimento (ex: "peça encomendada", "cliente avisado") — validado contra {@code fenix.sql.OS_EVOLUCAO}. Append-only: nunca editado/removido depois de criado. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "os_evolucao")
public class OsEvolucao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_abertura_id", nullable = false)
    private OsAbertura osAbertura;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    @Column(name = "hora_registro", length = 8)
    private String horaRegistro;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @Builder.Default
    @Column(name = "enviar_email", nullable = false)
    private boolean enviarEmail = false;
}
