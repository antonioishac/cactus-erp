package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ContadorRequest;
import br.com.cactusdigital.erp.cadastro.dto.ContadorResponse;
import br.com.cactusdigital.erp.cadastro.service.ContadorService;
import org.springframework.http.ProblemDetail;
import br.com.cactusdigital.erp.common.rest.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Empresa — Contador", description = "Contador responsável (troca de arquivos com a contabilidade) — N:1 com Empresa")
@RestController
@RequestMapping("/api/v1/contadores")
@RequiredArgsConstructor
public class ContadorController {

    private final ContadorService service;

    @Operation(summary = "Listar contadores", description = "Busca paginada por nome, opcional")
    @ApiResponse(responseCode = "200", description = "Página de contadores")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public PageResponse<ContadorResponse> listar(
            @Parameter(description = "Filtro por nome, contém, sem distinção de caixa") @RequestParam(required = false) String nome,
            Pageable pageable) {
        return PageResponse.of(service.listar(nome, pageable));
    }

    @Operation(summary = "Buscar contador por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contador encontrado"),
            @ApiResponse(responseCode = "404", description = "Contador não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public ContadorResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar contador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contador criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CRIAR')")
    public ResponseEntity<ContadorResponse> criar(@Valid @RequestBody ContadorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar contador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contador atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Contador não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public ContadorResponse atualizar(@PathVariable Long id, @Valid @RequestBody ContadorRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir contador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contador excluído"),
            @ApiResponse(responseCode = "404", description = "Contador não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
