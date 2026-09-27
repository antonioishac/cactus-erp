package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoTipoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoTipoResponse;
import br.com.cactusdigital.erp.cadastro.service.ProdutoTipoService;
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

@Tag(name = "Produto — Tipo", description = "Lookup configurável pela empresa (ex: normal/kit/insumo) — distinto de Grupo/Subgrupo")
@RestController
@RequestMapping("/api/v1/produtos-tipos")
@RequiredArgsConstructor
public class ProdutoTipoController {

    private final ProdutoTipoService service;

    @Operation(summary = "Listar tipos de produto")
    @ApiResponse(responseCode = "200", description = "Página de tipos de produto")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public PageResponse<ProdutoTipoResponse> listar(
            @Parameter(description = "Filtro por descrição, contém, sem distinção de caixa") @RequestParam(required = false) String descricao,
            Pageable pageable) {
        return PageResponse.of(service.listar(descricao, pageable));
    }

    @Operation(summary = "Buscar tipo de produto por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo de produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public ProdutoTipoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar tipo de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de produto criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CRIAR')")
    public ResponseEntity<ProdutoTipoResponse> criar(@Valid @RequestBody ProdutoTipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar tipo de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de produto atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EDITAR')")
    public ProdutoTipoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoTipoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir tipo de produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo de produto excluído"),
            @ApiResponse(responseCode = "404", description = "Tipo de produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
