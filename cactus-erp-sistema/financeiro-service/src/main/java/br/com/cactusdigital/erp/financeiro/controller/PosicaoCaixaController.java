package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.financeiro.dto.PosicaoCaixaResponse;
import br.com.cactusdigital.erp.financeiro.service.PosicaoCaixaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tesouraria — Posição de Caixa", description = "Saldo consolidado de todas as contas bancárias ativas da empresa, agrupado por tipo")
@RestController
@RequestMapping("/api/v1/tesouraria")
@RequiredArgsConstructor
public class PosicaoCaixaController {

    private final PosicaoCaixaService service;

    @Operation(summary = "Posição de caixa consolidada", description = "Soma saldoAtual de todas as contas ATIVAS da empresa, agrupado por TipoContaBancaria")
    @ApiResponse(responseCode = "200", description = "Posição de caixa")
    @GetMapping("/posicao-caixa")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PosicaoCaixaResponse posicaoCaixa(@RequestParam Long empresaId) {
        return service.consultar(empresaId);
    }
}
