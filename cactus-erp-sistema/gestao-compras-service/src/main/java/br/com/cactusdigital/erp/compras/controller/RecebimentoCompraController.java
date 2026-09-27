package br.com.cactusdigital.erp.compras.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.compras.dto.RecebimentoCompraRequest;
import br.com.cactusdigital.erp.compras.dto.RecebimentoCompraResponse;
import br.com.cactusdigital.erp.compras.service.RecebimentoCompraService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recebimento de Compra", description = "Confirma recebimento total/parcial de um pedido — gera ENTRADA no controle-estoque-service por item")
@RestController
@RequestMapping("/api/v1/recebimentos")
@RequiredArgsConstructor
public class RecebimentoCompraController {

    private final RecebimentoCompraService service;

    @Operation(summary = "Listar recebimentos de um pedido")
    @ApiResponse(responseCode = "200", description = "Página de recebimentos")
    @GetMapping
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public PageResponse<RecebimentoCompraResponse> listarPorPedido(@RequestParam Long pedidoId, Pageable pageable) {
        return PageResponse.of(service.listarPorPedido(pedidoId, pageable));
    }

    @Operation(summary = "Buscar recebimento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recebimento encontrado"),
            @ApiResponse(responseCode = "404", description = "Recebimento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public RecebimentoCompraResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Registrar recebimento", description = "Pedido precisa estar ENVIADO ou PARCIALMENTE_RECEBIDO. "
            + "Cada item gera uma ENTRADA real no controle-estoque-service (ver limitação de atomicidade documentada em EstoqueClient) "
            + "e atualiza a situação do pedido pra PARCIALMENTE_RECEBIDO ou RECEBIDO_TOTAL")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recebimento registrado"),
            @ApiResponse(responseCode = "404", description = "Pedido ou item de pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido não está ENVIADO/PARCIALMENTE_RECEBIDO",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Quantidade recebida excede a quantidade pendente do item",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha ao chamar o controle-estoque-service",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('COMPRAS_RECEBER')")
    public ResponseEntity<RecebimentoCompraResponse> registrar(@Valid @RequestBody RecebimentoCompraRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("usuarioId");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request, usuarioId));
    }
}
