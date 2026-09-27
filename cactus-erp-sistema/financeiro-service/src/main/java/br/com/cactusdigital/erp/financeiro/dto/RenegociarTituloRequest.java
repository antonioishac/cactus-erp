package br.com.cactusdigital.erp.financeiro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * A soma de {@code novasParcelas[].valor} precisa bater exatamente com o
 * {@code valorSaldo} do título original (validado no service — {@code NaturezaFinanceira}/
 * {@code tipoDocumento}/{@code fornecedorId ou clienteId} são herdados do título original,
 * não re-informados aqui).
 */
public record RenegociarTituloRequest(
        @NotBlank @Size(max = 250) String motivo,
        @NotEmpty @Valid List<NovaParcelaRenegociacaoRequest> novasParcelas) {
}
