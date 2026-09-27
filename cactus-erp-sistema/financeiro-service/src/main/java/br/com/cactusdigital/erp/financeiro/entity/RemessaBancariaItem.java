package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 1 título de {@link ContaReceber} registrado numa {@link RemessaBancaria} — dados do
 * sacado (pagador) são SNAPSHOT no momento da geração (não referência solta ao
 * cadastro-base-service): {@code financeiro-service} não tem cliente OAuth2 de saída pra
 * nenhum outro serviço (é só chamado, nunca chama), então pedir nome/documento/endereço
 * do sacado ao operador na hora de gerar a remessa evita criar a primeira dependência
 * síncrona de saída deste serviço só pra isso — mesma decisão de {@code ContaBancaria.cedenteNome}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "remessa_bancaria_item")
public class RemessaBancariaItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remessa_id", nullable = false)
    private RemessaBancaria remessa;

    @Column(name = "conta_receber_id", nullable = false)
    private Long contaReceberId;

    @Column(name = "nosso_numero", nullable = false, length = 20)
    private String nossoNumero;

    @Column(name = "numero_documento", length = 15)
    private String numeroDocumento;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "valor_titulo", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTitulo;

    @Column(name = "sacado_tipo_inscricao", length = 4)
    private String sacadoTipoInscricao;

    @Column(name = "sacado_numero_inscricao", length = 20)
    private String sacadoNumeroInscricao;

    @Column(name = "sacado_nome", length = 100)
    private String sacadoNome;

    @Column(name = "sacado_logradouro", length = 100)
    private String sacadoLogradouro;

    @Column(name = "sacado_bairro", length = 50)
    private String sacadoBairro;

    @Column(name = "sacado_cep", length = 10)
    private String sacadoCep;

    @Column(name = "sacado_cidade", length = 50)
    private String sacadoCidade;

    @Column(name = "sacado_uf", length = 2)
    private String sacadoUf;
}
