package br.com.cactusdigital.erp.vendas.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code vendas-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum VendasErrorCode implements ErrorCode {

    ORCAMENTO_SITUACAO_INVALIDA("O orçamento não está numa situação válida para essa ação", HttpStatus.CONFLICT),
    ORCAMENTO_SEM_ITENS("O orçamento não tem itens — não é possível gerar venda", HttpStatus.UNPROCESSABLE_ENTITY),
    ORCAMENTO_ITEM_APENAS_EM_RASCUNHO("Só é possível adicionar/remover item de orçamento em RASCUNHO", HttpStatus.CONFLICT),
    VENDA_SITUACAO_INVALIDA("A venda não está numa situação válida para essa ação", HttpStatus.CONFLICT),
    VENDA_SEM_ITENS("A venda não tem itens — não é possível confirmar", HttpStatus.UNPROCESSABLE_ENTITY),
    VENDA_ITEM_SEM_DEPOSITO("Todo item precisa de um depósito definido antes de confirmar a venda", HttpStatus.UNPROCESSABLE_ENTITY),
    VENDA_ITEM_APENAS_EM_RASCUNHO("Só é possível adicionar/remover item, ou definir o frete, de venda em RASCUNHO", HttpStatus.CONFLICT),
    VENDA_CANCELAMENTO_APENAS_EM_RASCUNHO(
            "Só é possível cancelar venda em RASCUNHO — após confirmada, o estoque e a comissão já foram gerados; "
                    + "use o fluxo de devolução (fora de escopo desta fase)", HttpStatus.CONFLICT),
    VENDEDOR_JA_CADASTRADO_PARA_COLABORADOR("Já existe um vendedor cadastrado para este colaborador", HttpStatus.CONFLICT),
    FALHA_INTEGRACAO_ESTOQUE("Falha ao registrar a saída de estoque no controle-estoque-service", HttpStatus.BAD_GATEWAY),
    FALHA_INTEGRACAO_FINANCEIRO("Falha ao gerar contas a receber no financeiro-service", HttpStatus.BAD_GATEWAY),
    VENDA_PDV_PAGAMENTOS_DIVERGEM_DO_RECEBIDO("A soma dos pagamentos da venda do PDV não bate com o valor recebido", HttpStatus.UNPROCESSABLE_ENTITY),
    VENDA_PDV_RECEBIDO_MENOS_TROCO_DIVERGE_DO_FINAL("Valor recebido menos troco não bate com o valor final da venda do PDV", HttpStatus.UNPROCESSABLE_ENTITY),
    VENDA_PDV_ITEM_NAO_ENCONTRADO("O item informado na devolução não existe na venda do PDV", HttpStatus.UNPROCESSABLE_ENTITY),
    VENDA_PDV_CANCELADA_NAO_ADMITE_DEVOLUCAO("Venda do PDV cancelada não admite devolução", HttpStatus.UNPROCESSABLE_ENTITY),
    VENDA_PDV_DEVOLUCAO_ACIMA_DO_VENDIDO("A quantidade devolvida excede o que ainda resta do item na venda do PDV", HttpStatus.UNPROCESSABLE_ENTITY),
    VENDA_PDV_JA_CANCELADA("A venda do PDV já está cancelada", HttpStatus.CONFLICT);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    VendasErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "VENDAS_" + name();
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
