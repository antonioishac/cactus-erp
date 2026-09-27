package br.com.cactusdigital.erp.loja.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.loja.dto.CancelarPedidoRequest;
import br.com.cactusdigital.erp.loja.dto.PedidoResponse;
import br.com.cactusdigital.erp.loja.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pedidos", description = "Nasce do checkout de um Carrinho — diferente do Carrinho, fica atrás do RBAC de sempre (trabalho de staff/lojista, não do comprador)")
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService service;

    @Operation(summary = "Listar pedidos", description = "Filtra por lojaId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de pedidos")
    @GetMapping
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public PageResponse<PedidoResponse> listar(@RequestParam(required = false) Long lojaId, Pageable pageable) {
        return PageResponse.of(service.listar(lojaId, pageable));
    }

    @Operation(summary = "Buscar pedido por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public PedidoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Confirmar pagamento do pedido",
            description = "Ação manual/webhook-ready — sem gateway de pagamento real integrado nesta fatia. Debita o estoque de cada variante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido marcado como PAGO"),
            @ApiResponse(responseCode = "404", description = "Pedido ou variante não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido não está AGUARDANDO_PAGAMENTO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/confirmar-pagamento")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public PedidoResponse confirmarPagamento(@PathVariable Long id) {
        return service.confirmarPagamento(id);
    }

    @Operation(summary = "Marcar pedido em separação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido marcado como EM_SEPARACAO"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido não está PAGO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/marcar-em-separacao")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public PedidoResponse marcarEmSeparacao(@PathVariable Long id) {
        return service.marcarEmSeparacao(id);
    }

    @Operation(summary = "Marcar pedido enviado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido marcado como ENVIADO"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido não está EM_SEPARACAO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/marcar-enviado")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public PedidoResponse marcarEnviado(@PathVariable Long id) {
        return service.marcarEnviado(id);
    }

    @Operation(summary = "Marcar pedido entregue")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido marcado como ENTREGUE"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido não está ENVIADO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/marcar-entregue")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public PedidoResponse marcarEntregue(@PathVariable Long id) {
        return service.marcarEntregue(id);
    }

    @Operation(summary = "Cancelar pedido", description = "Só antes de ENVIADO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Pedido já enviado/entregue/cancelado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public PedidoResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelarPedidoRequest request) {
        return service.cancelar(id, request);
    }
}
