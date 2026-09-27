package br.com.cactusdigital.erp.contabilidade.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * {@code origemTipo}/{@code origemId} não são campos do request nesta v1 — todo
 * lançamento criado por esta API nasce {@code MANUAL} (ver Javadoc de
 * {@code OrigemLancamentoContabil}); a contabilização automática por evento de negócio
 * é quem vai preencher esses campos, via um caminho de código próprio, quando essa
 * sub-fase existir.
 */
public record LancamentoContabilRequest(
        @NotNull Long empresaId,
        @NotNull LocalDate dataLancamento,
        @jakarta.validation.constraints.NotBlank @Size(max = 250) String historico,
        @Size(max = 30) String numeroDocumento,
        @NotEmpty(message = "Um lançamento contábil precisa de pelo menos 2 partidas (débito e crédito)")
        @Valid List<PartidaContabilRequest> partidas) {
}
