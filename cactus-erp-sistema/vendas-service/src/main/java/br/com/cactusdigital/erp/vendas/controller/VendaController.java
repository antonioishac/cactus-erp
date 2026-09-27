package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaFreteRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaFreteResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaItemRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaItemResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaResponse;
import br.com.cactusdigital.erp.vendas.service.VendaService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Venda", description = "RASCUNHO → (confirmar) → CONFIRMADA (gera comissão e baixa estoque) → CANCELADA (só a partir de RASCUNHO)")
@RestController
@RequestMapping("/api/v1/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService service;

    @Operation(summary = "Listar vendas", description = "Opcionalmente filtrado por cliente")
    @ApiResponse(responseCode = "200", description = "Página de vendas")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public PageResponse<VendaResponse> listar(@RequestParam(required = false) Long clienteId, Pageable pageable) {
        return PageResponse.of(service.listar(clienteId, pageable));
    }

    @Operation(summary = "Buscar venda por id", description = "Inclui os itens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venda encontrada"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public VendaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar venda avulsa", description = "Nasce em RASCUNHO, sem itens — adicione via POST /vendas/{id}/itens. Pra gerar venda a partir de um orçamento, use POST /orcamentos/{id}/gerar-venda")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venda criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Condição de pagamento, tipo de nota fiscal ou vendedor não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('VENDAS_VENDER')")
    public ResponseEntity<VendaResponse> criar(@Valid @RequestBody VendaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Adicionar item à venda", description = "Só permitido em RASCUNHO — exige depositoId (produto×depósito, controle-estoque-service)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Venda não está em RASCUNHO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/itens")
    @PreAuthorize("hasAuthority('VENDAS_VENDER')")
    public ResponseEntity<VendaItemResponse> adicionarItem(@PathVariable Long id, @Valid @RequestBody VendaItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarItem(id, request));
    }

    @Operation(summary = "Atualizar item da venda", description = "Substitui produto/depósito/quantidade/preço — principal uso: definir o depósito de um item copiado de um orçamento (que não conhece estoque). Só permitido em RASCUNHO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item atualizado"),
            @ApiResponse(responseCode = "404", description = "Venda ou item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Venda não está em RASCUNHO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}/itens/{itemId}")
    @PreAuthorize("hasAuthority('VENDAS_VENDER')")
    public VendaItemResponse atualizarItem(@PathVariable Long id, @PathVariable Long itemId, @Valid @RequestBody VendaItemRequest request) {
        return service.atualizarItem(id, itemId, request);
    }

    @Operation(summary = "Remover item da venda", description = "Só permitido em RASCUNHO")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removido"),
            @ApiResponse(responseCode = "404", description = "Venda ou item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Venda não está em RASCUNHO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/itens/{itemId}")
    @PreAuthorize("hasAuthority('VENDAS_VENDER')")
    public ResponseEntity<Void> removerItem(@PathVariable Long id, @PathVariable Long itemId) {
        service.removerItem(id, itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Definir/atualizar frete da venda", description = "1:1 com a venda — chamar de novo substitui os dados; só permitido em RASCUNHO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Frete definido"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Venda não está em RASCUNHO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}/frete")
    @PreAuthorize("hasAuthority('VENDAS_VENDER')")
    public VendaFreteResponse definirFrete(@PathVariable Long id, @Valid @RequestBody VendaFreteRequest request) {
        return service.definirFrete(id, request);
    }

    @Operation(summary = "Buscar o frete da venda", description = "Consumido pelo fiscal-service (Fase 11) ao compor um CT-e a partir da venda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Frete encontrado"),
            @ApiResponse(responseCode = "404", description = "Venda não tem frete definido", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}/frete")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public VendaFreteResponse buscarFrete(@PathVariable Long id) {
        return service.buscarFrete(id);
    }

    @Operation(summary = "Confirmar venda", description = "RASCUNHO → CONFIRMADA: gera VendaComissao e chama o controle-estoque-service (SAIDA) uma vez por item — exige depositoId em todo item")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venda confirmada"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Venda não está em RASCUNHO", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Venda sem itens ou item sem depósito definido", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha ao integrar com o controle-estoque-service", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/confirmar")
    @PreAuthorize("hasAuthority('VENDAS_VENDER')")
    public VendaResponse confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @Operation(summary = "Cancelar venda", description = "Só permitido em RASCUNHO — depois de confirmada, estoque e comissão já foram gerados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venda cancelada"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Venda não está em RASCUNHO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('VENDAS_CANCELAR')")
    public VendaResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
