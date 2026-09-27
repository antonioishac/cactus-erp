package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.estoque.dto.LoteRequest;
import br.com.cactusdigital.erp.estoque.dto.LoteResponse;
import br.com.cactusdigital.erp.estoque.service.LoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Lote", description = "Rastreabilidade por lote de fabricação — obrigatório em alimentício/farma")
@RestController
@RequestMapping("/api/v1/lotes")
@RequiredArgsConstructor
public class LoteController {

    private final LoteService service;

    @Operation(summary = "Listar lotes", description = "Busca paginada, opcionalmente filtrada por produto")
    @ApiResponse(responseCode = "200", description = "Página de lotes")
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public PageResponse<LoteResponse> listar(@RequestParam(required = false) Long produtoId, Pageable pageable) {
        return PageResponse.of(service.listar(produtoId, pageable));
    }

    @Operation(summary = "Listar lotes próximos do vencimento (FEFO)", description = "Ordenados por data de validade mais próxima primeiro")
    @ApiResponse(responseCode = "200", description = "Página de lotes próximos do vencimento")
    @GetMapping("/produto/{produtoId}/vencimento-proximo")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public PageResponse<LoteResponse> listarProximosDoVencimento(
            @PathVariable Long produtoId,
            @Parameter(description = "Quantos dias à frente considerar 'próximo do vencimento'") @RequestParam(defaultValue = "30") int dias,
            Pageable pageable) {
        return PageResponse.of(service.listarProximosDoVencimento(produtoId, dias, pageable));
    }

    @Operation(summary = "Buscar lote por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lote encontrado"),
            @ApiResponse(responseCode = "404", description = "Lote não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public LoteResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar lote")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lote criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe lote com esse código pra esse produto",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ESTOQUE_GERENCIAR')")
    public ResponseEntity<LoteResponse> criar(@Valid @RequestBody LoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar lote")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lote atualizado"),
            @ApiResponse(responseCode = "404", description = "Lote não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe lote com esse código pra esse produto",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_GERENCIAR')")
    public LoteResponse atualizar(@PathVariable Long id, @Valid @RequestBody LoteRequest request) {
        return service.atualizar(id, request);
    }
}
