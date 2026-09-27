package br.com.cactusdigital.erp.contabilidade.controller;

import br.com.cactusdigital.erp.contabilidade.dto.BalanceteItemResponse;
import br.com.cactusdigital.erp.contabilidade.dto.RazaoItemResponse;
import br.com.cactusdigital.erp.contabilidade.service.RelatorioContabilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Relatórios Contábeis", description = "Balancete e Razão — calculados sob demanda a partir do Livro Diário, sem snapshot persistido")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioContabilController {

    private final RelatorioContabilService service;

    @Operation(summary = "Balancete de verificação", description = "Uma linha por conta analítica com movimento no período — só entram contas com ao menos 1 partida lançada")
    @ApiResponse(responseCode = "200", description = "Linhas do balancete, ordenadas por código de conta")
    @GetMapping("/balancete")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public List<BalanceteItemResponse> balancete(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return service.balancete(empresaId, dataInicio, dataFim);
    }

    @Operation(summary = "Livro Razão de uma conta", description = "Extrato cronológico das partidas da conta no período, com saldo corrido")
    @ApiResponse(responseCode = "200", description = "Linhas do razão, em ordem cronológica")
    @GetMapping("/razao")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public List<RazaoItemResponse> razao(
            @RequestParam Long contaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return service.razao(contaId, dataInicio, dataFim);
    }
}
