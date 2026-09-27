package br.com.cactusdigital.erp.bi.controller;

import br.com.cactusdigital.erp.bi.dto.EtlExecucaoRequest;
import br.com.cactusdigital.erp.bi.service.EtlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ETL", description = "Disparo manual do ETL do BI — além da execução agendada diária")
@RestController
@RequestMapping("/api/v1/etl")
@RequiredArgsConstructor
public class EtlController {

    private final EtlService service;

    @Operation(summary = "Executar ETL agora", description = "Atualiza dimensões e regenera os snapshots pra dataReferencia informada (default: hoje) — idempotente, pode ser rodado mais de uma vez pro mesmo dia")
    @ApiResponse(responseCode = "204", description = "ETL executado com sucesso")
    @PostMapping("/executar")
    @PreAuthorize("hasAuthority('BI_GERENCIAR')")
    public ResponseEntity<Void> executar(@RequestBody(required = false) EtlExecucaoRequest request) {
        LocalDate dataReferencia = request != null && request.dataReferencia() != null ? request.dataReferencia() : LocalDate.now();
        service.executar(dataReferencia);
        return ResponseEntity.noContent().build();
    }
}
