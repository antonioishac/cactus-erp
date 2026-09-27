package br.com.cactusdigital.erp.financeiro.entity;

import br.com.cactusdigital.erp.common.domain.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Registro (cabeçalho) de uma remessa CNAB 240 de cobrança gerada — 1 lote por arquivo
 * (simplificação deliberada: um arquivo com múltiplos lotes/serviços diferentes não é
 * necessário pro cenário "registrar título em carteira de cobrança"). {@code conteudoArquivo}
 * guarda o texto CNAB240 completo já gerado (não é um arquivo em disco/GED — TEXT no banco
 * é suficiente pro volume esperado de remessa de cobrança, diferente de documento genérico
 * do GED que pode ser binário grande). Ver {@code Cnab240RemessaBuilder}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "remessa_bancaria")
public class RemessaBancaria extends BaseEntity {

    @Column(name = "conta_bancaria_id", nullable = false)
    private Long contaBancariaId;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "data_geracao", nullable = false)
    private LocalDate dataGeracao;

    @Column(name = "numero_sequencial_arquivo", nullable = false)
    private long numeroSequencialArquivo;

    @Column(name = "quantidade_titulos", nullable = false)
    private int quantidadeTitulos;

    @Column(name = "valor_total", nullable = false, precision = 18, scale = 6)
    private BigDecimal valorTotal;

    /** {@code columnDefinition} explícito (não {@code @Lob}) — {@code @Lob} num
     * {@code String} faz o Hibernate esperar {@code TINYTEXT} por padrão neste dialect
     * MySQL, incompatível com o {@code MEDIUMTEXT} que a migration cria (achado real na
     * primeira subida, corrigido antes de qualquer dado real existir). */
    @Column(name = "conteudo_arquivo", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String conteudoArquivo;

    @Builder.Default
    @OneToMany(mappedBy = "remessa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RemessaBancariaItem> itens = new ArrayList<>();
}
