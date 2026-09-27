package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.ComissaoObjetivoRequest;
import br.com.cactusdigital.erp.vendas.dto.ComissaoObjetivoResponse;
import br.com.cactusdigital.erp.vendas.dto.ComissaoPerfilRequest;
import br.com.cactusdigital.erp.vendas.dto.ComissaoPerfilResponse;
import br.com.cactusdigital.erp.vendas.service.ComissaoObjetivoService;
import br.com.cactusdigital.erp.vendas.service.ComissaoPerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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

@Tag(name = "Perfil de Comissão", description = "Perfil de comissão (ex: Vendedor Balcão, Representante Externo) e suas metas de comissão por produto (objetivos)")
@RestController
@RequestMapping("/api/v1/comissao-perfis")
@RequiredArgsConstructor
public class ComissaoPerfilController {

    private final ComissaoPerfilService service;
    private final ComissaoObjetivoService objetivoService;

    @Operation(summary = "Listar perfis de comissão")
    @ApiResponse(responseCode = "200", description = "Página de perfis de comissão")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public PageResponse<ComissaoPerfilResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar perfil de comissão por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public ComissaoPerfilResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar perfil de comissão")
    @ApiResponse(responseCode = "201", description = "Perfil criado")
    @PostMapping
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<ComissaoPerfilResponse> criar(@Valid @RequestBody ComissaoPerfilRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar perfil de comissão")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ComissaoPerfilResponse atualizar(@PathVariable Long id, @Valid @RequestBody ComissaoPerfilRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir perfil de comissão")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil excluído"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar objetivos de comissão do perfil", description = "Meta de comissão por produto — sem paginação própria, lista sempre pequena")
    @ApiResponse(responseCode = "200", description = "Lista de objetivos")
    @GetMapping("/{id}/objetivos")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public List<ComissaoObjetivoResponse> listarObjetivos(@PathVariable Long id) {
        return objetivoService.listarPorPerfil(id);
    }

    @Operation(summary = "Adicionar objetivo de comissão ao perfil")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Objetivo adicionado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/objetivos")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<ComissaoObjetivoResponse> adicionarObjetivo(@PathVariable Long id, @Valid @RequestBody ComissaoObjetivoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(objetivoService.adicionar(id, request));
    }

    @Operation(summary = "Remover objetivo de comissão do perfil")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Objetivo removido"),
            @ApiResponse(responseCode = "404", description = "Perfil ou objetivo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/objetivos/{objetivoId}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<Void> removerObjetivo(@PathVariable Long id, @PathVariable Long objetivoId) {
        objetivoService.remover(id, objetivoId);
        return ResponseEntity.noContent().build();
    }
}
