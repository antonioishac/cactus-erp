package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoSubgrupoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoSubgrupoResponse;
import br.com.cactusdigital.erp.cadastro.service.ProdutoSubgrupoService;
import org.springframework.http.ProblemDetail;
import br.com.cactusdigital.erp.common.rest.PageResponse;
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

@Tag(name = "Produto — Subgrupo", description = "Subcategoria comercial, N:1 com Grupo")
@RestController
@RequestMapping("/api/v1/produtos-subgrupo")
@RequiredArgsConstructor
public class ProdutoSubgrupoController {

    private final ProdutoSubgrupoService service;

    @Operation(summary = "Listar subgrupos de produto", description = "Busca paginada por grupo e/ou nome, ambos opcionais")
    @ApiResponse(responseCode = "200", description = "Página de subgrupos de produto")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public PageResponse<ProdutoSubgrupoResponse> listar(
            @Parameter(description = "Filtro por id do grupo") @RequestParam(required = false) Long produtoGrupoId,
            @Parameter(description = "Filtro por nome, contém, sem distinção de caixa") @RequestParam(required = false) String nome,
            Pageable pageable) {
        return PageResponse.of(service.listar(produtoGrupoId, nome, pageable));
    }

    @Operation(summary = "Buscar subgrupo de produto por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subgrupo de produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Subgrupo de produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public ProdutoSubgrupoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar subgrupo de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subgrupo de produto criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Grupo de produto informado não existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CRIAR')")
    public ResponseEntity<ProdutoSubgrupoResponse> criar(@Valid @RequestBody ProdutoSubgrupoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar subgrupo de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subgrupo de produto atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Subgrupo ou grupo de produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EDITAR')")
    public ProdutoSubgrupoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoSubgrupoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir subgrupo de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Subgrupo de produto excluído"),
            @ApiResponse(responseCode = "404", description = "Subgrupo de produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
