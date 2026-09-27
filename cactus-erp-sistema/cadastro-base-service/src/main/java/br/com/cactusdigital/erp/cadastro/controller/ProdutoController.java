package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ProdutoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoResponse;
import br.com.cactusdigital.erp.cadastro.dto.RecalcularTributacaoPadraoRequest;
import br.com.cactusdigital.erp.cadastro.service.ProdutoService;
import br.com.cactusdigital.erp.common.domain.Situacao;
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
import java.time.Instant;
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

@Tag(name = "Produto", description = "Cadastro de produto — identidade comercial, fiscal (NCM/CEST/grupo tributário) e preço")
@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @Operation(summary = "Listar produtos", description = "Busca paginada com filtros por nome, código interno, GTIN, subgrupo, marca, situação e cursor de alteração (desde)")
    @ApiResponse(responseCode = "200", description = "Página de produtos")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public PageResponse<ProdutoResponse> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String codigoInterno,
            @RequestParam(required = false) String gtin,
            @RequestParam(required = false) Long produtoSubgrupoId,
            @RequestParam(required = false) Long produtoMarcaId,
            @RequestParam(required = false) Situacao situacao,
            @Parameter(description = "Cursor de sincronização — só produtos alterados depois desse instante (ISO-8601)")
            @RequestParam(required = false) Instant desde,
            Pageable pageable) {
        return PageResponse.of(produtoService.listar(nome, codigoInterno, gtin, produtoSubgrupoId, produtoMarcaId, situacao, desde, pageable));
    }

    @Operation(summary = "Buscar produto por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public ProdutoResponse buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    @Operation(summary = "Criar produto", description = "ncmId e grupoTributarioId são obrigatórios; cestId só quando sujeito a substituição tributária")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Alguma referência (unidade/marca/subgrupo/NCM/CEST/grupo tributário/tipo) não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CRIAR')")
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(request));
    }

    @Operation(summary = "Atualizar produto", description = "faixasPreco e imagens substituem as listas inteiras")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Produto ou alguma referência não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EDITAR')")
    public ProdutoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        return produtoService.atualizar(id, request);
    }

    @Operation(summary = "Excluir produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto excluído"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        produtoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Recalcular tributação padrão",
            description = "Resolve grupo tributário do produto x operação fiscal x UF de destino e cacheia o CST/CSOSN/alíquotas nos campos *Padrao, prontos pra sync com o cactus-pdv")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tributação padrão recalculada"),
            @ApiResponse(responseCode = "404", description = "Produto, operação fiscal ou configuração tributária não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Não há regra de ICMS configurada para a UF informada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/recalcular-tributacao-padrao")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EDITAR')")
    public ProdutoResponse recalcularTributacaoPadrao(
            @PathVariable Long id, @Valid @RequestBody RecalcularTributacaoPadraoRequest request) {
        return produtoService.recalcularTributacaoPadrao(id, request);
    }
}
