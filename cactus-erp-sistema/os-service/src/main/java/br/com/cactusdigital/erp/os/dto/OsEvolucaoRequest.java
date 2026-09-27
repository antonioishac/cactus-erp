package br.com.cactusdigital.erp.os.dto;

import jakarta.validation.constraints.Size;

public record OsEvolucaoRequest(
        @Size(max = 500) String observacao,
        boolean enviarEmail) {
}
