package br.com.cactusdigital.erp.financeiro.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code financeiro-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta.
 */
public enum FinanceiroErrorCode implements ErrorCode {

    TITULO_JA_QUITADO("Este título já está quitado", HttpStatus.CONFLICT),
    TITULO_CANCELADO("Este título está cancelado — não é possível registrar baixa", HttpStatus.CONFLICT),
    VALOR_BAIXA_MAIOR_QUE_SALDO("O valor da baixa é maior que o saldo em aberto do título", HttpStatus.UNPROCESSABLE_ENTITY),
    TITULO_JA_TEM_BAIXA_PARA_CANCELAR("Este título tem baixa ativa registrada — estorne a(s) baixa(s) antes de cancelar", HttpStatus.CONFLICT),
    VENDA_JA_GEROU_CONTAS_RECEBER("Esta venda já gerou contas a receber", HttpStatus.CONFLICT),
    COMPRA_JA_GEROU_CONTAS_PAGAR("Este pedido de compra já gerou contas a pagar", HttpStatus.CONFLICT),
    BAIXA_JA_ESTORNADA("Esta baixa já foi estornada", HttpStatus.CONFLICT),

    // Caixa e Bancos / Cheque / Renegociação (2026-09-06)
    TITULO_RENEGOCIADO("Este título foi renegociado — o saldo foi substituído por título(s) novo(s)", HttpStatus.CONFLICT),
    TITULO_NAO_PODE_SER_RENEGOCIADO("Só é possível renegociar título ABERTO ou PARCIAL, com saldo em aberto", HttpStatus.CONFLICT),
    SOMA_NOVAS_PARCELAS_DIVERGE_DO_SALDO("A soma das novas parcelas precisa ser igual ao saldo em aberto do título original", HttpStatus.UNPROCESSABLE_ENTITY),
    DESCONTO_FORA_DO_PRAZO("Data da baixa posterior ao prazo de desconto por antecipação deste título", HttpStatus.UNPROCESSABLE_ENTITY),
    VALOR_BAIXA_MAIS_DESCONTO_MAIOR_QUE_SALDO("Valor da baixa somado ao desconto é maior que o saldo em aberto do título", HttpStatus.UNPROCESSABLE_ENTITY),
    CONTA_BANCARIA_INATIVA("Conta bancária inativa — não pode receber movimentação", HttpStatus.CONFLICT),
    BANCO_OBRIGATORIO_PARA_TIPO_DE_CONTA("Conta corrente/poupança/aplicação exige um banco — só CAIXA_INTERNO pode ficar sem banco", HttpStatus.UNPROCESSABLE_ENTITY),
    CHEQUE_SITUACAO_INVALIDA_PARA_OPERACAO("Situação atual do cheque não permite esta operação", HttpStatus.CONFLICT),
    PAGAMENTO_FIADO_MAIOR_QUE_DIVIDA("O pagamento de fiado vindo do PDV é maior que a dívida de fiado em aberto do cliente no ERP", HttpStatus.UNPROCESSABLE_ENTITY),
    VALOR_MOVIMENTO_DEVE_SER_POSITIVO("Valor do movimento de caixa/banco precisa ser maior que zero", HttpStatus.UNPROCESSABLE_ENTITY),
    TIPO_MOVIMENTO_INVALIDO_PARA_LANCAMENTO_MANUAL("Lançamento manual só aceita ENTRADA ou SAIDA — transferência tem endpoint próprio", HttpStatus.UNPROCESSABLE_ENTITY),

    // Tesouraria (2026-09-07)
    CONTA_ORIGEM_IGUAL_DESTINO("Conta de origem e destino da transferência não podem ser a mesma", HttpStatus.UNPROCESSABLE_ENTITY),
    APLICACAO_JA_RESGATADA("Esta aplicação financeira já foi resgatada", HttpStatus.CONFLICT),

    // Conciliação Bancária (2026-09-07)
    ARQUIVO_OFX_INVALIDO("Arquivo não é um OFX válido ou não contém nenhuma transação reconhecível", HttpStatus.UNPROCESSABLE_ENTITY),
    ITEM_EXTRATO_JA_CONCILIADO("Este item do extrato já está conciliado", HttpStatus.CONFLICT),
    ITEM_EXTRATO_NAO_CONCILIADO("Este item do extrato não está conciliado", HttpStatus.CONFLICT),
    MOVIMENTO_JA_CONCILIADO("Este movimento de caixa/banco já está conciliado com outro item do extrato", HttpStatus.CONFLICT),

    // EDI Bancário (2026-09-07)
    ARQUIVO_CNAB240_INVALIDO("Arquivo não é um retorno CNAB240 válido ou não contém nenhum par de segmentos T+U reconhecível", HttpStatus.UNPROCESSABLE_ENTITY),
    TITULO_NAO_APTO_PARA_REMESSA("Só título ABERTO ou PARCIAL pode ser incluído numa remessa de cobrança", HttpStatus.CONFLICT),
    CONFIGURACAO_COBRANCA_INCOMPATIVEL_COM_LAYOUT("Configuração de cobrança da conta bancária (carteira/convênio/código do cedente) não cabe nas posições do layout CNAB240 usado — confira o tamanho de cada código configurado", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    FinanceiroErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "FINANCEIRO_" + name();
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
