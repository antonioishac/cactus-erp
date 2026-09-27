package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoGrupoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoGrupoResponse;
import br.com.cactusdigital.erp.cadastro.service.ProdutoGrupoService;
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

@Tag(name = "Produto — Grupo", description = "Categoria comercial de topo da hierarquia de produto")
@RestController
@RequestMapping("/api/v1/produtos-grupo")
@RequiredArgsConstructor
public class ProdutoGrupoController {

    private final ProdutoGrupoService service;

    @Operation(summary = "Listar grupos de produto", description = "Busca paginada por nome, opcional")
    @ApiResponse(responseCode = "200", description = "Página de grupos de produto")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public PageResponse<ProdutoGrupoResponse> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return PageResponse.of(service.listar(nome, pageable));
    }

    @Operation(summary = "Buscar grupo de produto por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo de produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Grupo de produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public ProdutoGrupoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar grupo de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Grupo de produto criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CRIAR')")
    public ResponseEntity<ProdutoGrupoResponse> criar(@Valid @RequestBody ProdutoGrupoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar grupo de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo de produto atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Grupo de produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EDITAR')")
    public ProdutoGrupoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoGrupoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir grupo de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Grupo de produto excluído"),
            @ApiResponse(responseCode = "404", description = "Grupo de produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
