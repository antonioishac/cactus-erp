package br.com.cactusdigital.erp.fiscal.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code fiscal-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum FiscalErrorCode implements ErrorCode {

    VENDA_JA_TEM_NOTA_FISCAL("Esta venda já tem uma nota fiscal eletrônica gerada", HttpStatus.CONFLICT),
    VENDA_SITUACAO_INVALIDA_PARA_NOTA_FISCAL("A venda precisa estar CONFIRMADA para gerar nota fiscal", HttpStatus.UNPROCESSABLE_ENTITY),
    NOTA_FISCAL_SITUACAO_INVALIDA("A nota fiscal não está numa situação válida para essa ação", HttpStatus.CONFLICT),
    EMPRESA_SEM_CONFIGURACAO_FISCAL("A empresa não tem configuração fiscal cadastrada (certificado/série/ambiente)", HttpStatus.UNPROCESSABLE_ENTITY),
    FALHA_INTEGRACAO_VENDAS("Falha ao consultar a venda no vendas-service", HttpStatus.BAD_GATEWAY),
    FALHA_INTEGRACAO_CADASTRO("Falha ao consultar cadastro base (cliente/produto/empresa/transportadora)", HttpStatus.BAD_GATEWAY),
    FALHA_INTEGRACAO_OS("Falha ao consultar a OS no os-service", HttpStatus.BAD_GATEWAY),
    FALHA_COMUNICACAO_ACBR("Falha ao comunicar com o ACBr Monitor", HttpStatus.BAD_GATEWAY),
    SEFAZ_REJEITOU_DOCUMENTO("A SEFAZ rejeitou o documento fiscal", HttpStatus.UNPROCESSABLE_ENTITY),
    OS_JA_TEM_NOTA_SERVICO("Esta OS já tem uma nota de serviço gerada", HttpStatus.CONFLICT),
    OS_ORCAMENTO_NAO_APROVADO("A OS precisa ter o orçamento aprovado para gerar nota de serviço", HttpStatus.UNPROCESSABLE_ENTITY),
    OS_SEM_ITEM_SERVICO("A OS não tem nenhum item do tipo SERVICO para faturar", HttpStatus.UNPROCESSABLE_ENTITY),
    VENDA_JA_TEM_CTE("Esta venda já tem um CT-e gerado", HttpStatus.CONFLICT),
    VENDA_SITUACAO_INVALIDA_PARA_CTE("A venda precisa estar CONFIRMADA para gerar CT-e", HttpStatus.UNPROCESSABLE_ENTITY),
    VENDA_SEM_FRETE("A venda não tem frete definido (POST /vendas/{id}/frete) — necessário para gerar CT-e", HttpStatus.UNPROCESSABLE_ENTITY),
    FRETE_SEM_TRANSPORTADORA("O frete da venda não tem transportadora definida", HttpStatus.UNPROCESSABLE_ENTITY),
    TOMADOR_NAO_SUPORTADO("Só REMETENTE (empresa emitente) ou DESTINATARIO (cliente da venda) são suportados como tomador do CT-e nesta fase", HttpStatus.UNPROCESSABLE_ENTITY),
    FALHA_INTEGRACAO_LOJA_VIRTUAL("Falha ao consultar o loja-virtual-service", HttpStatus.BAD_GATEWAY),
    PEDIDO_JA_TEM_NOTA_CONSUMIDOR("Este pedido já tem uma NFC-e gerada", HttpStatus.CONFLICT),
    PEDIDO_SITUACAO_INVALIDA_PARA_NOTA_CONSUMIDOR("O pedido precisa estar PAGO para gerar NFC-e", HttpStatus.UNPROCESSABLE_ENTITY),
    LOJA_SEM_EMPRESA_VINCULADA("Esta loja não tem empresaId vinculado — configure antes de emitir NFC-e", HttpStatus.UNPROCESSABLE_ENTITY),
    ITEM_SEM_TRIBUTACAO_CONFIGURADA("Um ou mais itens do pedido não têm NCM/CSOSN configurado na variante — não é possível gerar NFC-e", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    FiscalErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "FISCAL_" + name();
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
