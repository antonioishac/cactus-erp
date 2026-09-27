package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.AtualizarClassificacaoTituloRequest;
import br.com.cactusdigital.erp.financeiro.dto.BaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.CancelarTituloRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaReceberRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaReceberResponse;
import br.com.cactusdigital.erp.financeiro.dto.EstornarBaixaRequest;
import br.com.cactusdigital.erp.financeiro.dto.EstornoCompensacaoResponse;
import br.com.cactusdigital.erp.financeiro.dto.GerarContaReceberParceladaRequest;
import br.com.cactusdigital.erp.financeiro.dto.GerarContasReceberDeVendaRequest;
import br.com.cactusdigital.erp.financeiro.dto.RenegociarTituloRequest;
import br.com.cactusdigital.erp.financeiro.service.ContaReceberService;
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

@Tag(name = "Contas a Receber", description = "Título gerado manualmente ou automaticamente (1 por parcela) na confirmação de uma Venda pelo vendas-service")
@RestController
@RequestMapping("/api/v1/contas-receber")
@RequiredArgsConstructor
public class ContaReceberController {

    private final ContaReceberService service;

    @Operation(summary = "Listar contas a receber", description = "Filtra por empresaId ou clienteId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de contas a receber")
    @GetMapping
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public PageResponse<ContaReceberResponse> listar(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long clienteId,
            Pageable pageable) {
        return PageResponse.of(service.listar(empresaId, clienteId, pageable));
    }

    @Operation(summary = "Buscar conta a receber por id", description = "Inclui as baixas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public ContaReceberResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar conta a receber manual")
    @ApiResponse(responseCode = "201", description = "Conta criada")
    @PostMapping
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ResponseEntity<ContaReceberResponse> criar(@Valid @RequestBody ContaReceberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Gerar contas a receber a partir de uma venda", description = "1 conta por parcela — chamado pelo vendas-service ao confirmar a venda, idempotente por vendaId")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contas geradas"),
            @ApiResponse(responseCode = "409", description = "Esta venda já gerou contas a receber", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/gerar-de-venda")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ResponseEntity<List<ContaReceberResponse>> gerarDeVenda(@Valid @RequestBody GerarContasReceberDeVendaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarDeVenda(request));
    }

    @Operation(summary = "Estornar contas a receber de uma venda (compensação de saga)",
            description = "Chamado pelo vendas-service quando um passo POSTERIOR à confirmação da venda falha (ver common.saga.SagaOrchestrator) — cancela as contas geradas por gerar-de-venda. Idempotente/tolerante: sem contas encontradas ou já canceladas, não é erro")
    @ApiResponse(responseCode = "200", description = "Quantidade de contas efetivamente canceladas (pode ser 0)")
    @PostMapping("/de-venda/{vendaId}/estornar")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public EstornoCompensacaoResponse estornarDeVenda(@PathVariable Long vendaId) {
        return new EstornoCompensacaoResponse(service.estornarPorVenda(vendaId));
    }

    @Operation(summary = "Gerar contas a receber parceladas (lançamento manual)", description = "Parcelamento nativo — valor dividido igualmente, última parcela absorve o resto da divisão. Fecha o gap contra FIN_LANCAMENTO_RECEBER do fenix.sql")
    @ApiResponse(responseCode = "201", description = "Parcelas geradas")
    @PostMapping("/gerar-parcelado")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ResponseEntity<List<ContaReceberResponse>> gerarParcelado(@Valid @RequestBody GerarContaReceberParceladaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarParcelado(request));
    }

    @Operation(summary = "Atualizar classificação do título", description = "naturezaFinanceiraId/tipoDocumentoId/contaBancariaId/desconto/overrides de multa-juros — nunca mexe em valor/vencimento/situação. Fecha a lacuna de títulos nascidos de VENDA, que não têm como informar isso na geração automática")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Classificação atualizada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}/classificacao")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ContaReceberResponse atualizarClassificacao(@PathVariable Long id, @Valid @RequestBody AtualizarClassificacaoTituloRequest request) {
        return service.atualizarClassificacao(id, request);
    }

    @Operation(summary = "Registrar baixa (recebimento parcial/total)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Baixa registrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conta já quitada ou cancelada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Valor da baixa maior que o saldo em aberto", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/baixas")
    @PreAuthorize("hasAuthority('FINANCEIRO_LANCAR')")
    public ContaReceberResponse registrarBaixa(@PathVariable Long id, @Valid @RequestBody BaixaRequest request) {
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
    public ContaReceberResponse estornarBaixa(@PathVariable Long id, @PathVariable Long baixaId, @Valid @RequestBody EstornarBaixaRequest request) {
        return service.estornarBaixa(id, baixaId, request);
    }

    @Operation(summary = "Cancelar conta a receber", description = "Só antes de qualquer baixa ativa registrada (baixas estornadas não bloqueiam)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta cancelada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conta já cancelada ou tem baixa ativa registrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('FINANCEIRO_GERENCIAR')")
    public ContaReceberResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelarTituloRequest request) {
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
    public ContaReceberResponse renegociar(@PathVariable Long id, @Valid @RequestBody RenegociarTituloRequest request) {
        return service.renegociar(id, request);
    }
}
