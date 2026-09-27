package br.com.cactusdigital.erp.ged.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.ged.dto.PastaDocumentoRequest;
import br.com.cactusdigital.erp.ged.dto.PastaDocumentoResponse;
import br.com.cactusdigital.erp.ged.service.PastaDocumentoService;
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

@Tag(name = "Pasta de Documento", description = "Hierarquia de pastas do GED — enriquecimento sobre o fenix, que só tinha agrupador flat")
@RestController
@RequestMapping("/api/v1/pastas")
@RequiredArgsConstructor
public class PastaDocumentoController {

    private final PastaDocumentoService service;

    @Operation(summary = "Listar pastas")
    @ApiResponse(responseCode = "200", description = "Página de pastas")
    @GetMapping
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public PageResponse<PastaDocumentoResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar pasta por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pasta encontrada"),
            @ApiResponse(responseCode = "404", description = "Pasta não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public PastaDocumentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar pasta", description = "pastaPaiId opcional — omitido cria uma pasta raiz")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pasta criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Pasta pai não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('GED_GERENCIAR')")
    public ResponseEntity<PastaDocumentoResponse> criar(@Valid @RequestBody PastaDocumentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar pasta", description = "Rejeita trocar a pasta pai por um descendente da própria pasta (ciclo)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pasta atualizada"),
            @ApiResponse(responseCode = "404", description = "Pasta ou pasta pai não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Pasta pai geraria um ciclo",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GED_GERENCIAR')")
    public PastaDocumentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PastaDocumentoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir pasta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pasta excluída"),
            @ApiResponse(responseCode = "404", description = "Pasta não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GED_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
