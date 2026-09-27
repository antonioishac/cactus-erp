package br.com.cactusdigital.erp.loja.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.loja.dto.ProdutoLojaRequest;
import br.com.cactusdigital.erp.loja.dto.ProdutoLojaResponse;
import br.com.cactusdigital.erp.loja.dto.VarianteProdutoLojaRequest;
import br.com.cactusdigital.erp.loja.dto.VarianteProdutoLojaResponse;
import br.com.cactusdigital.erp.loja.service.ProdutoLojaService;
import br.com.cactusdigital.erp.loja.service.VarianteProdutoLojaService;
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

@Tag(name = "Produtos da Loja", description = "O 'modelo' vendido (ex: Vestido Floral Midi) — não é vendável direto, ver VarianteProdutoLoja pro SKU de fato")
@RestController
@RequestMapping("/api/v1/produtos-loja")
@RequiredArgsConstructor
public class ProdutoLojaController {

    private final ProdutoLojaService service;
    private final VarianteProdutoLojaService varianteService;

    @Operation(summary = "Listar produtos", description = "Filtra por lojaId (opcional). Inclui as variantes")
    @ApiResponse(responseCode = "200", description = "Página de produtos")
    @GetMapping
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public PageResponse<ProdutoLojaResponse> listar(@RequestParam(required = false) Long lojaId, Pageable pageable) {
        return PageResponse.of(service.listar(lojaId, pageable));
    }

    @Operation(summary = "Buscar produto por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public ProdutoLojaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar produto", description = "Nasce sem variantes — adicione via POST /produtos-loja/{id}/variantes")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<ProdutoLojaResponse> criar(@Valid @RequestBody ProdutoLojaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar dados do produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado"),
            @ApiResponse(responseCode = "404", description = "Produto ou categoria não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ProdutoLojaResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoLojaRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir produto", description = "Exclui em cascata as variantes")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluído"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Criar variante (SKU) do produto",
            description = "valoresAtributos precisa cobrir todo atributo obrigatório com geraVariacao=true da categoria do produto — ver Javadoc de VarianteProdutoLojaService")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Variante criada"),
            @ApiResponse(responseCode = "404", description = "Produto ou atributo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "SKU já utilizado por outra variante desta loja", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Atributo de outra categoria, atributo não gerador de variação, atributo obrigatório ausente, ou opção inválida pra atributo LISTA", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/variantes")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<VarianteProdutoLojaResponse> criarVariante(@PathVariable Long id, @Valid @RequestBody VarianteProdutoLojaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(varianteService.criar(id, request));
    }
}
