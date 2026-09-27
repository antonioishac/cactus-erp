package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.CestRequest;
import br.com.cactusdigital.erp.cadastro.dto.CestResponse;
import br.com.cactusdigital.erp.cadastro.service.CestService;
import org.springframework.http.ProblemDetail;
import br.com.cactusdigital.erp.common.rest.PageResponse;
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

@Tag(name = "Tributação — CEST", description = "Código Especificador da Substituição Tributária (catálogo, amostra representativa)")
@RestController
@RequestMapping("/api/v1/cest")
@RequiredArgsConstructor
public class CestController {

    private final CestService service;

    @Operation(summary = "Listar CEST", description = "Busca paginada por descrição, opcional")
    @ApiResponse(responseCode = "200", description = "Página de CEST")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public PageResponse<CestResponse> listar(@RequestParam(required = false) String descricao, Pageable pageable) {
        return PageResponse.of(service.listar(descricao, pageable));
    }

    @Operation(summary = "Buscar CEST por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CEST encontrado"),
            @ApiResponse(responseCode = "404", description = "CEST não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public CestResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar CEST")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CEST criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "NCM informado não existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CRIAR')")
    public ResponseEntity<CestResponse> criar(@Valid @RequestBody CestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar CEST")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CEST atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "CEST ou NCM não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EDITAR')")
    public CestResponse atualizar(@PathVariable Long id, @Valid @RequestBody CestRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir CEST")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "CEST excluído"),
            @ApiResponse(responseCode = "404", description = "CEST não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
