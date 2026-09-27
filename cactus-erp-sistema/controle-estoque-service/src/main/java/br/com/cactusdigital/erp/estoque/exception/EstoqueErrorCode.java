package br.com.cactusdigital.erp.estoque.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code controle-estoque-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum EstoqueErrorCode implements ErrorCode {

    DEPOSITO_CODIGO_DUPLICADO("Já existe um depósito com esse código nessa empresa", HttpStatus.CONFLICT),
    SALDO_INSUFICIENTE("Saldo insuficiente pra essa saída/ajuste — a operação deixaria o estoque negativo", HttpStatus.UNPROCESSABLE_ENTITY),
    AJUSTE_DIRECAO_OBRIGATORIA("Ajuste de estoque precisa informar a direção (acréscimo ou decréscimo)", HttpStatus.UNPROCESSABLE_ENTITY),
    APROVADOR_OBRIGATORIO("Ajuste/perda acima do limite configurado exige um aprovador", HttpStatus.UNPROCESSABLE_ENTITY),
    TRANSFERENCIA_DEPOSITO_IGUAL("Depósito de origem e destino não podem ser o mesmo", HttpStatus.UNPROCESSABLE_ENTITY),
    TRANSFERENCIA_SITUACAO_INVALIDA("A transferência não está numa situação válida para essa ação", HttpStatus.CONFLICT),
    INVENTARIO_SITUACAO_INVALIDA("O inventário não está numa situação válida para essa ação", HttpStatus.CONFLICT),
    INVENTARIO_ITEM_NAO_CONTADO("Não é possível finalizar o inventário com itens ainda não contados", HttpStatus.UNPROCESSABLE_ENTITY),
    NUMERO_SERIE_JA_CADASTRADO("Esse número de série já está cadastrado para esse produto", HttpStatus.CONFLICT),
    LOTE_JA_CADASTRADO("Já existe um lote com esse código para esse produto", HttpStatus.CONFLICT),
    KIT_COMPONENTE_CIRCULAR("Um produto não pode ser componente de si mesmo", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    EstoqueErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "ESTOQUE_" + name();
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
