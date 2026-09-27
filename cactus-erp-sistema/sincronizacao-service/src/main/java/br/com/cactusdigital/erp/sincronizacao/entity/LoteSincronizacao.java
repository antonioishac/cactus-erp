package br.com.cactusdigital.erp.sincronizacao.entity;

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
 * Envelope de um {@code POST /sync/lotes} — agrupador dos {@link RegistroSincronizacao}
 * recebidos numa mesma chamada. {@code lojaId}/{@code dispositivoId} nunca reaproveitam
 * o id local do PDV como chave primária aqui (já registrado no CLAUDE.md) — são só
 * atributos de rastreio, a PK é a de sempre ({@code BaseEntity}, auto-increment).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "lote_sincronizacao")
public class LoteSincronizacao extends BaseEntity {

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @Column(name = "dispositivo_id", nullable = false, length = 30)
    private String dispositivoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro", nullable = false, length = 30)
    private TipoRegistroSincronizacao tipoRegistro;

    @Column(name = "total_registros", nullable = false)
    private int totalRegistros;

    @Column(name = "total_aceitos", nullable = false)
    private int totalAceitos;

    @Column(name = "total_rejeitados", nullable = false)
    private int totalRejeitados;
}
