package br.com.cactusdigital.erp.crm.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.crm.dto.AtividadeRequest;
import br.com.cactusdigital.erp.crm.dto.AtividadeResponse;
import br.com.cactusdigital.erp.crm.service.AtividadeService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Atividades", description = "Tarefas agendadas (ligar/reunir/visitar) vinculadas a um Lead e/ou Oportunidade")
@RestController
@RequestMapping("/api/v1/atividades")
@RequiredArgsConstructor
public class AtividadeController {

    private final AtividadeService service;

    @Operation(summary = "Listar atividades", description = "Filtra por leadId ou oportunidadeId (opcional, mutuamente relevantes)")
    @ApiResponse(responseCode = "200", description = "Página de atividades")
    @GetMapping
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public PageResponse<AtividadeResponse> listar(
            @RequestParam(required = false) Long leadId,
            @RequestParam(required = false) Long oportunidadeId,
            Pageable pageable) {
        return PageResponse.of(service.listar(leadId, oportunidadeId, pageable));
    }

    @Operation(summary = "Buscar atividade por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atividade encontrada"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public AtividadeResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar atividade", description = "Pelo menos um de leadId/oportunidadeId é obrigatório")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Atividade criada"),
            @ApiResponse(responseCode = "404", description = "Lead ou oportunidade não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Nem leadId nem oportunidadeId informado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public ResponseEntity<AtividadeResponse> criar(@Valid @RequestBody AtividadeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Concluir atividade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atividade concluída"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Atividade já concluída", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/concluir")
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public AtividadeResponse concluir(@PathVariable Long id) {
        return service.concluir(id);
    }

    @Operation(summary = "Excluir atividade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Atividade excluída"),
            @ApiResponse(responseCode = "404", description = "Atividade não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
