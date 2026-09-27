package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.financeiro.dto.BancoResponse;
import br.com.cactusdigital.erp.financeiro.service.BancoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Bancos", description = "Catálogo global de bancos brasileiros (código FEBRABAN) — só leitura, seedado por migration")
@RestController
@RequestMapping("/api/v1/bancos")
@RequiredArgsConstructor
public class BancoController {

    private final BancoService service;

    @Operation(summary = "Listar bancos", description = "Ordenado por código FEBRABAN")
    @ApiResponse(responseCode = "200", description = "Lista de bancos")
    @GetMapping
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public List<BancoResponse> listar() {
        return service.listar();
    }
}
