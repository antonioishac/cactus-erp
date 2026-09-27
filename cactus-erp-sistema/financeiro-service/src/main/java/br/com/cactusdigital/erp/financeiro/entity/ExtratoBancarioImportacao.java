package br.com.cactusdigital.erp.financeiro.entity;

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
 * Um upload de arquivo OFX — cabeçalho da importação, guarda quantos itens vieram
 * ({@code quantidadeItens}) e quantos já existiam de uma importação anterior e foram
 * ignorados ({@code quantidadeDuplicados}, dedup por {@code fitId}, ver
 * {@code ExtratoBancarioItem}). O arquivo em si não é armazenado (diferente do GED) —
 * só o resultado do parsing, o arquivo original pode ser re-importado se precisar.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "extrato_bancario_importacao")
public class ExtratoBancarioImportacao extends BaseEntity {

    @Column(name = "conta_bancaria_id", nullable = false)
    private Long contaBancariaId;

    @Column(name = "nome_arquivo", length = 250)
    private String nomeArquivo;

    @Column(name = "quantidade_itens", nullable = false)
    private int quantidadeItens;

    @Column(name = "quantidade_duplicados", nullable = false)
    private int quantidadeDuplicados;

    @Column(name = "usuario_id")
    private Long usuarioId;
}
