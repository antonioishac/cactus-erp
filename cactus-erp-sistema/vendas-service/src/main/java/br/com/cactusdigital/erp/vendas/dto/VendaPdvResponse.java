package br.com.cactusdigital.erp.vendas.dto;

import br.com.cactusdigital.erp.vendas.entity.OrigemVendaPdv;
import br.com.cactusdigital.erp.vendas.entity.SituacaoVendaPdv;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record VendaPdvResponse(
        Long id,
        Long empresaId,
        Long registroSincronizacaoId,
        String idOrigem,
        Long pdvCaixaId,
        String pdvCaixaNome,
        Long pdvOperadorId,
        Long clienteId,
        Long colaboradorId,
        String nomeCliente,
        String cpfCnpjCliente,
        Instant dataHoraVenda,
        OrigemVendaPdv origemVenda,
        SituacaoVendaPdv situacao,
        String tipoOperacaoFiscal,
        String statusFiscal,
        String chaveAcessoFiscal,
        String protocoloAutorizacao,
        Integer coo,
        Integer ccf,
        BigDecimal valorTotalProdutos,
        BigDecimal valorDesconto,
        BigDecimal valorAcrescimo,
        BigDecimal valorFinal,
        BigDecimal valorRecebido,
        BigDecimal valorTroco,
        BigDecimal valorBaseIcms,
        BigDecimal valorIcms,
        BigDecimal valorPis,
        BigDecimal valorCofins,
        BigDecimal valorIssqn,
        BigDecimal valorDinheiroLiquido,
        Instant dataHoraCancelamento,
        Long pdvOperadorCancelamentoId,
        Long pdvGerenteCancelamentoId,
        String justificativaCancelamento,
        String protocoloCancelamento,
        List<VendaPdvItemResponse> itens,
        List<VendaPdvPagamentoResponse> pagamentos,
        List<VendaPdvDevolucaoResponse> devolucoes) {
}
