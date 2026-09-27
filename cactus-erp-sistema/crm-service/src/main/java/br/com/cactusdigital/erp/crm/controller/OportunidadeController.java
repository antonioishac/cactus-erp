package br.com.cactusdigital.erp.crm.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.crm.dto.FunilEstagioResponse;
import br.com.cactusdigital.erp.crm.dto.MoverEstagioRequest;
import br.com.cactusdigital.erp.crm.dto.OportunidadeRequest;
import br.com.cactusdigital.erp.crm.dto.OportunidadeResponse;
import br.com.cactusdigital.erp.crm.service.OportunidadeService;
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

@Tag(name = "Oportunidades", description = "Funil de vendas — ABERTA até ser movida pra um estágio de encerramento (GANHA/PERDIDA)")
@RestController
@RequestMapping("/api/v1/oportunidades")
@RequiredArgsConstructor
public class OportunidadeController {

    private final OportunidadeService service;

    @Operation(summary = "Listar oportunidades")
    @ApiResponse(responseCode = "200", description = "Página de oportunidades")
    @GetMapping
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public PageResponse<OportunidadeResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Visão de funil (Kanban)", description = "Colunas na ordem do funil, cada uma com as oportunidades ABERTAS nela e o valor estimado somado")
    @ApiResponse(responseCode = "200", description = "Funil montado")
    @GetMapping("/funil")
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public List<FunilEstagioResponse> visaoFunil() {
        return service.visaoFunil();
    }

    @Operation(summary = "Buscar oportunidade por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oportunidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Oportunidade não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public OportunidadeResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar oportunidade", description = "leadId é opcional — pode nascer direto de um clienteId já existente, sem passar por Lead")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Oportunidade criada"),
            @ApiResponse(responseCode = "404", description = "Estágio do funil ou lead não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public ResponseEntity<OportunidadeResponse> criar(@Valid @RequestBody OportunidadeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar dados da oportunidade", description = "Não move de estágio — use POST /{id}/mover-estagio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oportunidade atualizada"),
            @ApiResponse(responseCode = "404", description = "Oportunidade ou lead não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public OportunidadeResponse atualizar(@PathVariable Long id, @Valid @RequestBody OportunidadeRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir oportunidade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Oportunidade excluída"),
            @ApiResponse(responseCode = "404", description = "Oportunidade não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CRM_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mover oportunidade de estágio",
            description = "Se o estágio destino tem tipoEncerramento=GANHO/PERDIDO, fecha a oportunidade automaticamente (mesmo efeito de arrastar um card pra coluna de fechamento num Kanban). PERDIDO exige motivoPerda")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oportunidade movida (e fechada, se aplicável)"),
            @ApiResponse(responseCode = "404", description = "Oportunidade ou estágio não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Oportunidade já está fechada (GANHA/PERDIDA)", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "motivoPerda obrigatório ao mover pra estágio PERDIDO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/mover-estagio")
    @PreAuthorize("hasAuthority('CRM_PROSPECTAR')")
    public OportunidadeResponse moverEstagio(@PathVariable Long id, @Valid @RequestBody MoverEstagioRequest request) {
        return service.moverEstagio(id, request);
    }
}
