package br.com.cactusdigital.erp.fiscal.controller;

import br.com.cactusdigital.erp.fiscal.sped.SpedContribuicoesExportService;
import br.com.cactusdigital.erp.fiscal.sped.SpedFiscalIcmsIpiExportService;
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

@Tag(name = "SPED Fiscal", description = "Export EFD ICMS/IPI e EFD Contribuições (PIS/COFINS) — ver Javadoc de SpedFiscalIcmsIpiExportService/SpedContribuicoesExportService pro recorte de escopo e nível de confiança do layout")
@RestController
@RequestMapping("/api/v1/sped")
@RequiredArgsConstructor
public class SpedFiscalController {

    private final SpedFiscalIcmsIpiExportService icmsIpiService;
    private final SpedContribuicoesExportService contribuicoesService;

    @Operation(summary = "Gerar arquivo EFD ICMS/IPI (Blocos 0/C/D/E)",
            description = "Texto delimitado por pipe — só documentos AUTORIZADA, só saídas (NF-e/CT-e), apuração sem créditos de entrada; layout campo-a-campo nunca validado contra o PVA da Receita nesta sessão, ver Javadoc de SpedFiscalIcmsIpiExportService")
    @ApiResponse(responseCode = "200", description = "Arquivo EFD ICMS/IPI gerado (text/plain)")
    @GetMapping("/efd-icms-ipi")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public ResponseEntity<String> gerarEfdIcmsIpi(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        String conteudo = icmsIpiService.gerarEfdIcmsIpi(empresaId, dataInicio, dataFim);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(conteudo);
    }

    @Operation(summary = "Gerar arquivo EFD Contribuições (Blocos A/C/M)",
            description = "Texto delimitado por pipe — só documentos AUTORIZADA, regime não-cumulativo assumido, sem créditos de entrada; layout campo-a-campo nunca validado contra o PVA da Receita nesta sessão, ver Javadoc de SpedContribuicoesExportService")
    @ApiResponse(responseCode = "200", description = "Arquivo EFD Contribuições gerado (text/plain)")
    @GetMapping("/efd-contribuicoes")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public ResponseEntity<String> gerarEfdContribuicoes(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        String conteudo = contribuicoesService.gerarEfdContribuicoes(empresaId, dataInicio, dataFim);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(conteudo);
    }
}
