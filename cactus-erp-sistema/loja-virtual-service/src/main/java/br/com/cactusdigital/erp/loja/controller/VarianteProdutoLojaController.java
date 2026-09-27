package br.com.cactusdigital.erp.loja.controller;

import br.com.cactusdigital.erp.loja.dto.VarianteProdutoLojaRequest;
import br.com.cactusdigital.erp.loja.dto.VarianteProdutoLojaResponse;
import br.com.cactusdigital.erp.loja.service.VarianteProdutoLojaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Variantes de Produto", description = "O SKU vendável de fato — preço/estoque/GTIN próprios. Criação é sub-recurso de Produto (POST /produtos-loja/{id}/variantes); aqui só consulta/atualização/exclusão por id próprio")
@RestController
@RequestMapping("/api/v1/variantes-produto-loja")
@RequiredArgsConstructor
public class VarianteProdutoLojaController {

    private final VarianteProdutoLojaService service;

    @Operation(summary = "Buscar variante por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Variante encontrada"),
            @ApiResponse(responseCode = "404", description = "Variante não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public VarianteProdutoLojaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Atualizar variante", description = "Só preço/estoque/peso/situação — a composição de atributos (valoresAtributos) não muda depois de criada, ver Javadoc de VarianteProdutoLojaService")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Variante atualizada"),
            @ApiResponse(responseCode = "404", description = "Variante não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "SKU já utilizado por outra variante desta loja", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public VarianteProdutoLojaResponse atualizar(@PathVariable Long id, @Valid @RequestBody VarianteProdutoLojaRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir variante")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Variante excluída"),
            @ApiResponse(responseCode = "404", description = "Variante não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
