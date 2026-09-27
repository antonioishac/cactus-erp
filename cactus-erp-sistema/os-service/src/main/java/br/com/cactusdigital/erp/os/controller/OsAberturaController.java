package br.com.cactusdigital.erp.os.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.os.dto.OsAberturaEquipamentoRequest;
import br.com.cactusdigital.erp.os.dto.OsAberturaEquipamentoResponse;
import br.com.cactusdigital.erp.os.dto.OsAberturaRequest;
import br.com.cactusdigital.erp.os.dto.OsAberturaResponse;
import br.com.cactusdigital.erp.os.dto.OsEvolucaoRequest;
import br.com.cactusdigital.erp.os.dto.OsEvolucaoResponse;
import br.com.cactusdigital.erp.os.dto.OsProdutoServicoRequest;
import br.com.cactusdigital.erp.os.dto.OsProdutoServicoResponse;
import br.com.cactusdigital.erp.os.service.OsAberturaService;
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

@Tag(name = "Ordem de Serviço", description = "Abertura, equipamentos atendidos, itens (peça/mão de obra) e evolução (linha do tempo) — status é um catálogo livre, não uma máquina de estados fixa")
@RestController
@RequestMapping("/api/v1/os-aberturas")
@RequiredArgsConstructor
public class OsAberturaController {

    private final OsAberturaService service;

    @Operation(summary = "Listar ordens de serviço", description = "Opcionalmente filtrado por cliente")
    @ApiResponse(responseCode = "200", description = "Página de ordens de serviço")
    @GetMapping
    @PreAuthorize("hasAuthority('OS_CONSULTAR')")
    public PageResponse<OsAberturaResponse> listar(@RequestParam(required = false) Long clienteId, Pageable pageable) {
        return PageResponse.of(service.listar(clienteId, pageable));
    }

    @Operation(summary = "Buscar ordem de serviço por id", description = "Inclui equipamentos, itens e evolução")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ordem de serviço encontrada"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OS_CONSULTAR')")
    public OsAberturaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Abrir ordem de serviço", description = "Nasce sem equipamentos/itens/evolução — adicione via sub-recursos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ordem de serviço aberta"),
            @ApiResponse(responseCode = "404", description = "Status de OS não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('OS_ABRIR')")
    public ResponseEntity<OsAberturaResponse> criar(@Valid @RequestBody OsAberturaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Trocar o status da OS", description = "Status é um catálogo livre — qualquer transição é permitida, sem máquina de estados fixa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status trocado"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço ou status não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/status/{osStatusId}")
    @PreAuthorize("hasAuthority('OS_ABRIR')")
    public OsAberturaResponse trocarStatus(@PathVariable Long id, @PathVariable Long osStatusId) {
        return service.trocarStatus(id, osStatusId);
    }

    @Operation(summary = "Aprovar orçamento da OS", description = "Melhoria sobre o fenix (que não rastreava isso) — marca orcamentoAprovado + dataAprovacaoOrcamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento aprovado"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento já estava aprovado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/aprovar-orcamento")
    @PreAuthorize("hasAuthority('OS_ABRIR')")
    public OsAberturaResponse aprovarOrcamento(@PathVariable Long id) {
        return service.aprovarOrcamento(id);
    }

    @Operation(summary = "Encerrar a OS", description = "Marca dataFim como hoje")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ordem de serviço encerrada"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/encerrar")
    @PreAuthorize("hasAuthority('OS_ABRIR')")
    public OsAberturaResponse encerrar(@PathVariable Long id) {
        return service.encerrar(id);
    }

    @Operation(summary = "Adicionar equipamento à OS")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipamento adicionado"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço ou tipo de equipamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/equipamentos")
    @PreAuthorize("hasAuthority('OS_ABRIR')")
    public ResponseEntity<OsAberturaEquipamentoResponse> adicionarEquipamento(@PathVariable Long id, @Valid @RequestBody OsAberturaEquipamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarEquipamento(id, request));
    }

    @Operation(summary = "Remover equipamento da OS")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Equipamento removido"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço ou equipamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/equipamentos/{vinculoId}")
    @PreAuthorize("hasAuthority('OS_ABRIR')")
    public ResponseEntity<Void> removerEquipamento(@PathVariable Long id, @PathVariable Long vinculoId) {
        service.removerEquipamento(id, vinculoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adicionar item (peça ou mão de obra) à OS", description = "produtoId obrigatório quando tipo=PRODUTO — recalcula o valorTotal da OS")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "produtoId ausente para item do tipo PRODUTO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/itens")
    @PreAuthorize("hasAuthority('OS_ABRIR')")
    public ResponseEntity<OsProdutoServicoResponse> adicionarItem(@PathVariable Long id, @Valid @RequestBody OsProdutoServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarItem(id, request));
    }

    @Operation(summary = "Remover item da OS")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removido"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço ou item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/itens/{itemId}")
    @PreAuthorize("hasAuthority('OS_ABRIR')")
    public ResponseEntity<Void> removerItem(@PathVariable Long id, @PathVariable Long itemId) {
        service.removerItem(id, itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adicionar registro de evolução (linha do tempo)", description = "Append-only — sem edição/remoção depois de criado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evolução registrada"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/evolucoes")
    @PreAuthorize("hasAuthority('OS_ABRIR')")
    public ResponseEntity<OsEvolucaoResponse> adicionarEvolucao(@PathVariable Long id, @Valid @RequestBody OsEvolucaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarEvolucao(id, request));
    }
}
