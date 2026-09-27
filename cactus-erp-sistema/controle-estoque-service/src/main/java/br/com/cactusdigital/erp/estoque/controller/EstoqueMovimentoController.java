package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoRequest;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoResponse;
import br.com.cactusdigital.erp.estoque.service.EstoqueMovimentoService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ledger append-only — só {@code GET}/{@code POST}, nunca {@code PUT}/{@code DELETE}: um
 * movimento registrado não é corrigido, é compensado com outro movimento (ex: um
 * {@code AJUSTE} pra reverter um lançamento errado).
 */
@Tag(name = "Movimento de Estoque", description = "Ledger append-only de entrada/saída/ajuste/perda/devolução/transferência")
@RestController
@RequestMapping("/api/v1/movimentos")
@RequiredArgsConstructor
public class EstoqueMovimentoController {

    private final EstoqueMovimentoService service;

    @Operation(summary = "Listar movimentos", description = "Busca paginada, opcionalmente filtrada por produto e/ou depósito")
    @ApiResponse(responseCode = "200", description = "Página de movimentos")
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public PageResponse<EstoqueMovimentoResponse> listar(
            @RequestParam(required = false) Long produtoId, @RequestParam(required = false) Long depositoId, Pageable pageable) {
        return PageResponse.of(service.listarPorProdutoEDeposito(produtoId, depositoId, pageable));
    }

    @Operation(summary = "Buscar movimento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimento encontrado"),
            @ApiResponse(responseCode = "404", description = "Movimento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public EstoqueMovimentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Registrar movimento de estoque",
            description = "Entrada/saída/ajuste/perda/devolução manual. usuarioId é extraído do token, não do corpo da requisição. "
                    + "AJUSTE exige direcaoAjuste; AJUSTE/PERDA acima do limite configurado (ConfiguracaoEstoqueEmpresa) exigem aprovadorId.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimento registrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Depósito, lote ou número de série não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Saldo insuficiente, direção de ajuste ausente ou aprovador obrigatório ausente",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ESTOQUE_MOVIMENTAR')")
    public ResponseEntity<EstoqueMovimentoResponse> registrar(
            @Valid @RequestBody EstoqueMovimentoRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("usuarioId");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request, usuarioId));
    }
}
