package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.financeiro.dto.AgrupamentoFluxoCaixa;
import br.com.cactusdigital.erp.financeiro.dto.FluxoCaixaPeriodoResponse;
import br.com.cactusdigital.erp.financeiro.service.FluxoCaixaService;
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

@Tag(name = "Relatórios Financeiros", description = "Fluxo de caixa projetado (Contas a Receber/Contas a Pagar ainda em aberto)")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class FluxoCaixaController {

    private final FluxoCaixaService service;

    @Operation(summary = "Fluxo de caixa projetado", description = "Soma valorSaldo de títulos ABERTA/PARCIAL com dataVencimento no intervalo, agrupado por dia ou mês — não é fluxo realizado (não considera baixas já pagas)")
    @ApiResponse(responseCode = "200", description = "Períodos do fluxo de caixa, em ordem cronológica, com saldo acumulado")
    @GetMapping("/fluxo-caixa")
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public List<FluxoCaixaPeriodoResponse> fluxoCaixa(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "MES") AgrupamentoFluxoCaixa agrupamento) {
        return service.calcular(empresaId, dataInicio, dataFim, agrupamento);
    }
}
