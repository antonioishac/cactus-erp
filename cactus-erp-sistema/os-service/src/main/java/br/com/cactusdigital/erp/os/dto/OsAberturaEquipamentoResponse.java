package br.com.cactusdigital.erp.os.dto;

public record OsAberturaEquipamentoResponse(
        Long id,
        Long osEquipamentoId,
        String osEquipamentoNome,
        String numeroSerie,
        String tipoCobertura) {
}
