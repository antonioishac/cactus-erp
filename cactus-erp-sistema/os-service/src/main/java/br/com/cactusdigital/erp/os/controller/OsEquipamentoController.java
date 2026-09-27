package br.com.cactusdigital.erp.os.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.os.dto.OsEquipamentoRequest;
import br.com.cactusdigital.erp.os.dto.OsEquipamentoResponse;
import br.com.cactusdigital.erp.os.service.OsEquipamentoService;
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

@Tag(name = "Tipo de Equipamento", description = "Catálogo de tipo de equipamento atendido (ex: Notebook, Motor de partida, Ar-condicionado)")
@RestController
@RequestMapping("/api/v1/os-equipamentos")
@RequiredArgsConstructor
public class OsEquipamentoController {

    private final OsEquipamentoService service;

    @Operation(summary = "Listar tipos de equipamento")
    @ApiResponse(responseCode = "200", description = "Página de tipos de equipamento")
    @GetMapping
    @PreAuthorize("hasAuthority('OS_CONSULTAR')")
    public PageResponse<OsEquipamentoResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar tipo de equipamento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de equipamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo de equipamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OS_CONSULTAR')")
    public OsEquipamentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar tipo de equipamento")
    @ApiResponse(responseCode = "201", description = "Tipo de equipamento criado")
    @PostMapping
    @PreAuthorize("hasAuthority('OS_GERENCIAR')")
    public ResponseEntity<OsEquipamentoResponse> criar(@Valid @RequestBody OsEquipamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar tipo de equipamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de equipamento atualizado"),
            @ApiResponse(responseCode = "404", description = "Tipo de equipamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OS_GERENCIAR')")
    public OsEquipamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody OsEquipamentoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir tipo de equipamento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo de equipamento excluído"),
            @ApiResponse(responseCode = "404", description = "Tipo de equipamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OS_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
