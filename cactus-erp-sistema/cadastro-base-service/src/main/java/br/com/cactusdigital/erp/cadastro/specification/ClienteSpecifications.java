package br.com.cactusdigital.erp.cadastro.specification;

import br.com.cactusdigital.erp.cadastro.entity.Cliente;
import br.com.cactusdigital.erp.cadastro.entity.Pessoa;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Filtros dinâmicos de {@link Cliente} via JPA {@link Specification} — evita a
 * explosão de métodos {@code findByXAndY} no repository e continua usando os índices
 * criados nas migrations (nome/documento/situação).
 */
public final class ClienteSpecifications {

    private ClienteSpecifications() {
    }

    public static Specification<Cliente> comFiltros(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao) {
        return comFiltros(nome, documento, tipoPessoa, situacao, null);
    }

    /** {@code desde} = cursor de sincronização incremental, ver {@code ProdutoSpecifications}. */
    public static Specification<Cliente> comFiltros(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, Instant desde) {
        return (root, query, cb) -> {
            var pessoa = root.join("pessoa", JoinType.INNER);

            Predicate predicate = cb.conjunction();

            if (StringUtils.hasText(nome)) {
                predicate = cb.and(predicate, cb.like(cb.lower(pessoa.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (tipoPessoa != null) {
                predicate = cb.and(predicate, cb.equal(pessoa.get("tipoPessoa"), tipoPessoa));
            }
            if (situacao != null) {
                predicate = cb.and(predicate, cb.equal(root.get("situacao"), situacao));
            }
            if (StringUtils.hasText(documento)) {
                var pessoaFisica = pessoa.join("pessoaFisica", JoinType.LEFT);
                var pessoaJuridica = pessoa.join("pessoaJuridica", JoinType.LEFT);
                predicate = cb.and(predicate, cb.or(
                        cb.equal(pessoaFisica.get("cpf"), documento),
                        cb.equal(pessoaJuridica.get("cnpj"), documento)));
            }
            if (desde != null) {
                predicate = cb.and(predicate, cb.greaterThan(root.get("dataAtualizacao"), desde));
            }

            // Sem query.distinct(true) de propósito — achado ao vivo (2026-09-01,
            // cactus-erp-web): todo join usado aqui (pessoa, pessoaFisica,
            // pessoaJuridica) é @OneToOne/@ManyToOne, nenhum pode multiplicar linhas
            // de Cliente, então DISTINCT nunca foi necessário nesta Specification (o
            // comentário antigo já reconhecia isso — "a query só filtra por Pessoa/
            // especialização, não navega essas listas" — mas mesmo assim ligava o
            // distinct). DISTINCT incondicional quebrava `sort=pessoa.nome,...`
            // (usado pela listagem de clientes no frontend) com erro real do MySQL:
            // "Expression #1 of ORDER BY clause is not in SELECT list ... this is
            // incompatible with DISTINCT" (ErrorCode 3065) — MySQL proíbe
            // SELECT DISTINCT + ORDER BY numa coluna fora da projeção.

            return predicate;
        };
    }
}
