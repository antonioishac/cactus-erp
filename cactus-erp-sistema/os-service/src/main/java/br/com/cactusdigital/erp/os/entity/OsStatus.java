package br.com.cactusdigital.erp.os.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Status de OS — validado contra {@code fenix.sql.OS_STATUS}: catálogo livre (não enum
 * fixo), porque o ciclo de vida real de uma OS varia por vertical de negócio (oficina,
 * assistência técnica, autoelétrica) — "Aguardando peça", "Em orçamento", "Aprovado pelo
 * cliente" etc. não são estados universais como RASCUNHO/CONFIRMADA de Venda. Mesmo
 * espírito de {@code NotaFiscalModelo} (catálogo simples, gerenciável).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "os_status")
public class OsStatus extends BaseEntity {

    @Column(name = "codigo", length = 2)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
