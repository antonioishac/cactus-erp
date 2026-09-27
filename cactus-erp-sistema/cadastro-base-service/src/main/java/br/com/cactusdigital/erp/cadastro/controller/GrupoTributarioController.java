package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.GrupoTributarioRequest;
import br.com.cactusdigital.erp.cadastro.dto.GrupoTributarioResponse;
import br.com.cactusdigital.erp.cadastro.service.GrupoTributarioService;
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

@Tag(name = "Tributação — Grupo Tributário", description = "Perfil tributário do produto (origem da mercadoria + regras associadas)")
@RestController
@RequestMapping("/api/v1/tributacao/grupos-tributarios")
@RequiredArgsConstructor
public class GrupoTributarioController {

    private final GrupoTributarioService service;

    @Operation(summary = "Listar grupos tributários", description = "Busca paginada por nome, opcional")
    @ApiResponse(responseCode = "200", description = "Página de grupos tributários")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public PageResponse<GrupoTributarioResponse> listar(
            @Parameter(description = "Filtro por nome, contém, sem distinção de caixa") @RequestParam(required = false) String nome,
            Pageable pageable) {
        return PageResponse.of(service.listar(nome, pageable));
    }

    @Operation(summary = "Buscar grupo tributário por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo tributário encontrado"),
            @ApiResponse(responseCode = "404", description = "Grupo tributário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public GrupoTributarioResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar grupo tributário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Grupo tributário criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CRIAR')")
    public ResponseEntity<GrupoTributarioResponse> criar(@Valid @RequestBody GrupoTributarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar grupo tributário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo tributário atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Grupo tributário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EDITAR')")
    public GrupoTributarioResponse atualizar(@PathVariable Long id, @Valid @RequestBody GrupoTributarioRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir grupo tributário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Grupo tributário excluído"),
            @ApiResponse(responseCode = "404", description = "Grupo tributário não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
