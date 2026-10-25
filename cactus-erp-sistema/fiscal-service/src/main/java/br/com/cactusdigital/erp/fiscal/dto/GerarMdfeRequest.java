package br.com.cactusdigital.erp.fiscal.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Agrupa N CT-e já {@code AUTORIZADA} da MESMA empresa numa viagem. Carga (valor/peso),
 * município de carregamento (= município do emitente) e municípios de descarga (= endereço
 * do destinatário de cada CT-e) são derivados — aqui só o que é da viagem em si
 * (veículo/condutor/data), que nenhum CT-e carrega.
 */
public record GerarMdfeRequest(
        @NotNull Long empresaId,
        @NotEmpty List<@NotNull Long> cteIds,
        @NotNull @Size(min = 7, max = 7) String veiculoPlaca,
        @Size(min = 2, max = 2) String veiculoUf,
        @Size(max = 11) String veiculoRenavam,
        @NotNull BigDecimal veiculoTaraKg,
        BigDecimal veiculoCapacidadeKg,
        @NotNull @Size(max = 60) String condutorNome,
        @NotNull @Pattern(regexp = "\\d{11}", message = "CPF do condutor deve ter 11 dígitos") String condutorCpf,
        /** {@code null} = usa o RNTRC do primeiro CT-e vinculado. */
        @Size(max = 8) String rntrc,
        /** {@code null} = data de geração do MDF-e. */
        LocalDate dataInicioViagem) {
}
