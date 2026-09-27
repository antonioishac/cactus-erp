package br.com.cactusdigital.erp.compras.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code gestao-compras-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum ComprasErrorCode implements ErrorCode {

    REQUISICAO_SITUACAO_INVALIDA("A requisição não está numa situação válida para essa ação", HttpStatus.CONFLICT),
    COTACAO_SITUACAO_INVALIDA("A cotação não está numa situação válida para essa ação", HttpStatus.CONFLICT),
    COTACAO_SEM_ITENS("O fornecedor não tem itens cotados — não é possível gerar pedido", HttpStatus.UNPROCESSABLE_ENTITY),
    PEDIDO_SITUACAO_INVALIDA("O pedido não está numa situação válida para essa ação", HttpStatus.CONFLICT),
    PEDIDO_SEM_ITENS("O pedido não tem itens — não é possível enviar", HttpStatus.UNPROCESSABLE_ENTITY),
    PEDIDO_ITEM_APENAS_EM_RASCUNHO("Só é possível adicionar/remover item de pedido em RASCUNHO", HttpStatus.CONFLICT),
    RECEBIMENTO_QUANTIDADE_EXCEDE_PEDIDO("A quantidade recebida excede a quantidade pendente do item", HttpStatus.UNPROCESSABLE_ENTITY),
    FALHA_INTEGRACAO_ESTOQUE("Falha ao registrar a entrada de estoque no controle-estoque-service", HttpStatus.BAD_GATEWAY),
    FALHA_INTEGRACAO_FINANCEIRO("Falha ao gerar contas a pagar no financeiro-service", HttpStatus.BAD_GATEWAY);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    ComprasErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "COMPRAS_" + name();
    }

    @Override
    public String mensagemPadrao() {
        return mensagemPadrao;
    }

    @Override
    public HttpStatus httpStatusPadrao() {
        return httpStatusPadrao;
    }
}
