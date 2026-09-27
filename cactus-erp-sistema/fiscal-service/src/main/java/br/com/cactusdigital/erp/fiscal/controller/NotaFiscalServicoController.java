package br.com.cactusdigital.erp.fiscal.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.fiscal.dto.GerarNotaServicoRequest;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalServicoResponse;
import br.com.cactusdigital.erp.fiscal.service.NotaFiscalServicoService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Nota Fiscal de Serviço (NFS-e)", description = "RASCUNHO (composta a partir da OS) → (emitir) → ENVIADA/AUTORIZADA/REJEITADA/ERRO, via ACBr Monitor — confiança menor que NF-e, ver Javadoc de NfseIniBuilder")
@RestController
@RequestMapping("/api/v1/notas-servico")
@RequiredArgsConstructor
public class NotaFiscalServicoController {

    private final NotaFiscalServicoService service;

    @Operation(summary = "Listar notas de serviço")
    @ApiResponse(responseCode = "200", description = "Página de notas de serviço")
    @GetMapping
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public PageResponse<NotaFiscalServicoResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar nota de serviço por id", description = "Inclui os itens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota de serviço encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota de serviço não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public NotaFiscalServicoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Gerar nota de serviço a partir de uma OS",
            description = "A OS precisa ter orcamentoAprovado=true e pelo menos 1 item SERVICO (peças/PRODUTO ficam de fora, viram NF-e separada). listaServicoId é aplicado a todos os itens SERVICO da OS")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nota de serviço composta em RASCUNHO"),
            @ApiResponse(responseCode = "404", description = "OS, cliente, empresa ou item da lista de serviço não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Esta OS já tem nota de serviço gerada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Orçamento da OS não aprovado, ou sem item SERVICO", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha ao consultar os-service ou cadastro-base-service", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/de-os/{osAberturaId}")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public ResponseEntity<NotaFiscalServicoResponse> gerarDeOs(@PathVariable Long osAberturaId, @Valid @RequestBody GerarNotaServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarDeOs(osAberturaId, request));
    }

    @Operation(summary = "Emitir a nota de serviço via ACBr Monitor",
            description = "Adiciona o RPS ao lote (NFSe.AdicionarRPS) — envio/autorização do lote ainda não implementado, ver limitação no Javadoc de NotaFiscalServicoService")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorno do ACBr Monitor processado"),
            @ApiResponse(responseCode = "404", description = "Nota de serviço não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Nota de serviço não está RASCUNHO/ERRO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/emitir")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public NotaFiscalServicoResponse emitir(@PathVariable Long id) {
        return service.emitir(id);
    }
}
