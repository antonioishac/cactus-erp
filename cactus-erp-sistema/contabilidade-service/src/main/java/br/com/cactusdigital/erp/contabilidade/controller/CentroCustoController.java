package br.com.cactusdigital.erp.contabilidade.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.contabilidade.dto.CentroCustoRequest;
import br.com.cactusdigital.erp.contabilidade.dto.CentroCustoResponse;
import br.com.cactusdigital.erp.contabilidade.service.CentroCustoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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

@Tag(name = "Centros de Custo", description = "Catálogo flat por empresa — dimensão opcional de PartidaContabil")
@RestController
@RequestMapping("/api/v1/centros-custo")
@RequiredArgsConstructor
public class CentroCustoController {

    private final CentroCustoService service;

    @Operation(summary = "Listar centros de custo (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de centros de custo")
    @GetMapping
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public PageResponse<CentroCustoResponse> listar(@RequestParam Long empresaId, Pageable pageable) {
        return PageResponse.of(service.listar(empresaId, pageable));
    }

    @Operation(summary = "Listar todos os centros de custo da empresa, sem paginação")
    @ApiResponse(responseCode = "200", description = "Todos os centros de custo, ordenados por código")
    @GetMapping("/todos")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public List<CentroCustoResponse> listarTodos(@RequestParam Long empresaId) {
        return service.listarTodos(empresaId);
    }

    @Operation(summary = "Buscar centro de custo por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Centro de custo encontrado"),
            @ApiResponse(responseCode = "404", description = "Centro de custo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public CentroCustoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar centro de custo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Centro de custo criado"),
            @ApiResponse(responseCode = "409", description = "Código já usado nesta empresa", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CONTABIL_GERENCIAR')")
    public ResponseEntity<CentroCustoResponse> criar(@Valid @RequestBody CentroCustoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar centro de custo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Centro de custo atualizado"),
            @ApiResponse(responseCode = "404", description = "Centro de custo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Código já usado nesta empresa", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTABIL_GERENCIAR')")
    public CentroCustoResponse atualizar(@PathVariable Long id, @Valid @RequestBody CentroCustoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir centro de custo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Centro de custo excluído"),
            @ApiResponse(responseCode = "404", description = "Centro de custo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTABIL_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
