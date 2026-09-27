package br.com.cactusdigital.erp.ged.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Catálogo de tipos de documento — {@code tamanhoMaximoMb} valida o upload de toda
 * {@link DocumentoVersao} desse tipo. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "tipo_documento")
public class TipoDocumento extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 100, unique = true)
    private String nome;

    @Column(name = "tamanho_maximo_mb", nullable = false, precision = 10, scale = 2)
    private BigDecimal tamanhoMaximoMb;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
