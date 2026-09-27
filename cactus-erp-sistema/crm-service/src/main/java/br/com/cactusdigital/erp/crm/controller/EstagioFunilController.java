package br.com.cactusdigital.erp.crm.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.crm.dto.EstagioFunilRequest;
import br.com.cactusdigital.erp.crm.dto.EstagioFunilResponse;
import br.com.cactusdigital.erp.crm.service.EstagioFunilService;
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

@Tag(name = "Estágios do Funil", description = "Catálogo livre de estágios do funil de vendas (Kanban) — cada empresa/vertical desenha o próprio funil")
@RestController
@RequestMapping("/api/v1/estagios-funil")
@RequiredArgsConstructor
public class EstagioFunilController {

    private final EstagioFunilService service;

    @Operation(summary = "Listar estágios do funil (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de estágios")
    @GetMapping
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public PageResponse<EstagioFunilResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Listar todos os estágios ordenados", description = "Sem paginação, ordenado por 'ordem' — pronto pra montar as colunas de um Kanban")
    @ApiResponse(responseCode = "200", description = "Estágios ordenados")
    @GetMapping("/ordenado")
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public List<EstagioFunilResponse> listarOrdenado() {
        return service.listarOrdenado();
    }

    @Operation(summary = "Buscar estágio do funil por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estágio encontrado"),
            @ApiResponse(responseCode = "404", description = "Estágio não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public EstagioFunilResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar estágio do funil")
    @ApiResponse(responseCode = "201", description = "Estágio criado")
    @PostMapping
    @PreAuthorize("hasAuthority('CRM_GERENCIAR')")
    public ResponseEntity<EstagioFunilResponse> criar(@Valid @RequestBody EstagioFunilRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar estágio do funil")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estágio atualizado"),
            @ApiResponse(responseCode = "404", description = "Estágio não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_GERENCIAR')")
    public EstagioFunilResponse atualizar(@PathVariable Long id, @Valid @RequestBody EstagioFunilRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir estágio do funil")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estágio excluído"),
            @ApiResponse(responseCode = "404", description = "Estágio não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
