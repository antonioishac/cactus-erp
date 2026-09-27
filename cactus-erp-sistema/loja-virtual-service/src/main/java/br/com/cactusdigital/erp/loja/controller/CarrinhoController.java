package br.com.cactusdigital.erp.loja.controller;

import br.com.cactusdigital.erp.loja.dto.AdicionarItemCarrinhoRequest;
import br.com.cactusdigital.erp.loja.dto.AtualizarItemCarrinhoRequest;
import br.com.cactusdigital.erp.loja.dto.CarrinhoResponse;
import br.com.cactusdigital.erp.loja.dto.CriarCarrinhoRequest;
import br.com.cactusdigital.erp.loja.dto.FinalizarCompraRequest;
import br.com.cactusdigital.erp.loja.dto.ItemCarrinhoResponse;
import br.com.cactusdigital.erp.loja.dto.PedidoResponse;
import br.com.cactusdigital.erp.loja.service.CarrinhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rotas PÚBLICAS (sem Bearer OAuth2) — {@code ResourceServerConfig} libera
 * {@code /api/v1/carrinhos/**} inteiro, o {@code token} do carrinho é a própria
 * credencial de posse. Ver Javadoc de {@code Carrinho} e CLAUDE.md ("Fase 13,
 * sub-fase 2 — decisão de segurança"). Nenhum método aqui tem {@code @PreAuthorize},
 * de propósito.
 */
@Tag(name = "Carrinho (público)", description = "Sem autenticação — o token do carrinho é a própria credencial. Comprador anônimo da loja usa estas rotas direto")
@RestController
@RequestMapping("/api/v1/carrinhos")
@RequiredArgsConstructor
public class CarrinhoController {

    private final CarrinhoService service;

    @Operation(summary = "Criar carrinho", description = "Devolve um token — guarde-o, é a única forma de operar este carrinho depois")
    @ApiResponse(responseCode = "201", description = "Carrinho criado")
    @PostMapping
    public ResponseEntity<CarrinhoResponse> criar(@Valid @RequestBody CriarCarrinhoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Buscar carrinho pelo token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho encontrado"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{token}")
    public CarrinhoResponse buscarPorToken(@PathVariable String token) {
        return service.buscarPorToken(token);
    }

    @Operation(summary = "Adicionar item ao carrinho", description = "precoUnitario é capturado no momento (preço promocional se houver) — não muda retroativamente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou variante não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Carrinho já finalizado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Estoque insuficiente", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{token}/itens")
    public ResponseEntity<ItemCarrinhoResponse> adicionarItem(@PathVariable String token, @Valid @RequestBody AdicionarItemCarrinhoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarItem(token, request));
    }

    @Operation(summary = "Atualizar quantidade de um item do carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item atualizado"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Carrinho já finalizado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{token}/itens/{itemId}")
    public ItemCarrinhoResponse atualizarItem(@PathVariable String token, @PathVariable Long itemId, @Valid @RequestBody AtualizarItemCarrinhoRequest request) {
        return service.atualizarItem(token, itemId, request);
    }

    @Operation(summary = "Remover item do carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removido"),
            @ApiResponse(responseCode = "404", description = "Carrinho ou item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Carrinho já finalizado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{token}/itens/{itemId}")
    public ResponseEntity<Void> removerItem(@PathVariable String token, @PathVariable Long itemId) {
        service.removerItem(token, itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Abandonar carrinho", description = "Libera a reserva de estoque de todos os itens e marca o carrinho ABANDONADO — use quando o comprador desiste antes do checkout")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrinho abandonado"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Carrinho já finalizado/abandonado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{token}/abandonar")
    public ResponseEntity<Void> abandonar(@PathVariable String token) {
        service.abandonar(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Finalizar compra (checkout)", description = "Guest checkout — capta identidade/entrega inline, converte o carrinho num Pedido AGUARDANDO_PAGAMENTO. Nenhum gateway de pagamento é chamado aqui")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado"),
            @ApiResponse(responseCode = "404", description = "Carrinho não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Carrinho já finalizado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Carrinho vazio", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{token}/finalizar-compra")
    public ResponseEntity<PedidoResponse> finalizarCompra(@PathVariable String token, @Valid @RequestBody FinalizarCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.finalizarCompra(token, request));
    }
}
