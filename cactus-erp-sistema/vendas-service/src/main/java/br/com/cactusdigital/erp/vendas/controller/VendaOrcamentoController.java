package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.GerarVendaDeOrcamentoRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoItemRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoItemResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoRequest;
import br.com.cactusdigital.erp.vendas.dto.VendaOrcamentoResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaResponse;
import br.com.cactusdigital.erp.vendas.service.VendaOrcamentoService;
import br.com.cactusdigital.erp.vendas.service.VendaService;
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

@Tag(name = "Orçamento de Venda", description = "RASCUNHO → (enviar) → ENVIADO → (aprovar) → APROVADO → (gerar-venda) → CONVERTIDO; CANCELADO/EXPIRADO alcançáveis antes de CONVERTIDO")
@RestController
@RequestMapping("/api/v1/orcamentos")
@RequiredArgsConstructor
public class VendaOrcamentoController {

    private final VendaOrcamentoService service;
    private final VendaService vendaService;

    @Operation(summary = "Listar orçamentos", description = "Opcionalmente filtrado por cliente")
    @ApiResponse(responseCode = "200", description = "Página de orçamentos")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public PageResponse<VendaOrcamentoResponse> listar(@RequestParam(required = false) Long clienteId, Pageable pageable) {
        return PageResponse.of(service.listar(clienteId, pageable));
    }

    @Operation(summary = "Buscar orçamento por id", description = "Inclui os itens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public VendaOrcamentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar orçamento de venda", description = "Nasce em RASCUNHO, sem itens — adicione via POST /orcamentos/{id}/itens")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orçamento criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Condição de pagamento ou vendedor não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('VENDAS_ORCAR')")
    public ResponseEntity<VendaOrcamentoResponse> criar(@Valid @RequestBody VendaOrcamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Adicionar item ao orçamento", description = "Só permitido em RASCUNHO — recalcula os totais")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está em RASCUNHO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/itens")
    @PreAuthorize("hasAuthority('VENDAS_ORCAR')")
    public ResponseEntity<VendaOrcamentoItemResponse> adicionarItem(@PathVariable Long id, @Valid @RequestBody VendaOrcamentoItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarItem(id, request));
    }

    @Operation(summary = "Remover item do orçamento", description = "Só permitido em RASCUNHO")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item removido"),
            @ApiResponse(responseCode = "404", description = "Orçamento ou item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está em RASCUNHO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/itens/{itemId}")
    @PreAuthorize("hasAuthority('VENDAS_ORCAR')")
    public ResponseEntity<Void> removerItem(@PathVariable Long id, @PathVariable Long itemId) {
        service.removerItem(id, itemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enviar orçamento ao cliente", description = "RASCUNHO → ENVIADO, exige ao menos 1 item")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento enviado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está em RASCUNHO", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Orçamento sem itens", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/enviar")
    @PreAuthorize("hasAuthority('VENDAS_ORCAR')")
    public VendaOrcamentoResponse enviar(@PathVariable Long id) {
        return service.enviar(id);
    }

    @Operation(summary = "Aprovar orçamento", description = "ENVIADO → APROVADO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento aprovado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está ENVIADO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasAuthority('VENDAS_ORCAR')")
    public VendaOrcamentoResponse aprovar(@PathVariable Long id) {
        return service.aprovar(id);
    }

    @Operation(summary = "Marcar orçamento como expirado", description = "ENVIADO/APROVADO → EXPIRADO (validade vencida)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento marcado como expirado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está ENVIADO/APROVADO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/expirar")
    @PreAuthorize("hasAuthority('VENDAS_ORCAR')")
    public VendaOrcamentoResponse expirar(@PathVariable Long id) {
        return service.expirar(id);
    }

    @Operation(summary = "Cancelar orçamento", description = "Não permitido depois de CONVERTIDO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orçamento cancelado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento já convertido ou já cancelado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('VENDAS_ORCAR')")
    public VendaOrcamentoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @Operation(summary = "Gerar venda a partir do orçamento", description = "Só permitido com o orçamento APROVADO — copia os itens pra uma venda nova em RASCUNHO e marca o orçamento como CONVERTIDO")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venda gerada"),
            @ApiResponse(responseCode = "404", description = "Orçamento ou tipo de nota fiscal não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Orçamento não está APROVADO", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Orçamento sem itens", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/gerar-venda")
    @PreAuthorize("hasAuthority('VENDAS_VENDER')")
    public ResponseEntity<VendaResponse> gerarVenda(@PathVariable Long id, @Valid @RequestBody GerarVendaDeOrcamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.gerarAPartirDeOrcamento(id, request));
    }
}
