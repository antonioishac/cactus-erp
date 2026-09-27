package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.estoque.dto.InventarioFisicoAbrirRequest;
import br.com.cactusdigital.erp.estoque.dto.InventarioFisicoContagemRequest;
import br.com.cactusdigital.erp.estoque.dto.InventarioFisicoResponse;
import br.com.cactusdigital.erp.estoque.service.InventarioFisicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Inventário Físico", description = "Contagem cega ou por saldo — finalizar gera ajuste automático pra toda divergência")
@RestController
@RequestMapping("/api/v1/inventarios")
@RequiredArgsConstructor
public class InventarioFisicoController {

    private final InventarioFisicoService service;

    @Operation(summary = "Listar inventários de um depósito")
    @ApiResponse(responseCode = "200", description = "Página de inventários")
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public PageResponse<InventarioFisicoResponse> listarPorDeposito(@RequestParam Long depositoId, Pageable pageable) {
        return PageResponse.of(service.listarPorDeposito(depositoId, pageable));
    }

    @Operation(summary = "Buscar inventário por id", description = "Inclui os itens com o snapshot de saldo e a contagem, se já lançada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventário encontrado"),
            @ApiResponse(responseCode = "404", description = "Inventário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public InventarioFisicoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Abrir inventário", description = "Tira um snapshot do saldo atual de todo produto com estoque no depósito — cada um vira um item a contar")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inventário aberto"),
            @ApiResponse(responseCode = "404", description = "Depósito não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ESTOQUE_INVENTARIO_GERENCIAR')")
    public ResponseEntity<InventarioFisicoResponse> abrir(@Valid @RequestBody InventarioFisicoAbrirRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long responsavelId = jwt.getClaim("usuarioId");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.abrir(request, responsavelId));
    }

    @Operation(summary = "Iniciar contagem", description = "ABERTO -> EM_CONTAGEM")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem iniciada"),
            @ApiResponse(responseCode = "409", description = "Inventário não está ABERTO",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/iniciar-contagem")
    @PreAuthorize("hasAuthority('ESTOQUE_INVENTARIO_GERENCIAR')")
    public InventarioFisicoResponse iniciarContagem(@PathVariable Long id) {
        return service.iniciarContagem(id);
    }

    @Operation(summary = "Lançar contagem de um item", description = "Registra a quantidade contada e quem contou — não afeta saldo ainda, só finalizar() gera o ajuste")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contagem lançada"),
            @ApiResponse(responseCode = "404", description = "Inventário ou item não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Inventário não está ABERTO/EM_CONTAGEM",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}/itens/{itemId}/contagem")
    @PreAuthorize("hasAuthority('ESTOQUE_INVENTARIO_GERENCIAR')")
    public ResponseEntity<Void> lancarContagem(
            @PathVariable Long id, @PathVariable Long itemId, @Valid @RequestBody InventarioFisicoContagemRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long contadorId = jwt.getClaim("usuarioId");
        service.lancarContagem(id, itemId, request, contadorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Finalizar inventário", description = "Gera AJUSTE automático (ledger) pra cada item com divergência ≠ 0 e marca FINALIZADO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventário finalizado"),
            @ApiResponse(responseCode = "404", description = "Inventário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Inventário não está ABERTO/EM_CONTAGEM",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Existe item ainda não contado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/finalizar")
    @PreAuthorize("hasAuthority('ESTOQUE_INVENTARIO_GERENCIAR')")
    public InventarioFisicoResponse finalizar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.finalizar(id, jwt.getClaim("usuarioId"));
    }

    @Operation(summary = "Cancelar inventário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventário cancelado"),
            @ApiResponse(responseCode = "404", description = "Inventário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Inventário já está FINALIZADO",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('ESTOQUE_INVENTARIO_GERENCIAR')")
    public InventarioFisicoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
