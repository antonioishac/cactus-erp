package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaCondicoesPagamentoRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaCondicoesPagamentoResponse;
import br.com.cactusdigital.erp.vendas.service.VendaCondicoesPagamentoService;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Condição de Pagamento", description = "Condição de pagamento (ex: BOLETO 30/60/90) e suas parcelas")
@RestController
@RequestMapping("/api/v1/condicoes-pagamento")
@RequiredArgsConstructor
public class VendaCondicoesPagamentoController {

    private final VendaCondicoesPagamentoService service;

    @Operation(summary = "Listar condições de pagamento")
    @ApiResponse(responseCode = "200", description = "Página de condições de pagamento")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public PageResponse<VendaCondicoesPagamentoResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar condição de pagamento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Condição encontrada"),
            @ApiResponse(responseCode = "404", description = "Condição não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public VendaCondicoesPagamentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar condição de pagamento", description = "parcelas é a lista completa (substituída inteira em atualizações futuras)")
    @ApiResponse(responseCode = "201", description = "Condição criada")
    @PostMapping
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<VendaCondicoesPagamentoResponse> criar(@Valid @RequestBody VendaCondicoesPagamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar condição de pagamento", description = "parcelas substitui a lista inteira")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Condição atualizada"),
            @ApiResponse(responseCode = "404", description = "Condição não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public VendaCondicoesPagamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody VendaCondicoesPagamentoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir condição de pagamento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Condição excluída"),
            @ApiResponse(responseCode = "404", description = "Condição não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
