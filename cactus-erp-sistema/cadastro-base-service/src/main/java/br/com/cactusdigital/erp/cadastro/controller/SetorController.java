package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.SetorRequest;
import br.com.cactusdigital.erp.cadastro.dto.SetorResponse;
import br.com.cactusdigital.erp.cadastro.service.SetorService;
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

@Tag(name = "Colaborador — Setor", description = "Departamento/setor do colaborador")
@RestController
@RequestMapping("/api/v1/setores")
@RequiredArgsConstructor
public class SetorController {

    private final SetorService service;

    @Operation(summary = "Listar setores", description = "Busca paginada por nome, opcional")
    @ApiResponse(responseCode = "200", description = "Página de setores")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_CONSULTAR')")
    public PageResponse<SetorResponse> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return PageResponse.of(service.listar(nome, pageable));
    }

    @Operation(summary = "Buscar setor por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Setor encontrado"),
            @ApiResponse(responseCode = "404", description = "Setor não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_CONSULTAR')")
    public SetorResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar setor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Setor criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_CRIAR')")
    public ResponseEntity<SetorResponse> criar(@Valid @RequestBody SetorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar setor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Setor atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Setor não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_EDITAR')")
    public SetorResponse atualizar(@PathVariable Long id, @Valid @RequestBody SetorRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir setor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Setor excluído"),
            @ApiResponse(responseCode = "404", description = "Setor não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
