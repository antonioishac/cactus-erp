package br.com.cactusdigital.erp.os.dto;

import java.time.LocalDate;

public record OsEvolucaoResponse(
        Long id,
        LocalDate dataRegistro,
        String horaRegistro,
        String observacao,
        boolean enviarEmail) {
}
