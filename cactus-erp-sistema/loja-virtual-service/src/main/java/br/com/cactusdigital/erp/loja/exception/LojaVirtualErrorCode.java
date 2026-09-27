package br.com.cactusdigital.erp.loja.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code loja-virtual-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum LojaVirtualErrorCode implements ErrorCode {

    SUBDOMINIO_JA_UTILIZADO("Este subdomínio já está em uso por outra loja", HttpStatus.CONFLICT),
    SKU_JA_UTILIZADO("Este SKU já está em uso por outra variante desta loja", HttpStatus.CONFLICT),
    ATRIBUTO_CATEGORIA_DIVERGENTE("O atributo informado não pertence à categoria do produto", HttpStatus.UNPROCESSABLE_ENTITY),
    ATRIBUTO_NAO_GERA_VARIACAO("O atributo informado é só informativo (geraVariacao=false) — não pode compor uma variante", HttpStatus.UNPROCESSABLE_ENTITY),
    ATRIBUTO_OBRIGATORIO_AUSENTE("Um atributo obrigatório da categoria não foi informado na variante", HttpStatus.UNPROCESSABLE_ENTITY),
    OPCAO_INVALIDA_PARA_ATRIBUTO_LISTA("O valor informado não é uma opção válida para este atributo do tipo LISTA", HttpStatus.UNPROCESSABLE_ENTITY),
    CARRINHO_JA_FINALIZADO("Este carrinho já foi finalizado — não é possível alterar itens", HttpStatus.CONFLICT),
    CARRINHO_VAZIO("O carrinho não tem itens — não é possível finalizar a compra", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTOQUE_INSUFICIENTE("Quantidade solicitada maior que o estoque disponível desta variante", HttpStatus.UNPROCESSABLE_ENTITY),
    ESTOQUE_MENOR_QUE_RESERVADO("A nova quantidade de estoque é menor que a quantidade já reservada por carrinhos/pedidos em aberto", HttpStatus.UNPROCESSABLE_ENTITY),
    PEDIDO_SITUACAO_INVALIDA("O pedido não está numa situação válida para essa ação", HttpStatus.CONFLICT);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    LojaVirtualErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "LOJA_" + name();
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
