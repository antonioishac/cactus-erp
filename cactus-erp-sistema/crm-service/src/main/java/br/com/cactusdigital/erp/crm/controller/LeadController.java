package br.com.cactusdigital.erp.crm.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.crm.dto.ConverterLeadRequest;
import br.com.cactusdigital.erp.crm.dto.DesqualificarLeadRequest;
import br.com.cactusdigital.erp.crm.dto.LeadRequest;
import br.com.cactusdigital.erp.crm.dto.LeadResponse;
import br.com.cactusdigital.erp.crm.service.LeadService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Leads", description = "NOVO → EM_QUALIFICACAO → QUALIFICADO/DESQUALIFICADO → CONVERTIDO")
@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService service;

    @Operation(summary = "Listar leads")
    @ApiResponse(responseCode = "200", description = "Página de leads")
    @GetMapping
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public PageResponse<LeadResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar lead por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lead encontrado"),
            @ApiResponse(responseCode = "404", description = "Lead não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public LeadResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar lead")
    @ApiResponse(responseCode = "201", description = "Lead criado")
    @PostMapping
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public ResponseEntity<LeadResponse> criar(@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar dados do lead")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lead atualizado"),
            @ApiResponse(responseCode = "404", description = "Lead não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public LeadResponse atualizar(@PathVariable Long id, @Valid @RequestBody LeadRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir lead")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lead excluído"),
            @ApiResponse(responseCode = "404", description = "Lead não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Qualificar lead", description = "NOVO/EM_QUALIFICACAO → QUALIFICADO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lead qualificado"),
            @ApiResponse(responseCode = "404", description = "Lead não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Lead não está NOVO/EM_QUALIFICACAO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/qualificar")
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public LeadResponse qualificar(@PathVariable Long id) {
        return service.qualificar(id);
    }

    @Operation(summary = "Desqualificar lead", description = "→ DESQUALIFICADO, com motivo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lead desqualificado"),
            @ApiResponse(responseCode = "404", description = "Lead não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Lead já desqualificado ou convertido", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/desqualificar")
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public LeadResponse desqualificar(@PathVariable Long id, @Valid @RequestBody DesqualificarLeadRequest request) {
        return service.desqualificar(id, request);
    }

    @Operation(summary = "Converter lead em cliente", description = "QUALIFICADO → CONVERTIDO, vinculando um clienteId JÁ cadastrado no cadastro-base-service (não cria o Cliente automaticamente)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lead convertido"),
            @ApiResponse(responseCode = "404", description = "Lead não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Lead já convertido", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Lead não está QUALIFICADO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/converter")
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public LeadResponse converter(@PathVariable Long id, @Valid @RequestBody ConverterLeadRequest request) {
        return service.converter(id, request);
    }
}
