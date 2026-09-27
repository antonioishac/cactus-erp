package br.com.cactusdigital.erp.loja.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Cliente final da loja (o comprador) — capturado inline no checkout (guest checkout,
 * sem login/senha nesta fatia), por {@code loja_id}. Registro de identidade/entrega
 * mínimo o suficiente pra emitir NFC-e/enviar pedido depois; conta com login
 * self-service pro cliente final acompanhar o próprio histórico é peça futura,
 * registrada no CLAUDE.md, não construída aqui.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cliente_loja")
public class ClienteLoja extends BaseEntity {

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "cpf", length = 11)
    private String cpf;
}
