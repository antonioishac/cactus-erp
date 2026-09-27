package br.com.cactusdigital.erp.seguranca.controller;

import br.com.cactusdigital.erp.seguranca.dto.AtribuirFuncionalidadesRequest;
import br.com.cactusdigital.erp.seguranca.dto.PapelRequest;
import br.com.cactusdigital.erp.seguranca.dto.PapelResponse;
import br.com.cactusdigital.erp.seguranca.service.PapelService;
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

@Tag(name = "Segurança — Papel", description = "Agrupador de funcionalidades (ex: ADMIN, GERENTE) — N:N com Usuario via UsuarioPapelEmpresa")
@RestController
@RequestMapping("/api/v1/papeis")
@RequiredArgsConstructor
public class PapelController {

    private final PapelService papelService;

    @Operation(summary = "Listar papéis", description = "Busca paginada por nome, opcional")
    @ApiResponse(responseCode = "200", description = "Página de papéis")
    @GetMapping
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public PageResponse<PapelResponse> listar(@RequestParam(required = false) String nome, Pageable pageable) {
        return PageResponse.of(papelService.listar(nome, pageable));
    }

    @Operation(summary = "Buscar papel por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Papel encontrado"),
            @ApiResponse(responseCode = "404", description = "Papel não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public PapelResponse buscarPorId(@PathVariable Long id) {
        return papelService.buscarPorId(id);
    }

    @Operation(summary = "Criar papel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Papel criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public ResponseEntity<PapelResponse> criar(@Valid @RequestBody PapelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(papelService.criar(request));
    }

    @Operation(summary = "Atualizar papel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Papel atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Papel não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public PapelResponse atualizar(@PathVariable Long id, @Valid @RequestBody PapelRequest request) {
        return papelService.atualizar(id, request);
    }

    @Operation(summary = "Atribuir funcionalidades ao papel", description = "Substitui o conjunto inteiro de funcionalidades do papel")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funcionalidades atribuídas"),
            @ApiResponse(responseCode = "404", description = "Papel ou alguma funcionalidade não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}/funcionalidades")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public PapelResponse atribuirFuncionalidades(@PathVariable Long id, @Valid @RequestBody AtribuirFuncionalidadesRequest request) {
        return papelService.atribuirFuncionalidades(id, request);
    }

    @Operation(summary = "Excluir papel")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Papel excluído"),
            @ApiResponse(responseCode = "404", description = "Papel não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        papelService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
