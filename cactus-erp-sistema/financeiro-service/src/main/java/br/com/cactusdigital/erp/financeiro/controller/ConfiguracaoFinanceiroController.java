package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.financeiro.dto.ConfiguracaoFinanceiroEmpresaRequest;
import br.com.cactusdigital.erp.financeiro.dto.ConfiguracaoFinanceiroEmpresaResponse;
import br.com.cactusdigital.erp.financeiro.service.ConfiguracaoFinanceiroEmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Configuração Financeira", description = "Percentual de multa/juros por atraso, por empresa")
@RestController
@RequestMapping("/api/v1/configuracao-financeiro")
@RequiredArgsConstructor
public class ConfiguracaoFinanceiroController {

    private final ConfiguracaoFinanceiroEmpresaService service;

    @Operation(summary = "Buscar configuração financeira de uma empresa", description = "Devolve default (sem multa/juros) se a empresa nunca configurou nada")
    @ApiResponse(responseCode = "200", description = "Configuração financeira")
    @GetMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public ConfiguracaoFinanceiroEmpresaResponse buscarPorEmpresa(@PathVariable Long empresaId) {
        return service.buscarPorEmpresa(empresaId);
    }

    @Operation(summary = "Atualizar configuração financeira de uma empresa", description = "Cria se ainda não existir (upsert)")
    @ApiResponse(responseCode = "200", description = "Configuração atualizada")
    @PutMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAuthority('FINANCEIRO_GERENCIAR')")
    public ConfiguracaoFinanceiroEmpresaResponse atualizar(@PathVariable Long empresaId, @Valid @RequestBody ConfiguracaoFinanceiroEmpresaRequest request) {
        return service.atualizar(empresaId, request);
    }
}
