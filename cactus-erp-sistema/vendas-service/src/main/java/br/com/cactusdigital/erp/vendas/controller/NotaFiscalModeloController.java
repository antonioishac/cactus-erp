package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalModeloRequest;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalModeloResponse;
import br.com.cactusdigital.erp.vendas.service.NotaFiscalModeloService;
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

@Tag(name = "Modelo de Nota Fiscal", description = "Catálogo de modelo de documento fiscal (Ato Cotepe 46/10 — 55=NF-e, 65=NFC-e, 59=SAT/CF-e etc)")
@RestController
@RequestMapping("/api/v1/notas-fiscais-modelos")
@RequiredArgsConstructor
public class NotaFiscalModeloController {

    private final NotaFiscalModeloService service;

    @Operation(summary = "Listar modelos de nota fiscal")
    @ApiResponse(responseCode = "200", description = "Página de modelos")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public PageResponse<NotaFiscalModeloResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar modelo de nota fiscal por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modelo encontrado"),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public NotaFiscalModeloResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar modelo de nota fiscal")
    @ApiResponse(responseCode = "201", description = "Modelo criado")
    @PostMapping
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<NotaFiscalModeloResponse> criar(@Valid @RequestBody NotaFiscalModeloRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar modelo de nota fiscal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modelo atualizado"),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public NotaFiscalModeloResponse atualizar(@PathVariable Long id, @Valid @RequestBody NotaFiscalModeloRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir modelo de nota fiscal")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Modelo excluído"),
            @ApiResponse(responseCode = "404", description = "Modelo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
