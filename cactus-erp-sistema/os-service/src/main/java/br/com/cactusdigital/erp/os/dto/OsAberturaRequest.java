package br.com.cactusdigital.erp.os.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Nasce sem itens/equipamentos — adicione via sub-recursos (POST .../itens, .../equipamentos). */
public record OsAberturaRequest(
        @NotNull Long osStatusId,
        @NotNull Long empresaId,
        @NotNull Long clienteId,
        Long colaboradorId,
        @Size(max = 20) String numero,
        LocalDate dataInicio,
        @Size(max = 8) String horaInicio,
        LocalDate dataPrevisao,
        @Size(max = 8) String horaPrevisao,
        @Size(max = 50) String nomeContato,
        @Size(max = 15) String foneContato,
        @Size(max = 500) String observacaoCliente,
        @Size(max = 500) String observacaoAbertura) {
}
