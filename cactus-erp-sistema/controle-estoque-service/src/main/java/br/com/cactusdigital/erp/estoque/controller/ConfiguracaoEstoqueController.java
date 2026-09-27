package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.estoque.dto.ConfiguracaoEstoqueEmpresaRequest;
import br.com.cactusdigital.erp.estoque.dto.ConfiguracaoEstoqueEmpresaResponse;
import br.com.cactusdigital.erp.estoque.service.ConfiguracaoEstoqueEmpresaService;
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

@Tag(name = "Configuração de Estoque", description = "Método de custeio (média ponderada/FIFO) e limite de aprovação de ajuste, por empresa")
@RestController
@RequestMapping("/api/v1/configuracao-estoque")
@RequiredArgsConstructor
public class ConfiguracaoEstoqueController {

    private final ConfiguracaoEstoqueEmpresaService service;

    @Operation(summary = "Buscar configuração de estoque de uma empresa", description = "Devolve default (média ponderada, sem limite) se a empresa nunca configurou nada")
    @ApiResponse(responseCode = "200", description = "Configuração de estoque")
    @GetMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public ConfiguracaoEstoqueEmpresaResponse buscarPorEmpresa(@PathVariable Long empresaId) {
        return service.buscarPorEmpresa(empresaId);
    }

    @Operation(summary = "Atualizar configuração de estoque de uma empresa", description = "Cria se ainda não existir (upsert)")
    @ApiResponse(responseCode = "200", description = "Configuração atualizada")
    @PutMapping("/empresa/{empresaId}")
    @PreAuthorize("hasAuthority('ESTOQUE_GERENCIAR')")
    public ConfiguracaoEstoqueEmpresaResponse atualizar(@PathVariable Long empresaId, @Valid @RequestBody ConfiguracaoEstoqueEmpresaRequest request) {
        return service.atualizar(empresaId, request);
    }
}
