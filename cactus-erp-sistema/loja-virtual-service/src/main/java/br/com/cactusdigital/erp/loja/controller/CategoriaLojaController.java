package br.com.cactusdigital.erp.loja.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.loja.dto.CategoriaLojaRequest;
import br.com.cactusdigital.erp.loja.dto.CategoriaLojaResponse;
import br.com.cactusdigital.erp.loja.service.CategoriaLojaService;
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

@Tag(name = "Categorias da Loja", description = "Árvore de categorias por loja (nichos diferentes têm árvores diferentes) — carrega o schema de atributos dinâmicos via AtributoCategoria")
@RestController
@RequestMapping("/api/v1/categorias-loja")
@RequiredArgsConstructor
public class CategoriaLojaController {

    private final CategoriaLojaService service;

    @Operation(summary = "Listar categorias", description = "Filtra por lojaId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de categorias")
    @GetMapping
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public PageResponse<CategoriaLojaResponse> listar(@RequestParam(required = false) Long lojaId, Pageable pageable) {
        return PageResponse.of(service.listar(lojaId, pageable));
    }

    @Operation(summary = "Buscar categoria por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public CategoriaLojaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar categoria", description = "categoriaPaiId opcional — omitido cria uma categoria raiz")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada"),
            @ApiResponse(responseCode = "404", description = "Categoria pai não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<CategoriaLojaResponse> criar(@Valid @RequestBody CategoriaLojaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada"),
            @ApiResponse(responseCode = "404", description = "Categoria ou categoria pai não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public CategoriaLojaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaLojaRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
