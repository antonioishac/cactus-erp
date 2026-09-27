package br.com.cactusdigital.erp.sincronizacao.dto;

import java.util.List;

/**
 * Mirror de {@code br.com.cactusdigital.erp.common.rest.PageResponse} — cópia
 * deliberada (mesmo motivo de {@code EstoqueMovimentoClientRequest}: cada serviço com
 * seu próprio contrato de chamada, sem dependência Java entre eles), usada só pra
 * desserializar a resposta paginada dos três serviços de origem (cadastro-base-service/
 * controle-estoque-service/seguranca-service) via {@code RestClient}.
 */
public record UpstreamPage<T>(
        List<T> conteudo,
        int paginaAtual,
        int tamanhoPagina,
        long totalRegistros,
        int totalPaginas,
        boolean primeiraPagina,
        boolean ultimaPagina) {
}
