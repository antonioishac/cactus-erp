package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.CfopRequest;
import br.com.cactusdigital.erp.cadastro.dto.CfopResponse;
import br.com.cactusdigital.erp.cadastro.service.CfopService;
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

@Tag(name = "Tributação — CFOP", description = "Código Fiscal de Operações e Prestações (catálogo, amostra representativa)")
@RestController
@RequestMapping("/api/v1/cfop")
@RequiredArgsConstructor
public class CfopController {

    private final CfopService service;

    @Operation(summary = "Listar CFOP", description = "Busca paginada por descrição, opcional")
    @ApiResponse(responseCode = "200", description = "Página de CFOP")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public PageResponse<CfopResponse> listar(@RequestParam(required = false) String descricao, Pageable pageable) {
        return PageResponse.of(service.listar(descricao, pageable));
    }

    @Operation(summary = "Buscar CFOP por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CFOP encontrado"),
            @ApiResponse(responseCode = "404", description = "CFOP não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public CfopResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar CFOP")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CFOP criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CRIAR')")
    public ResponseEntity<CfopResponse> criar(@Valid @RequestBody CfopRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar CFOP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CFOP atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "CFOP não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EDITAR')")
    public CfopResponse atualizar(@PathVariable Long id, @Valid @RequestBody CfopRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir CFOP")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "CFOP excluído"),
            @ApiResponse(responseCode = "404", description = "CFOP não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
