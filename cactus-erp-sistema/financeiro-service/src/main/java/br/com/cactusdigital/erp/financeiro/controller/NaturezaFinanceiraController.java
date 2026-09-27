package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.financeiro.dto.NaturezaFinanceiraRequest;
import br.com.cactusdigital.erp.financeiro.dto.NaturezaFinanceiraResponse;
import br.com.cactusdigital.erp.financeiro.service.NaturezaFinanceiraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Natureza Financeira", description = "Categorização de título (Receita/Despesa) por empresa — fecha o gap contra FIN_NATUREZA_FINANCEIRA do fenix.sql")
@RestController
@RequestMapping("/api/v1/naturezas-financeiras")
@RequiredArgsConstructor
public class NaturezaFinanceiraController {

    private final NaturezaFinanceiraService service;

    @Operation(summary = "Listar naturezas financeiras ativas de uma empresa")
    @ApiResponse(responseCode = "200", description = "Lista de naturezas financeiras")
    @GetMapping
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public List<NaturezaFinanceiraResponse> listar(@RequestParam Long empresaId) {
        return service.listarAtivasDaEmpresa(empresaId);
    }

    @Operation(summary = "Criar natureza financeira")
    @ApiResponse(responseCode = "201", description = "Natureza criada")
    @PostMapping
    @PreAuthorize("hasAuthority('FINANCEIRO_GERENCIAR')")
    public ResponseEntity<NaturezaFinanceiraResponse> criar(@Valid @RequestBody NaturezaFinanceiraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar natureza financeira")
    @ApiResponse(responseCode = "200", description = "Natureza atualizada")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCEIRO_GERENCIAR')")
    public NaturezaFinanceiraResponse atualizar(@PathVariable Long id, @Valid @RequestBody NaturezaFinanceiraRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Inativar natureza financeira")
    @ApiResponse(responseCode = "204", description = "Natureza inativada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCEIRO_GERENCIAR')")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
