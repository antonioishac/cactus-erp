package br.com.cactusdigital.erp.common.jpa;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Resolve o N+1 clássico de "página de agregados cujo DTO expõe uma coleção filha
 * lazy" (ex: {@code PedidoCompraResponse.itens}, {@code Produto.imagens}) — sem isso,
 * cada linha da página dispara uma query extra pra resolver a coleção na hora do
 * mapeamento pro DTO.
 * <p>
 * <b>Por que não dá pra resolver com {@code JOIN FETCH} direto na query paginada</b>:
 * SQL aplica {@code LIMIT}/{@code OFFSET} sobre as linhas já multiplicadas pelo join
 * com a coleção — a paginação sairia errada (menos agregados por página do que o
 * solicitado, ou repetidos entre páginas). A solução padrão (e a que este helper
 * aplica) é em duas consultas: primeiro pagina os agregados normalmente (sem fetch da
 * coleção), depois busca só os da página atual com {@code JOIN FETCH} via
 * {@code WHERE id IN (:ids)} — um {@code IN} pequeno e fixo não sofre o mesmo problema.
 * <p>
 * Uso típico num service: {@code repository.findAll(pageable)} pra paginar, depois uma
 * query própria no repositório (@Query com {@code LEFT JOIN FETCH ... WHERE id IN :ids})
 * pra rebuscar só a página com a coleção carregada, e este helper faz o remapeamento
 * preservando ordem/contagem original da página.
 */
public final class PaginatedFetchSupport {

    private PaginatedFetchSupport() {
    }

    public static <T, ID, R> Page<R> comColecoesCarregadas(
            Page<T> pagina,
            Function<T, ID> idExtractor,
            Function<List<ID>, List<T>> buscarComFetch,
            Function<T, R> mapper) {
        if (pagina.isEmpty()) {
            return new PageImpl<>(List.of(), pagina.getPageable(), pagina.getTotalElements());
        }
        List<ID> ids = pagina.getContent().stream().map(idExtractor).toList();
        Map<ID, T> porId = buscarComFetch.apply(ids).stream()
                .collect(Collectors.toMap(idExtractor, Function.identity()));

        List<R> conteudo = pagina.getContent().stream()
                .map(item -> porId.getOrDefault(idExtractor.apply(item), item))
                .map(mapper)
                .toList();
        return new PageImpl<>(conteudo, pagina.getPageable(), pagina.getTotalElements());
    }
}
