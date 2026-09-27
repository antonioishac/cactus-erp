package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.estoque.dto.BaixarKitRequest;
import br.com.cactusdigital.erp.estoque.dto.ProdutoKitComponenteRequest;
import br.com.cactusdigital.erp.estoque.dto.ProdutoKitComponenteResponse;
import br.com.cactusdigital.erp.estoque.service.ProdutoKitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Kit / Produto Composto", description = "BOM simples — produto vendido como conjunto, baixa estoque dos componentes")
@RestController
@RequestMapping("/api/v1/kits/{produtoKitId}")
@RequiredArgsConstructor
public class ProdutoKitController {

    private final ProdutoKitService service;

    @Operation(summary = "Listar componentes de um kit")
    @ApiResponse(responseCode = "200", description = "Lista de componentes")
    @GetMapping("/componentes")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public List<ProdutoKitComponenteResponse> listarComponentes(@PathVariable Long produtoKitId) {
        return service.listarComponentes(produtoKitId);
    }

    @Operation(summary = "Adicionar componente ao kit")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Componente adicionado"),
            @ApiResponse(responseCode = "422", description = "Um produto não pode ser componente de si mesmo",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/componentes")
    @PreAuthorize("hasAuthority('ESTOQUE_GERENCIAR')")
    public ResponseEntity<ProdutoKitComponenteResponse> adicionarComponente(
            @PathVariable Long produtoKitId, @Valid @RequestBody ProdutoKitComponenteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarComponente(produtoKitId, request));
    }

    @Operation(summary = "Remover componente do kit")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Componente removido"),
            @ApiResponse(responseCode = "404", description = "Componente não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/componentes/{componenteId}")
    @PreAuthorize("hasAuthority('ESTOQUE_GERENCIAR')")
    public ResponseEntity<Void> removerComponente(@PathVariable Long produtoKitId, @PathVariable Long componenteId) {
        service.removerComponente(produtoKitId, componenteId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Baixar kit", description = "Gera SAIDA em cascata pra cada componente, na quantidade vendida × quantidade do componente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Baixa realizada"),
            @ApiResponse(responseCode = "404", description = "Depósito não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Saldo insuficiente de algum componente",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/baixar")
    @PreAuthorize("hasAuthority('ESTOQUE_MOVIMENTAR')")
    public ResponseEntity<Void> baixar(@PathVariable Long produtoKitId, @Valid @RequestBody BaixarKitRequest request, @AuthenticationPrincipal Jwt jwt) {
        service.baixar(produtoKitId, request, jwt.getClaim("usuarioId"));
        return ResponseEntity.noContent().build();
    }
}
