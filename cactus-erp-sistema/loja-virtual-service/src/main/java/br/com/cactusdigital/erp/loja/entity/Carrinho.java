package br.com.cactusdigital.erp.loja.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Carrinho — acessível SEM autenticação OAuth2 (o comprador é um visitante anônimo da
 * loja), a posse de {@code token} é a própria credencial de acesso. Ver CLAUDE.md,
 * "Fase 13, sub-fase 2 — decisão de segurança" pro porquê. {@code ResourceServerConfig}
 * libera {@code /api/v1/carrinhos/**} inteiro (`permitAll`) — os controllers deste
 * agregado não têm {@code @PreAuthorize} nenhum, de propósito.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "carrinho")
public class Carrinho extends BaseEntity {

    @Column(name = "loja_id", nullable = false)
    private Long lojaId;

    @Column(name = "token", nullable = false, unique = true, length = 36)
    private String token;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false, length = 15)
    private SituacaoCarrinho situacao = SituacaoCarrinho.ABERTO;

    @Builder.Default
    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemCarrinho> itens = new ArrayList<>();
}
