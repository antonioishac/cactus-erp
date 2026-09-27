package br.com.cactusdigital.erp.os.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Equipamento vinculado a uma {@link OsAbertura} — validado contra
 * {@code fenix.sql.OS_ABERTURA_EQUIPAMENTO}. {@code tipoCobertura} fica como {@code String}
 * crua (não virou enum) — mesmo critério já usado pra {@code Produto.pafPSt}: domínio de
 * valores real (garantia/fora de garantia?) não confirmado contra documentação, não
 * inventar enum sem certeza.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "os_abertura_equipamento")
public class OsAberturaEquipamento extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_abertura_id", nullable = false)
    private OsAbertura osAbertura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "os_equipamento_id", nullable = false)
    private OsEquipamento osEquipamento;

    @Column(name = "numero_serie", length = 50)
    private String numeroSerie;

    @Column(name = "tipo_cobertura", length = 1)
    private String tipoCobertura;
}
