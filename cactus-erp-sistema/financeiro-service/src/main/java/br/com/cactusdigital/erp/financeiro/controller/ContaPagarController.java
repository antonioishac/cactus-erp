package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.AtualizarClassificacaoTituloRequest;
import br.com.cactusdigital.erp.financeiro.dto.BaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.CancelarTituloRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaPagarRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaPagarResponse;
import br.com.cactusdigital.erp.financeiro.dto.EstornarBaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.EstornoCompensacaoResponse;
import br.com.cactusdigital.erp.financeiro.dto.GerarContaPagarParceladaRequest;
import br.com.cactusdigital.erp.financeiro.dto.GerarContasPagarDeCompraRequest;
import br.com.cactusdigital.erp.financeiro.dto.RenegociarTituloRequest;
import br.com.cactusdigital.erp.financeiro.service.ContaPagarService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Contas a Pagar", description = "Título gerado manualmente ou automaticamente (1 por parcela) quando um PedidoCompra vira RECEBIDO_TOTAL no gestao-compras-service")
@RestController
@RequestMapping("/api/v1/contas-pagar")
@RequiredArgsConstructor
public class ContaPagarController {

    private final ContaPagarService service;

    @Operation(summary = "Listar contas a pagar", description = "Filtra por empresaId ou fornecedorId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de contas a pagar")
    @GetMapping
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public PageResponse<ContaPagarResponse> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long fornecedorId,
            Pageable pageable) {
        return PageResponse.of(service.listar(empresaId, fornecedorId, pageable));
    }

    @Operation(summary = "Buscar conta a pagar por id", description = "Inclui as baixas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public ContaPagarResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar conta a pagar manual")
    @ApiResponse(responseCode = "201", description = "Conta criada")
    @PostMapping
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ResponseEntity<ContaPagarResponse> criar(@Valid @RequestBody ContaPagarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Gerar contas a pagar a partir de um pedido de compra", description = "1 conta por parcela — chamado pelo gestao-compras-service quando o pedido vira RECEBIDO_TOTAL, idempotente por pedidoCompraId")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contas geradas"),
            @ApiResponse(responseCode = "409", description = "Este pedido de compra já gerou contas a pagar", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/gerar-de-compra")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ResponseEntity<List<ContaPagarResponse>> gerarDeCompra(@Valid @RequestBody GerarContasPagarDeCompraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarDeCompra(request));
    }

    @Operation(summary = "Estornar contas a pagar de um pedido de compra (compensação de saga)",
            description = "Chamado pelo gestao-compras-service quando um passo POSTERIOR à confirmação do recebimento falha (ver common.saga.SagaOrchestrator) — cancela as contas geradas por gerar-de-compra. Idempotente/tolerante: sem contas encontradas ou já canceladas, não é erro")
    @ApiResponse(responseCode = "200", description = "Quantidade de contas efetivamente canceladas (pode ser 0)")
    @PostMapping("/de-compra/{pedidoCompraId}/estornar")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public EstornoCompensacaoResponse estornarDeCompra(@PathVariable Long pedidoCompraId) {
        return new EstornoCompensacaoResponse(service.estornarPorCompra(pedidoCompraId));
    }

    @Operation(summary = "Gerar contas a pagar parceladas (lançamento manual)", description = "Parcelamento nativo — valor dividido igualmente, última parcela absorve o resto da divisão. Fecha o gap contra FIN_LANCAMENTO_PAGAR do fenix.sql")
    @ApiResponse(responseCode = "201", description = "Parcelas geradas")
    @PostMapping("/gerar-parcelado")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ResponseEntity<List<ContaPagarResponse>> gerarParcelado(@Valid @RequestBody GerarContaPagarParceladaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarParcelado(request));
    }

    @Operation(summary = "Atualizar classificação do título", description = "naturezaFinanceiraId/tipoDocumentoId/contaBancariaId/desconto/overrides de multa-juros — nunca mexe em valor/vencimento/situação. Fecha a lacuna de títulos nascidos de COMPRA, que não têm como informar isso na geração automática")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Classificação atualizada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}/classificacao")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ContaPagarResponse atualizarClassificacao(@PathVariable Long id, @Valid @RequestBody AtualizarClassificacaoTituloRequest request) {
        return service.atualizarClassificacao(id, request);
    }

    @Operation(summary = "Registrar baixa (pagamento parcial/total)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Baixa registrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conta já quitada ou cancelada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Valor da baixa maior que o saldo em aberto", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/baixas")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ContaPagarResponse registrarBaixa(@PathVariable Long id, @Valid @RequestBody BaixaRequest request) {
        return service.registrarBaixa(id, request);
    }

    @Operation(summary = "Estornar baixa", description = "Não apaga a baixa (auditoria) — marca como estornada e recalcula valorPago/valorSaldo/situacao do título. Destrava o cancelamento de um título que já tinha baixa registrada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Baixa estornada"),
            @ApiResponse(responseCode = "404", description = "Conta ou baixa não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Baixa já estornada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/baixas/{baixaId}/estornar")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ContaPagarResponse estornarBaixa(@PathVariable Long id, @PathVariable Long baixaId, @Valid @RequestBody EstornarBaixaRequest request) {
        return service.estornarBaixa(id, baixaId, request);
    }

    @Operation(summary = "Cancelar conta a pagar", description = "Só antes de qualquer baixa ativa registrada (baixas estornadas não bloqueiam)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta cancelada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conta já cancelada ou tem baixa ativa registrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('FINANCEIRO_GERENCIAR')")
    public ContaPagarResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelarTituloRequest request) {
        return service.cancelar(id, request);
    }

    @Operation(summary = "Renegociar título", description = "Substitui o saldo em aberto por N título(s) novo(s) — soma das novas parcelas precisa bater exatamente com o saldo. Fecha o gap contra FIN_STATUS_PARCELA do fenix.sql ('05 = Renegociado')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Título renegociado"),
            @ApiResponse(responseCode = "409", description = "Título não pode ser renegociado (situação inválida)", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Soma das novas parcelas diverge do saldo em aberto", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/renegociar")
    @PreAuthorize("hasAuthority('FINANCEIRO_GERENCIAR')")
    public ContaPagarResponse renegociar(@PathVariable Long id, @Valid @RequestBody RenegociarTituloRequest request) {
        return service.renegociar(id, request);
    }
}
