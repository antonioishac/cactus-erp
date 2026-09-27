package br.com.cactusdigital.erp.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Contrato de catálogo de erro de negócio. Cada módulo do Cactus ERP declara o próprio
 * enum implementando esta interface (ex: {@code CadastroErrorCode} no
 * {@code cadastro-base-service}) — nunca um código solto em string espalhado pelo
 * código, pra não repetir/errar o mesmo código em dois lugares e pra o catálogo inteiro
 * de um módulo ficar visível num arquivo só.
 * <p>
 * O {@code codigo()} vira o {@code type}/propriedade {@code codigo} do
 * {@link java.net.URI ProblemDetail} devolvido pela API (RFC 7807) — é o identificador
 * estável que times de suporte/dev usam pra procurar o erro na documentação, sem
 * depender do texto da mensagem (que pode mudar).
 */
public interface ErrorCode {

    /** Código estável do erro, ex: {@code "FORNECEDOR_CPF_OBRIGATORIO"}. */
    String codigo();

    /** Mensagem amigável padrão, em português, segura para exibir ao usuário final. */
    String mensagemPadrao();

    /** Status HTTP que este erro deve produzir quando não sobrescrito explicitamente. */
    HttpStatus httpStatusPadrao();
}
