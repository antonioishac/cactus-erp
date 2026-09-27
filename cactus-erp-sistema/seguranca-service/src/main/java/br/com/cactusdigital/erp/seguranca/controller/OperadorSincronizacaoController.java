package br.com.cactusdigital.erp.seguranca.controller;

import br.com.cactusdigital.erp.seguranca.dto.OperadorSincronizacaoResponse;
import br.com.cactusdigital.erp.seguranca.service.OperadorSincronizacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Segurança — Operadores (sincronização)", description = "Composição de Usuario+Papel por empresa pronta pro sincronizacao-service provisionar PDV_OPERADOR (Fase 6)")
@RestController
@RequestMapping("/api/v1/operadores")
@RequiredArgsConstructor
public class OperadorSincronizacaoController {

    private final OperadorSincronizacaoService service;

    @Operation(summary = "Listar operadores de uma empresa",
            description = "Todo usuário com papel na empresa e colaboradorId preenchido, com nivelAutorizacao (G/S/O/E) já resolvido")
    @ApiResponse(responseCode = "200", description = "Operadores da empresa")
    @GetMapping
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public List<OperadorSincronizacaoResponse> listarPorEmpresa(@RequestParam Long empresaId) {
        return service.listarPorEmpresa(empresaId);
    }
}
