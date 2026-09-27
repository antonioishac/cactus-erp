package br.com.cactusdigital.erp.estoque.entity;

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
 * Depósito/armazém de uma empresa/filial. Saldo é sempre por produto×depósito
 * ({@link SaldoEstoque}) — este serviço nasce multi-depósito desde o início, mesmo que
 * o cenário mais comum no dia a dia seja "1 depósito principal por filial" (nem o
 * cactus-pdv nem o fenix.sql legado modelam mais de um local de estoque por produto;
 * essa é uma decisão do Cactus ERP pra competir com sistemas de mercado que já esperam
 * múltiplos armazéns/centros de distribuição).
 * <p>
 * {@code empresaId} é referência solta (sem FK) — {@code Empresa} mora no
 * cadastro-base-service, em outro schema.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "deposito")
public class Deposito extends BaseEntity {

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoDeposito tipo;

    @Builder.Default
    @Column(name = "situacao", nullable = false, length = 1)
    private Situacao situacao = Situacao.ATIVO;
}
