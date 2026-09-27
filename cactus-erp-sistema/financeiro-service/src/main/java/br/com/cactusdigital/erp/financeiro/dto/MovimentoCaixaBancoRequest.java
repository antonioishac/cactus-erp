package br.com.cactusdigital.erp.financeiro.dto;

import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Lançamento manual de ajuste (tarifa, IOF, correção de saldo) — {@code tipoMovimento}
 * só aceita {@code ENTRADA}/{@code SAIDA} aqui (transferência tem endpoint próprio, ver
 * {@code TransferenciaBancariaController}). Sempre {@code origemTipo=AJUSTE}. */
public record MovimentoCaixaBancoRequest(
        @NotNull Long contaBancariaId,
        @NotNull TipoMovimentoCaixa tipoMovimento,
        @NotNull @DecimalMin(value = "0.000001") BigDecimal valor,
        LocalDate dataMovimento,
        @NotBlank @Size(max = 250) String historico,
        Long usuarioId) {
}
