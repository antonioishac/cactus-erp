package br.com.cactusdigital.erp.fiscal.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.fiscal.dto.ListaServicoMunicipalRequest;
import br.com.cactusdigital.erp.fiscal.dto.ListaServicoMunicipalResponse;
import br.com.cactusdigital.erp.fiscal.service.ListaServicoMunicipalService;
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

@Tag(name = "Lista de Serviço Municipal", description = "Catálogo de item da lista de serviço municipal (ISS) — a empresa cadastra os itens que usa nos municípios onde opera")
@RestController
@RequestMapping("/api/v1/listas-servico-municipal")
@RequiredArgsConstructor
public class ListaServicoMunicipalController {

    private final ListaServicoMunicipalService service;

    @Operation(summary = "Listar itens da lista de serviço municipal")
    @ApiResponse(responseCode = "200", description = "Página de itens")
    @GetMapping
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public PageResponse<ListaServicoMunicipalResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar item da lista de serviço por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public ListaServicoMunicipalResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar item da lista de serviço municipal")
    @ApiResponse(responseCode = "201", description = "Item criado")
    @PostMapping
    @PreAuthorize("hasAuthority('FISCAL_GERENCIAR')")
    public ResponseEntity<ListaServicoMunicipalResponse> criar(@Valid @RequestBody ListaServicoMunicipalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar item da lista de serviço municipal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item atualizado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FISCAL_GERENCIAR')")
    public ListaServicoMunicipalResponse atualizar(@PathVariable Long id, @Valid @RequestBody ListaServicoMunicipalRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir item da lista de serviço municipal")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item excluído"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FISCAL_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
