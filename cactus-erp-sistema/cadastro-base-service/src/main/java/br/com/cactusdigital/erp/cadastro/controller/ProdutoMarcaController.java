package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoMarcaRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoMarcaResponse;
import br.com.cactusdigital.erp.cadastro.service.ProdutoMarcaService;
import org.springframework.http.ProblemDetail;
import br.com.cactusdigital.erp.common.rest.PageResponse;
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

@Tag(name = "Produto — Marca", description = "Marca do produto (enriquecimento do fenix.sql, sem equivalente no cactus-pdv)")
@RestController
@RequestMapping("/api/v1/produtos-marca")
@RequiredArgsConstructor
public class ProdutoMarcaController {

    private final ProdutoMarcaService service;

    @Operation(summary = "Listar marcas de produto", description = "Busca paginada por nome, opcional")
    @ApiResponse(responseCode = "200", description = "Página de marcas de produto")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public PageResponse<ProdutoMarcaResponse> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return PageResponse.of(service.listar(nome, pageable));
    }

    @Operation(summary = "Buscar marca de produto por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marca de produto encontrada"),
            @ApiResponse(responseCode = "404", description = "Marca de produto não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public ProdutoMarcaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar marca de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Marca de produto criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CRIAR')")
    public ResponseEntity<ProdutoMarcaResponse> criar(@Valid @RequestBody ProdutoMarcaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar marca de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marca de produto atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Marca de produto não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EDITAR')")
    public ProdutoMarcaResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoMarcaRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir marca de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Marca de produto excluída"),
            @ApiResponse(responseCode = "404", description = "Marca de produto não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
