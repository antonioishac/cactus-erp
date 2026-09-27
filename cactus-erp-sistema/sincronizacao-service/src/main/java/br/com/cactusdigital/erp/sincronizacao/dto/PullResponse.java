package br.com.cactusdigital.erp.sincronizacao.dto;

import java.time.Instant;
import java.util.List;

/**
 * Envelope de resposta de todo pull ERP→PDV — {@code proximoCursor} é o que o PDV grava
 * localmente e reenvia no próximo {@code desde}, carimbado pelo servidor
 * ({@code dataAtualizacao} das entidades), nunca pelo relógio do PDV (ver seção 5.1 do
 * FASE6_SINCRONIZACAO_PDV.md — resolve o cenário de clock skew, seção 8.4). O PDV só
 * avança o cursor local depois de persistir a página inteira com sucesso — se cair no
 * meio, repete a mesma página (idempotente por natureza, é leitura).
 */
public record PullResponse<T>(
        List<T> conteudo,
        Instant proximoCursor,
        int paginaAtual,
        int tamanhoPagina,
        long totalRegistros,
        int totalPaginas,
        boolean ultimaPagina) {

    public static <T> PullResponse<T> of(UpstreamPage<T> pagina, Instant proximoCursor) {
        return new PullResponse<>(
                pagina.conteudo(), proximoCursor, pagina.paginaAtual(), pagina.tamanhoPagina(),
                pagina.totalRegistros(), pagina.totalPaginas(), pagina.ultimaPagina());
    }
}
