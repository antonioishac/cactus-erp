package br.com.cactusdigital.erp.crm.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.crm.dto.InteracaoRequest;
import br.com.cactusdigital.erp.crm.dto.InteracaoResponse;
import br.com.cactusdigital.erp.crm.service.InteracaoService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Interações", description = "Histórico append-only (ligação feita, e-mail enviado, nota livre) vinculado a um Lead e/ou Oportunidade — sem atualizar/excluir")
@RestController
@RequestMapping("/api/v1/interacoes")
@RequiredArgsConstructor
public class InteracaoController {

    private final InteracaoService service;

    @Operation(summary = "Listar interações", description = "Filtra por leadId ou oportunidadeId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de interações")
    @GetMapping
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public PageResponse<InteracaoResponse> listar(
            @RequestParam(required = false) Long leadId,
            @RequestParam(required = false) Long oportunidadeId,
            Pageable pageable) {
        return PageResponse.of(service.listar(leadId, oportunidadeId, pageable));
    }

    @Operation(summary = "Registrar interação", description = "Pelo menos um de leadId/oportunidadeId é obrigatório")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Interação registrada"),
            @ApiResponse(responseCode = "404", description = "Lead ou oportunidade não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Nem leadId nem oportunidadeId informado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public ResponseEntity<InteracaoResponse> criar(@Valid @RequestBody InteracaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }
}
