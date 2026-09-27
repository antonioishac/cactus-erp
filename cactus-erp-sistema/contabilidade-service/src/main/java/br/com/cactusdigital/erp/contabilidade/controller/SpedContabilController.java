package br.com.cactusdigital.erp.contabilidade.controller;

import br.com.cactusdigital.erp.contabilidade.sped.SpedContabilExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SPED Contábil", description = "Export ECD (Escrituração Contábil Digital), Bloco I — ver Javadoc de SpedContabilExportService pro nível de confiança do layout")
@RestController
@RequestMapping("/api/v1/sped")
@RequiredArgsConstructor
public class SpedContabilController {

    private final SpedContabilExportService service;

    @Operation(summary = "Gerar arquivo ECD (Bloco I)",
            description = "Texto delimitado por pipe, mesmo layout geral do SPED oficial — layout campo-a-campo nunca validado contra o PVA da Receita nesta sessão, ver Javadoc de SpedContabilExportService")
    @ApiResponse(responseCode = "200", description = "Arquivo ECD gerado (text/plain)")
    @GetMapping("/ecd")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public ResponseEntity<String> gerarEcd(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        String conteudo = service.gerarEcd(empresaId, dataInicio, dataFim);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(conteudo);
    }
}
