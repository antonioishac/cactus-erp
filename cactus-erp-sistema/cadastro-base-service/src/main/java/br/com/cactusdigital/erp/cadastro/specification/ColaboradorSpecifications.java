package br.com.cactusdigital.erp.cadastro.specification;

import br.com.cactusdigital.erp.cadastro.entity.Colaborador;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ColaboradorSpecifications {

    private ColaboradorSpecifications() {
    }

    public static Specification<Colaborador> comFiltros(String nome, String documento, Long cargoId, Long setorId, Situacao situacao) {
        return (root, query, cb) -> {
            var pessoa = root.join("pessoa", JoinType.INNER);

            Predicate predicate = cb.conjunction();

            if (StringUtils.hasText(nome)) {
                predicate = cb.and(predicate, cb.like(cb.lower(pessoa.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (cargoId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cargo").get("id"), cargoId));
            }
            if (setorId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("setor").get("id"), setorId));
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

            // Sem query.distinct(true) de propósito — mesmo achado de
            // ClienteSpecifications (2026-09-01): pessoa/pessoaFisica/pessoaJuridica
            // são @OneToOne/@ManyToOne e cargo/setor são @ManyToOne — nenhum
            // multiplica linha, então DISTINCT nunca foi necessário — e quebrava
            // `sort=pessoa.nome,...` no MySQL (DISTINCT + ORDER BY em coluna fora
            // da projeção, ErrorCode 3065).

            return predicate;
        };
    }
}
