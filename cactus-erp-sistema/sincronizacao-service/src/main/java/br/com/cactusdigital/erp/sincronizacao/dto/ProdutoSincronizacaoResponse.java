package br.com.cactusdigital.erp.sincronizacao.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Payload achatado de produto pro PDV — campo a campo contra a tabela {@code PRODUTO} do
 * `MAPA DE SINCRONIZAÇÃO CAMPO A CAMPO` no CLAUDE.md. Tributação já vem resolvida
 * ({@code cstIcmsPadrao}/{@code aliquota*Padrao}, cacheados no {@code Produto} do
 * cadastro-base-service) — o PDV nunca recalcula nada, só imprime.
 * <p>
 * {@code quantidadeEstoque} NÃO vem do cadastro-base-service (produto não guarda saldo
 * flat) — é composto aqui a partir do saldo agregado do controle-estoque-service (ver
 * {@code SincronizacaoPullService#pullProdutos}), somando todos os depósitos da loja.
 * <p>
 * {@code faixasPreco}/{@code imagens} são relayed verbatim (lista de mapas, não um DTO
 * espelho campo a campo) — este serviço não interpreta esses campos, só repassa.
 */
public record ProdutoSincronizacaoResponse(
        Long id,
        String gtin,
        String codigoInterno,
        String produtoUnidadeSigla,
        String nome,
        String descricao,
        String descricaoPdv,
        String ncmCodigo,
        String cestCodigo,
        String tipoItemSped,
        String iat,
        String ippt,
        String totalizadorParcial,
        Integer codigoBalanca,
        String pafPSt,
        BigDecimal valorCusto,
        BigDecimal valorCompra,
        BigDecimal valorVenda,
        BigDecimal estoqueMinimo,
        BigDecimal estoqueMaximo,
        String cstIcmsPadrao,
        String csosnPadrao,
        BigDecimal aliquotaIcmsPadrao,
        BigDecimal aliquotaIpiPadrao,
        BigDecimal aliquotaPisPadrao,
        BigDecimal aliquotaCofinsPadrao,
        BigDecimal aliquotaIssqnPadrao,
        String situacao,
        BigDecimal quantidadeEstoque,
        List<Map<String, Object>> faixasPreco,
        List<Map<String, Object>> imagens,
        Instant dataAtualizacao) {

    /** Reconstrói com {@code quantidadeEstoque} composto — o valor desserializado
     * direto do cadastro-base-service vem sempre nulo (o campo não existe lá). */
    public ProdutoSincronizacaoResponse comEstoque(BigDecimal quantidadeEstoque) {
        return new ProdutoSincronizacaoResponse(
                id, gtin, codigoInterno, produtoUnidadeSigla, nome, descricao, descricaoPdv, ncmCodigo, cestCodigo, tipoItemSped, iat, ippt,
                totalizadorParcial, codigoBalanca, pafPSt, valorCusto, valorCompra, valorVenda, estoqueMinimo, estoqueMaximo,
                cstIcmsPadrao, csosnPadrao, aliquotaIcmsPadrao, aliquotaIpiPadrao, aliquotaPisPadrao, aliquotaCofinsPadrao,
                aliquotaIssqnPadrao, situacao, quantidadeEstoque, faixasPreco, imagens, dataAtualizacao);
    }
}
