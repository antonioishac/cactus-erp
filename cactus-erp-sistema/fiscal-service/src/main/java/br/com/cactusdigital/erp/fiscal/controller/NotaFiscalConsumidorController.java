package br.com.cactusdigital.erp.fiscal.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalConsumidorResponse;
import br.com.cactusdigital.erp.fiscal.service.NotaFiscalConsumidorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "NFC-e (Nota Fiscal de Consumidor)", description = "RASCUNHO (composta a partir de um Pedido PAGO da Loja Virtual) → (emitir) → ENVIADA → AUTORIZADA/REJEITADA/ERRO, via ACBr Monitor — só Simples Nacional, ver Javadoc de NotaFiscalConsumidor")
@RestController
@RequestMapping("/api/v1/notas-consumidor")
@RequiredArgsConstructor
public class NotaFiscalConsumidorController {

    private final NotaFiscalConsumidorService service;

    @Operation(summary = "Listar notas de consumidor")
    @ApiResponse(responseCode = "200", description = "Página de notas de consumidor")
    @GetMapping
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public PageResponse<NotaFiscalConsumidorResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar nota de consumidor por id", description = "Inclui os itens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public NotaFiscalConsumidorResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Gerar NFC-e a partir de um Pedido da Loja Virtual",
            description = "O pedido precisa estar PAGO e a Loja precisa ter empresaId vinculado; todo item precisa ter NCM/CSOSN configurado na variante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "NFC-e composta em RASCUNHO"),
            @ApiResponse(responseCode = "404", description = "Pedido ou loja não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Este pedido já tem NFC-e gerada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Pedido não está PAGO, loja sem empresaId, ou item sem NCM/CSOSN configurado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha ao consultar loja-virtual-service ou cadastro-base-service", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/de-pedido-loja/{pedidoId}")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public ResponseEntity<NotaFiscalConsumidorResponse> gerarDePedido(@PathVariable Long pedidoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarDePedido(pedidoId));
    }

    @Operation(summary = "Emitir a NFC-e via ACBr Monitor",
            description = "Monta o INI (modelo 65) e envia NFE.CriarEnviarNFe ao ACBr Monitor — sucesso vira AUTORIZADA, rejeição fiscal vira REJEITADA, falha de comunicação vira ERRO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorno do ACBr Monitor processado"),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Nota não está RASCUNHO/ERRO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/emitir")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public NotaFiscalConsumidorResponse emitir(@PathVariable Long id) {
        return service.emitir(id);
    }
}
