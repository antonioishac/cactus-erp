package br.com.cactusdigital.erp.common.rest;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Envelope de paginação padrão de toda listagem do Cactus ERP — desacopla o contrato
 * público da API do tipo interno {@link Page} do Spring Data.
 */
public record PageResponse<T>(
        List<T> conteudo,
        int paginaAtual,
        int tamanhoPagina,
        long totalRegistros,
        int totalPaginas,
        boolean primeiraPagina,
        boolean ultimaPagina) {

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    public static <T, R> PageResponse<R> of(Page<T> page, List<R> conteudoMapeado) {
        return new PageResponse<>(
                conteudoMapeado,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
