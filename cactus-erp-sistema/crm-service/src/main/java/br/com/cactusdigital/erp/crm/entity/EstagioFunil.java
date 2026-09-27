package br.com.cactusdigital.erp.crm.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Estágio do funil de vendas — catálogo LIVRE (mesmo critério de {@code OsStatus} do
 * os-service): o funil real varia por empresa/vertical ("Prospecção"/"Qualificação"/
 * "Proposta"/"Negociação" numa consultoria B2B é diferente de "Contato inicial"/
 * "Visita agendada"/"Fechamento" numa loja física), então não é um enum fixo.
 * {@code ordem} organiza a exibição em Kanban (menor = mais à esquerda/início do funil).
 * {@code tipoEncerramento} é o que diferencia isto de um catálogo passivo — determina o
 * efeito colateral de {@code OportunidadeService#moverEstagio} quando uma oportunidade é
 * movida pra este estágio (ver Javadoc de {@link TipoEncerramentoFunil}).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "estagio_funil")
public class EstagioFunil extends BaseEntity {

    @Column(name = "codigo", length = 30)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "ordem", nullable = false)
    private Integer ordem;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_encerramento", nullable = false, length = 10)
    private TipoEncerramentoFunil tipoEncerramento = TipoEncerramentoFunil.ABERTO;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
