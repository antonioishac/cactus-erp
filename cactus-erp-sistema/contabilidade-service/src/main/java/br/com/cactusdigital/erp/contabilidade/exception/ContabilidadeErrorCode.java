package br.com.cactusdigital.erp.contabilidade.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code contabilidade-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum ContabilidadeErrorCode implements ErrorCode {

    PLANO_CONTA_CODIGO_DUPLICADO("Já existe uma conta com este código nesta empresa", HttpStatus.CONFLICT),
    PLANO_CONTA_PAI_DE_OUTRA_EMPRESA("A conta pai selecionada pertence a outra empresa", HttpStatus.UNPROCESSABLE_ENTITY),
    PLANO_CONTA_PAI_DEVE_SER_SINTETICA("Só uma conta sintética (contaAnalitica=false) pode ser conta pai de outra conta", HttpStatus.UNPROCESSABLE_ENTITY),
    PLANO_CONTA_CICLO_HIERARQUIA("Esta alteração criaria um ciclo na hierarquia do plano de contas", HttpStatus.UNPROCESSABLE_ENTITY),
    PLANO_CONTA_COM_MOVIMENTO_OU_SUBCONTAS("Esta conta tem lançamentos ou subcontas vinculadas e não pode ser excluída", HttpStatus.CONFLICT),
    PLANO_CONTA_NAO_ANALITICA_PARA_LANCAMENTO("Só uma conta analítica pode receber lançamento — esta conta é sintética/agrupadora", HttpStatus.UNPROCESSABLE_ENTITY),
    PLANO_CONTA_DE_OUTRA_EMPRESA("Esta conta pertence a outra empresa, diferente da empresa do lançamento", HttpStatus.UNPROCESSABLE_ENTITY),
    CENTRO_CUSTO_CODIGO_DUPLICADO("Já existe um centro de custo com este código nesta empresa", HttpStatus.CONFLICT),
    CENTRO_CUSTO_DE_OUTRA_EMPRESA("Este centro de custo pertence a outra empresa, diferente da empresa do lançamento", HttpStatus.UNPROCESSABLE_ENTITY),
    LANCAMENTO_PARTIDAS_INSUFICIENTES("Um lançamento contábil precisa de pelo menos 2 partidas (débito e crédito)", HttpStatus.UNPROCESSABLE_ENTITY),
    LANCAMENTO_PARTIDA_DOBRADA_DIVERGENTE("A soma dos débitos precisa ser exatamente igual à soma dos créditos", HttpStatus.UNPROCESSABLE_ENTITY),
    LANCAMENTO_JA_ESTORNADO("Este lançamento já foi estornado anteriormente", HttpStatus.CONFLICT),
    LANCAMENTO_E_UM_ESTORNO("Este lançamento já é o estorno de outro — não é possível estornar um estorno", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    ContabilidadeErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "CONTABIL_" + name();
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
