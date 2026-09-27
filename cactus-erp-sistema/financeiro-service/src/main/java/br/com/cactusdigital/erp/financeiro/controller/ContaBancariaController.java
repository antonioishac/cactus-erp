package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.ContaBancariaRequest;
import br.com.cactusdigital.erp.financeiro.dto.ContaBancariaResponse;
import br.com.cactusdigital.erp.financeiro.service.ContaBancariaService;
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

@Tag(name = "Contas Bancárias", description = "Controle de Caixa e Bancos — conta corrente/poupança/caixa interno/aplicação por empresa, saldo mantido só via MovimentoCaixaBanco")
@RestController
@RequestMapping("/api/v1/contas-bancarias")
@RequiredArgsConstructor
public class ContaBancariaController {

    private final ContaBancariaService service;

    @Operation(summary = "Listar contas bancárias", description = "Filtra por empresaId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de contas bancárias")
    @GetMapping
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<ContaBancariaResponse> listar(@RequestParam(required = false) Long empresaId, Pageable pageable) {
        return PageResponse.of(service.listar(empresaId, pageable));
    }

    @Operation(summary = "Listar contas bancárias ATIVAS de uma empresa", description = "Sem paginação — pra popular Select de conta bancária em telas de baixa/movimento")
    @ApiResponse(responseCode = "200", description = "Lista de contas ativas")
    @GetMapping("/ativas")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public List<ContaBancariaResponse> listarAtivas(@RequestParam Long empresaId) {
        return service.listarAtivasDaEmpresa(empresaId);
    }

    @Operation(summary = "Buscar conta bancária por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public ContaBancariaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar conta bancária", description = "saldoInicial vira o saldoAtual inicial — mudanças de saldo depois só via MovimentoCaixaBanco")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta criada"),
            @ApiResponse(responseCode = "422", description = "Banco obrigatório pra tipo != CAIXA_INTERNO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CAIXA_GERENCIAR')")
    public ResponseEntity<ContaBancariaResponse> criar(@Valid @RequestBody ContaBancariaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar dados cadastrais da conta bancária", description = "Não mexe em saldo (saldoInicial/saldoAtual ignorados aqui)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta atualizada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CAIXA_GERENCIAR')")
    public ContaBancariaResponse atualizar(@PathVariable Long id, @Valid @RequestBody ContaBancariaRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Inativar conta bancária", description = "Bloqueia novos movimentos — não afeta o histórico do ledger")
    @ApiResponse(responseCode = "200", description = "Conta inativada")
    @PostMapping("/{id}/inativar")
    @PreAuthorize("hasAuthority('CAIXA_GERENCIAR')")
    public ContaBancariaResponse inativar(@PathVariable Long id) {
        return service.inativar(id);
    }

    @Operation(summary = "Reativar conta bancária")
    @ApiResponse(responseCode = "200", description = "Conta reativada")
    @PostMapping("/{id}/reativar")
    @PreAuthorize("hasAuthority('CAIXA_GERENCIAR')")
    public ContaBancariaResponse reativar(@PathVariable Long id) {
        return service.reativar(id);
    }
}
