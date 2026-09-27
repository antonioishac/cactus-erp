package br.com.cactusdigital.erp.crm.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
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
 * Lead — contato ainda não qualificado como cliente. Greenfield, sem equivalente no
 * fenix.sql/BANCO_PDV.md (confirmado por grep exaustivo — nenhuma tabela de CRM existe
 * em nenhuma das duas fontes). {@code vendedorId} é referência SOLTA pro
 * {@code Vendedor} do vendas-service; {@code clienteId} é referência SOLTA pro
 * {@code Cliente} do cadastro-base-service, preenchida só na conversão
 * ({@link SituacaoLead#CONVERTIDO}).
 * <p>
 * <b>Decisão de desenho</b>: converter um Lead NÃO cria um {@code Cliente} automaticamente
 * no cadastro-base-service — a conversão só vincula um {@code clienteId} JÁ existente
 * (criado separadamente, com os dados fiscais completos que um Lead nunca captura:
 * CPF/CNPJ, endereço, IE). Mesmo critério de nunca fazer escrita cross-serviço síncrona
 * sem necessidade real — aqui bastaria uma leitura de validação (não implementada nesta
 * v1, registrado no CLAUDE.md).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "crm_lead") // "lead" é palavra reservada no MySQL 8.0.2+ (função de janela LEAD/LAG) — achado real ao subir a migration
public class Lead extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "nome_empresa", length = 120)
    private String nomeEmpresa;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem", length = 15)
    private OrigemLead origem;

    @Column(name = "vendedor_id")
    private Long vendedorId;

    /** Preenchido só quando {@code situacao=CONVERTIDO} — ver Javadoc da classe. */
    @Column(name = "cliente_id")
    private Long clienteId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoLead situacao = SituacaoLead.NOVO;

    @Column(name = "motivo_desqualificacao", length = 250)
    private String motivoDesqualificacao;

    @Column(name = "observacao", length = 500)
    private String observacao;
}
