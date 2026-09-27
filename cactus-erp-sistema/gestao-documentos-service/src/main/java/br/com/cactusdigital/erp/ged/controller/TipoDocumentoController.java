package br.com.cactusdigital.erp.ged.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.ged.dto.TipoDocumentoRequest;
import br.com.cactusdigital.erp.ged.dto.TipoDocumentoResponse;
import br.com.cactusdigital.erp.ged.service.TipoDocumentoService;
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

@Tag(name = "Tipo de Documento", description = "Catálogo de tipos de documento do GED — tamanhoMaximoMb valida todo upload desse tipo")
@RestController
@RequestMapping("/api/v1/tipos-documento")
@RequiredArgsConstructor
public class TipoDocumentoController {

    private final TipoDocumentoService service;

    @Operation(summary = "Listar tipos de documento")
    @ApiResponse(responseCode = "200", description = "Página de tipos de documento")
    @GetMapping
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public PageResponse<TipoDocumentoResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar tipo de documento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de documento encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo de documento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public TipoDocumentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar tipo de documento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tipo de documento criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe tipo de documento com esse nome",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('GED_GERENCIAR')")
    public ResponseEntity<TipoDocumentoResponse> criar(@Valid @RequestBody TipoDocumentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar tipo de documento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tipo de documento atualizado"),
            @ApiResponse(responseCode = "404", description = "Tipo de documento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe tipo de documento com esse nome",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GED_GERENCIAR')")
    public TipoDocumentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody TipoDocumentoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir tipo de documento", description = "Só recomendado pra tipo nunca usado — considere inativar (situacao=I) em vez de excluir")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tipo de documento excluído"),
            @ApiResponse(responseCode = "404", description = "Tipo de documento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GED_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
