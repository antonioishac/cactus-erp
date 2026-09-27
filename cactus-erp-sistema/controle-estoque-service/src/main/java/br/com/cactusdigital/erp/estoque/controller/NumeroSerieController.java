package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.estoque.dto.NumeroSerieRequest;
import br.com.cactusdigital.erp.estoque.dto.NumeroSerieResponse;
import br.com.cactusdigital.erp.estoque.entity.SituacaoNumeroSerie;
import br.com.cactusdigital.erp.estoque.service.NumeroSerieService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Número de Série", description = "Rastreio unitário — eletrônicos/ativos que precisam identificar cada unidade")
@RestController
@RequestMapping("/api/v1/numeros-serie")
@RequiredArgsConstructor
public class NumeroSerieController {

    private final NumeroSerieService service;

    @Operation(summary = "Listar números de série de um produto")
    @ApiResponse(responseCode = "200", description = "Página de números de série")
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public PageResponse<NumeroSerieResponse> listarPorProduto(@RequestParam Long produtoId, Pageable pageable) {
        return PageResponse.of(service.listarPorProduto(produtoId, pageable));
    }

    @Operation(summary = "Listar números de série disponíveis num depósito")
    @ApiResponse(responseCode = "200", description = "Página de números de série disponíveis")
    @GetMapping("/deposito/{depositoId}/disponiveis")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public PageResponse<NumeroSerieResponse> listarDisponiveisNoDeposito(@PathVariable Long depositoId, Pageable pageable) {
        return PageResponse.of(service.listarDisponiveisNoDeposito(depositoId, pageable));
    }

    @Operation(summary = "Buscar número de série por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Número de série encontrado"),
            @ApiResponse(responseCode = "404", description = "Número de série não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public NumeroSerieResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Cadastrar número de série")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Número de série cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Depósito não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Esse número de série já está cadastrado pra esse produto",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ESTOQUE_GERENCIAR')")
    public ResponseEntity<NumeroSerieResponse> cadastrar(@Valid @RequestBody NumeroSerieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(request));
    }

    @Operation(summary = "Atualizar situação do número de série", description = "Ex: marcar como VENDIDO ou mover pra outro depósito")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Situação atualizada"),
            @ApiResponse(responseCode = "404", description = "Número de série ou depósito não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PatchMapping("/{id}/situacao")
    @PreAuthorize("hasAuthority('ESTOQUE_MOVIMENTAR')")
    public NumeroSerieResponse atualizarSituacao(
            @PathVariable Long id, @RequestParam SituacaoNumeroSerie situacao, @RequestParam(required = false) Long depositoId) {
        return service.atualizarSituacao(id, situacao, depositoId);
    }
}
