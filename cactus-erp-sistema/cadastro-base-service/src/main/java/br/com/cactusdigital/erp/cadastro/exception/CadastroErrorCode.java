package br.com.cactusdigital.erp.cadastro.exception;

import br.com.cactusdigital.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * Catálogo de códigos de erro de negócio do {@code cadastro-base-service}. Toda
 * {@link br.com.cactusdigital.erp.common.exception.BusinessException}/
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException} lançada neste
 * módulo usa um código daqui — nunca uma string solta — pra o cliente da API poder
 * discriminar o erro de forma estável (o texto da mensagem pode mudar por chamada, ex:
 * citando o nome da entidade, mas o código não).
 * <p>
 * Algumas regras (ex: "tipo de pessoa não pode mudar", "CPF obrigatório pra pessoa
 * física") se repetem entre {@code Cliente}/{@code Fornecedor}/{@code Colaborador} — é
 * a mesma regra de negócio, então usam o mesmo código, só com a mensagem
 * ({@link br.com.cactusdigital.erp.common.exception.BusinessException#BusinessException(ErrorCode, String)})
 * customizada por chamada pra citar a entidade certa.
 * <p>
 * {@code httpStatusPadrao()} é por constante, não um valor único pra todo o enum: os
 * códigos "já existe X" ({@code REGIME_TRIBUTARIO_ANO_DUPLICADO},
 * {@code CONFIGURACAO_TRIBUTARIA_DUPLICADA}) são conflito de estado (409, lançados como
 * {@code ConflictException}), não violação de regra de negócio sobre o dado enviado
 * (422, {@code BusinessException}) — ver critério completo na documentação de
 * {@link br.com.cactusdigital.erp.common.exception.ConflictException}.
 * <p>
 * Códigos de {@code Usuario}/RBAC (senha obrigatória, papel duplicado, sem acesso à
 * empresa) não moram mais aqui — ver {@code SegurancaErrorCode} no
 * {@code seguranca-service}, dono desse cluster desde a extração do Authorization
 * Server/RBAC pra um serviço próprio.
 */
public enum CadastroErrorCode implements ErrorCode {

    PESSOA_TIPO_IMUTAVEL("Não é possível alterar o tipo de pessoa (física/jurídica) de um cadastro já existente", HttpStatus.UNPROCESSABLE_ENTITY),
    PESSOA_CPF_OBRIGATORIO("CPF é obrigatório para pessoa física", HttpStatus.UNPROCESSABLE_ENTITY),
    PESSOA_CNPJ_OBRIGATORIO("CNPJ é obrigatório para pessoa jurídica", HttpStatus.UNPROCESSABLE_ENTITY),
    REGIME_TRIBUTARIO_ANO_DUPLICADO("Já existe regime tributário registrado para este ano-calendário", HttpStatus.CONFLICT),
    CONFIGURACAO_TRIBUTARIA_DUPLICADA("Já existe configuração tributária para esse grupo tributário e operação fiscal", HttpStatus.CONFLICT),
    TRIBUTACAO_UF_NAO_CONFIGURADA("Não há regra de ICMS configurada para essa UF nessa combinação de grupo tributário e operação fiscal", HttpStatus.UNPROCESSABLE_ENTITY),
    CLASSIFICACAO_IBS_CBS_NAO_SUPORTADA("Classificação tributária do IBS/CBS ainda não suportada pelo ERP", HttpStatus.UNPROCESSABLE_ENTITY),
    CLASSIFICACAO_IBS_CBS_FORA_DE_VIGENCIA("Classificação tributária do IBS/CBS fora de vigência", HttpStatus.UNPROCESSABLE_ENTITY),
    ALIQUOTA_IBS_CBS_NAO_DEFINIDA("Não há alíquota de IBS/CBS definida em lei para o ano de emissão", HttpStatus.UNPROCESSABLE_ENTITY),
    CONFIGURACAO_FISCAL_INCOMPLETA("A empresa não tem série de NF-e configurada — complete a configuração fiscal antes de emitir", HttpStatus.UNPROCESSABLE_ENTITY);

    private final String mensagemPadrao;
    private final HttpStatus httpStatusPadrao;

    CadastroErrorCode(String mensagemPadrao, HttpStatus httpStatusPadrao) {
        this.mensagemPadrao = mensagemPadrao;
        this.httpStatusPadrao = httpStatusPadrao;
    }

    @Override
    public String codigo() {
        return "CADASTRO_" + name();
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
