package br.com.cactusdigital.erp.bi.dto;

import java.time.LocalDate;

/** {@code dataReferencia} nula assume hoje. */
public record EtlExecucaoRequest(LocalDate dataReferencia) {
}
