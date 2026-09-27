package br.com.cactusdigital.erp.compras.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.compras.dto.CotacaoCompraRequest;
import br.com.cactusdigital.erp.compras.dto.CotacaoCompraResponse;
import br.com.cactusdigital.erp.compras.dto.FornecedorCotacaoItemRequest;
import br.com.cactusdigital.erp.compras.dto.FornecedorCotacaoRequest;
import br.com.cactusdigital.erp.compras.dto.FornecedorCotacaoResponse;
import br.com.cactusdigital.erp.compras.dto.GerarPedidoDeCotacaoRequest;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraResponse;
import br.com.cactusdigital.erp.compras.service.CotacaoCompraService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cotação de Compra", description = "N fornecedores concorrentes por requisição — gerar pedido a partir do vencedor finaliza a cotação")
@RestController
@RequestMapping("/api/v1/cotacoes")
@RequiredArgsConstructor
public class CotacaoCompraController {

    private final CotacaoCompraService service;
    private final PedidoCompraService pedidoCompraService;

    @Operation(summary = "Listar cotações de uma requisição")
    @ApiResponse(responseCode = "200", description = "Página de cotações")
    @GetMapping
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public PageResponse<CotacaoCompraResponse> listarPorRequisicao(@RequestParam Long requisicaoId, Pageable pageable) {
        return PageResponse.of(service.listarPorRequisicao(requisicaoId, pageable));
    }

    @Operation(summary = "Buscar cotação por id", description = "Inclui os fornecedores concorrentes e os itens de cada um")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotação encontrada"),
            @ApiResponse(responseCode = "404", description = "Cotação não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public CotacaoCompraResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar cotação", description = "Vinculada a uma requisição de compra existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cotação criada"),
            @ApiResponse(responseCode = "404", description = "Requisição não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('COMPRAS_COTAR')")
    public ResponseEntity<CotacaoCompraResponse> criar(@Valid @RequestBody CotacaoCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Cancelar cotação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotação cancelada"),
            @ApiResponse(responseCode = "404", description = "Cotação não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Cotação não está ABERTA",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('COMPRAS_COTAR')")
    public CotacaoCompraResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @Operation(summary = "Adicionar fornecedor concorrente à cotação")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fornecedor adicionado"),
            @ApiResponse(responseCode = "404", description = "Cotação não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Cotação não está ABERTA",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/fornecedores")
    @PreAuthorize("hasAuthority('COMPRAS_COTAR')")
    public ResponseEntity<FornecedorCotacaoResponse> adicionarFornecedor(
            @PathVariable Long id, @Valid @RequestBody FornecedorCotacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarFornecedor(id, request));
    }

    @Operation(summary = "Adicionar item cotado a um fornecedor da cotação",
            description = "Recalcula automaticamente valorSubtotal/valorDesconto/valorTotal do fornecedor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado, fornecedor com totais recalculados"),
            @ApiResponse(responseCode = "404", description = "Cotação ou fornecedor da cotação não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Cotação não está ABERTA",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/fornecedores/{fornecedorCotacaoId}/itens")
    @PreAuthorize("hasAuthority('COMPRAS_COTAR')")
    public ResponseEntity<FornecedorCotacaoResponse> adicionarItem(
            @PathVariable Long id, @PathVariable Long fornecedorCotacaoId, @Valid @RequestBody FornecedorCotacaoItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarItem(id, fornecedorCotacaoId, request));
    }

    @Operation(summary = "Gerar pedido a partir do fornecedor vencedor",
            description = "Copia os itens do fornecedor pra um PedidoCompra novo (RASCUNHO); finaliza a cotação e marca a requisição de origem como PEDIDO_GERADO")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido gerado"),
            @ApiResponse(responseCode = "404", description = "Cotação ou fornecedor da cotação não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Fornecedor sem itens cotados",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/fornecedores/{fornecedorCotacaoId}/gerar-pedido")
    @PreAuthorize("hasAuthority('COMPRAS_COTAR')")
    public ResponseEntity<PedidoCompraResponse> gerarPedido(
            @PathVariable Long id, @PathVariable Long fornecedorCotacaoId, @Valid @RequestBody GerarPedidoDeCotacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoCompraService.gerarAPartirDeCotacao(id, fornecedorCotacaoId, request));
    }
}
