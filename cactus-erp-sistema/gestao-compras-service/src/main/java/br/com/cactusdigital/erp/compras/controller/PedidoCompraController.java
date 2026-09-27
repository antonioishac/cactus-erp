package br.com.cactusdigital.erp.compras.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraItemRequest;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraItemResponse;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraRequest;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraResponse;
import br.com.cactusdigital.erp.compras.service.PedidoCompraService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pedido de Compra", description = "RASCUNHO → (enviar) → AGUARDANDO_APROVACAO/ENVIADO → PARCIALMENTE_RECEBIDO/RECEBIDO_TOTAL")
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoCompraController {

    private final PedidoCompraService service;

    @Operation(summary = "Listar pedidos de compra", description = "Opcionalmente filtrado por fornecedor")
    @ApiResponse(responseCode = "200", description = "Página de pedidos")
    @GetMapping
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public PageResponse<PedidoCompraResponse> listar(@RequestParam(required = false) Long fornecedorId, Pageable pageable) {
        return PageResponse.of(service.listar(fornecedorId, pageable));
    }

    @Operation(summary = "Buscar pedido por id", description = "Inclui os itens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public PedidoCompraResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar pedido de compra", description = "Nasce em RASCUNHO, sem itens — adicione via POST /pedidos/{id}/itens. "
            + "origemTipo=AVULSO pra pedido sem vínculo, REQUISICAO pra nascer direto de uma requisição (marca a requisição como PEDIDO_GERADO)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('COMPRAS_GERENCIAR')")
    public ResponseEntity<PedidoCompraResponse> criar(@Valid @RequestBody PedidoCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Adicionar item ao pedido", description = "Só permitido em RASCUNHO — recalcula os totais do pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido não está em RASCUNHO",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/itens")
    @PreAuthorize("hasAuthority('COMPRAS_GERENCIAR')")
    public ResponseEntity<PedidoCompraItemResponse> adicionarItem(@PathVariable Long id, @Valid @RequestBody PedidoCompraItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarItem(id, request));
    }

    @Operation(summary = "Remover item do pedido", description = "Só permitido em RASCUNHO")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removido"),
            @ApiResponse(responseCode = "404", description = "Pedido ou item não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido não está em RASCUNHO",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/itens/{itemId}")
    @PreAuthorize("hasAuthority('COMPRAS_GERENCIAR')")
    public ResponseEntity<Void> removerItem(@PathVariable Long id, @PathVariable Long itemId) {
        service.removerItem(id, itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enviar pedido ao fornecedor",
            description = "Se valorTotal exceder o limite configurado (ConfiguracaoComprasEmpresa), vai para AGUARDANDO_APROVACAO em vez de ENVIADO direto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido enviado ou aguardando aprovação"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido não está em RASCUNHO",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Pedido sem itens",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/enviar")
    @PreAuthorize("hasAuthority('COMPRAS_GERENCIAR')")
    public PedidoCompraResponse enviar(@PathVariable Long id) {
        return service.enviar(id);
    }

    @Operation(summary = "Aprovar pedido acima do limiar", description = "aprovadorId/dataAprovacao são setados a partir do JWT de quem chama, nunca do corpo da requisição")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido aprovado e enviado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido não está AGUARDANDO_APROVACAO",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasAuthority('COMPRAS_APROVAR')")
    public PedidoCompraResponse aprovar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long aprovadorId = jwt.getClaim("usuarioId");
        return service.aprovar(id, aprovadorId);
    }

    @Operation(summary = "Cancelar pedido", description = "Não permitido depois de já ter recebido algo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido já recebeu algo ou já está cancelado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('COMPRAS_GERENCIAR')")
    public PedidoCompraResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
