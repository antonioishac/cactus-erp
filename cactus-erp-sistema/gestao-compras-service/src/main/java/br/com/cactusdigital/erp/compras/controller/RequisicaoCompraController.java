package br.com.cactusdigital.erp.compras.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.compras.dto.RequisicaoCompraRequest;
import br.com.cactusdigital.erp.compras.dto.RequisicaoCompraResponse;
import br.com.cactusdigital.erp.compras.service.RequisicaoCompraService;
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

@Tag(name = "Requisição de Compra", description = "Ponto de partida do fluxo de compras — pode gerar cotações ou virar pedido direto")
@RestController
@RequestMapping("/api/v1/requisicoes")
@RequiredArgsConstructor
public class RequisicaoCompraController {

    private final RequisicaoCompraService service;

    @Operation(summary = "Listar requisições", description = "Opcionalmente filtrada por colaborador solicitante")
    @ApiResponse(responseCode = "200", description = "Página de requisições")
    @GetMapping
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public PageResponse<RequisicaoCompraResponse> listar(@RequestParam(required = false) Long colaboradorId, Pageable pageable) {
        return PageResponse.of(service.listar(colaboradorId, pageable));
    }

    @Operation(summary = "Buscar requisição por id", description = "Inclui os itens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requisição encontrada"),
            @ApiResponse(responseCode = "404", description = "Requisição não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public RequisicaoCompraResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar requisição de compra", description = "Itens vão inline no corpo da requisição")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Requisição criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('COMPRAS_REQUISITAR')")
    public ResponseEntity<RequisicaoCompraResponse> criar(@Valid @RequestBody RequisicaoCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Cancelar requisição", description = "Só permitido se ainda não gerou pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requisição cancelada"),
            @ApiResponse(responseCode = "404", description = "Requisição não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Requisição já gerou pedido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('COMPRAS_REQUISITAR')")
    public RequisicaoCompraResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
