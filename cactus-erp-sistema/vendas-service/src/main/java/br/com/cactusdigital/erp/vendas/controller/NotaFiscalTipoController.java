package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalTipoRequest;
import br.com.cactusdigital.erp.vendas.dto.NotaFiscalTipoResponse;
import br.com.cactusdigital.erp.vendas.service.NotaFiscalTipoService;
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

@Tag(name = "Tipo de Nota Fiscal", description = "Tipo de NF usado pela empresa (série/numeração de referência) — emissão real fica pro fiscal-service, Fase 8")
@RestController
@RequestMapping("/api/v1/notas-fiscais-tipos")
@RequiredArgsConstructor
public class NotaFiscalTipoController {

    private final NotaFiscalTipoService service;

    @Operation(summary = "Listar tipos de nota fiscal")
    @ApiResponse(responseCode = "200", description = "Página de tipos")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public PageResponse<NotaFiscalTipoResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar tipo de nota fiscal por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public NotaFiscalTipoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar tipo de nota fiscal")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo criado"),
            @ApiResponse(responseCode = "404", description = "Modelo de nota fiscal não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<NotaFiscalTipoResponse> criar(@Valid @RequestBody NotaFiscalTipoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar tipo de nota fiscal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo atualizado"),
            @ApiResponse(responseCode = "404", description = "Tipo ou modelo de nota fiscal não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public NotaFiscalTipoResponse atualizar(@PathVariable Long id, @Valid @RequestBody NotaFiscalTipoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir tipo de nota fiscal")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo excluído"),
            @ApiResponse(responseCode = "404", description = "Tipo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
