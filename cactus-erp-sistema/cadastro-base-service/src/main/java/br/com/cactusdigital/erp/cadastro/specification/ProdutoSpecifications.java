package br.com.cactusdigital.erp.cadastro.specification;

import br.com.cactusdigital.erp.cadastro.entity.Produto;
import br.com.cactusdigital.erp.common.domain.Situacao;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class ProdutoSpecifications {

    private ProdutoSpecifications() {
    }

    public static Specification<Produto> comFiltros(
            String nome, String codigoInterno, String gtin, Long produtoSubgrupoId, Long produtoMarcaId, Situacao situacao) {
        return comFiltros(nome, codigoInterno, gtin, produtoSubgrupoId, produtoMarcaId, situacao, null);
    }

    /**
     * {@code desde} é o cursor de sincronização incremental (ver {@code sincronizacao-service},
     * contrato de pull "cursor de alteração, não full dump") — filtra só produtos alterados
     * depois desse instante, carimbado pelo servidor via {@code BaseEntity#dataAtualizacao}.
     */
    public static Specification<Produto> comFiltros(
            String nome, String codigoInterno, String gtin, Long produtoSubgrupoId, Long produtoMarcaId, Situacao situacao, Instant desde) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (StringUtils.hasText(nome)) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(codigoInterno)) {
                predicate = cb.and(predicate, cb.equal(root.get("codigoInterno"), codigoInterno));
            }
            if (StringUtils.hasText(gtin)) {
                predicate = cb.and(predicate, cb.equal(root.get("gtin"), gtin));
            }
            if (produtoSubgrupoId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("produtoSubgrupo").get("id"), produtoSubgrupoId));
            }
            if (produtoMarcaId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("produtoMarca").get("id"), produtoMarcaId));
            }
            if (situacao != null) {
                predicate = cb.and(predicate, cb.equal(root.get("situacao"), situacao));
            }
            if (desde != null) {
                predicate = cb.and(predicate, cb.greaterThan(root.get("dataAtualizacao"), desde));
            }

            return predicate;
        };
    }
}
