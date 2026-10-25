package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.financeiro.dto.VendaPdvDinheiroRequest;
import br.com.cactusdigital.erp.financeiro.dto.VendaPdvDinheiroResponse;
import br.com.cactusdigital.erp.financeiro.service.FinanceiroPdvService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "PDV — Financeiro", description = "Entradas do PDV que chegam ao financeiro por chamada de serviço (Fase 6-B). Caixa/fiado chegam pela fila financeiro.pdv.q, não por aqui")
@RestController
@RequestMapping("/api/v1/pdv")
@RequiredArgsConstructor
public class PdvFinanceiroController {

    private final FinanceiroPdvService service;

    @Operation(summary = "Lançar o dinheiro líquido de uma venda do PDV no caixa do terminal",
            description = "Chamado pelo vendas-service ao registrar uma VendaPdv. Idempotente por registroId: repetir a chamada não lança de novo (aplicado=false). Valor zero só registra o processamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processado (aplicado=true) ou já processado antes (aplicado=false)"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/vendas/dinheiro")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public VendaPdvDinheiroResponse registrarDinheiroVenda(@Valid @RequestBody VendaPdvDinheiroRequest request) {
        return new VendaPdvDinheiroResponse(service.registrarDinheiroVenda(request));
    }

    @Operation(summary = "Estornar o dinheiro de uma venda do PDV cancelada depois de fechada",
            description = "Chamado pelo vendas-service ao registrar o cancelamento. SAÍDA do valor na conta do terminal (dataVenda = data do cancelamento). "
                    + "Idempotente pelo registroId do cancelamento (aplicado=false na repetição)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processado (aplicado=true) ou já processado antes (aplicado=false)"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/vendas/dinheiro/estorno")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public VendaPdvDinheiroResponse estornarDinheiroVenda(@Valid @RequestBody VendaPdvDinheiroRequest request) {
        return new VendaPdvDinheiroResponse(service.estornarDinheiroVenda(request));
    }
}
