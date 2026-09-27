package br.com.cactusdigital.erp.os.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.os.dto.OsStatusRequest;
import br.com.cactusdigital.erp.os.dto.OsStatusResponse;
import br.com.cactusdigital.erp.os.service.OsStatusService;
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

@Tag(name = "Status de OS", description = "Catálogo livre de status de Ordem de Serviço (ex: Aberta, Em Orçamento, Aguardando Peça, Finalizada)")
@RestController
@RequestMapping("/api/v1/os-status")
@RequiredArgsConstructor
public class OsStatusController {

    private final OsStatusService service;

    @Operation(summary = "Listar status de OS")
    @ApiResponse(responseCode = "200", description = "Página de status")
    @GetMapping
    @PreAuthorize("hasAuthority('OS_CONSULTAR')")
    public PageResponse<OsStatusResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar status de OS por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status encontrado"),
            @ApiResponse(responseCode = "404", description = "Status não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OS_CONSULTAR')")
    public OsStatusResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar status de OS")
    @ApiResponse(responseCode = "201", description = "Status criado")
    @PostMapping
    @PreAuthorize("hasAuthority('OS_GERENCIAR')")
    public ResponseEntity<OsStatusResponse> criar(@Valid @RequestBody OsStatusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar status de OS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado"),
            @ApiResponse(responseCode = "404", description = "Status não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OS_GERENCIAR')")
    public OsStatusResponse atualizar(@PathVariable Long id, @Valid @RequestBody OsStatusRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir status de OS")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Status excluído"),
            @ApiResponse(responseCode = "404", description = "Status não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OS_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
