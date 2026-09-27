package br.com.cactusdigital.erp.fiscal.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Catálogo de item da lista de serviço municipal — validado contra
 * {@code fenix.sql.NFSE_LISTA_SERVICO}. ISS varia por município (~5.500 no Brasil, cada
 * um com a própria tabela de código/alíquota) — diferente de NCM/CFOP (nacionais), não
 * dá pra semear um catálogo único; a empresa cadastra os itens que usa nos municípios
 * onde opera. {@code aliquotaIssPadrao} é o "resolvido padrão" cacheado aqui (mesmo
 * espírito do `Produto.aliquotaIcmsPadrao` no cadastro-base) — sem motor de resolução
 * completo por município nesta fase, simplificação registrada.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "lista_servico_municipal")
public class ListaServicoMunicipal extends BaseEntity {

    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    @Column(name = "descricao", nullable = false, length = 250)
    private String descricao;

    @Column(name = "codigo_tributacao_municipio", length = 20)
    private String codigoTributacaoMunicipio;

    @Column(name = "aliquota_iss_padrao", precision = 18, scale = 6)
    private BigDecimal aliquotaIssPadrao;
}
