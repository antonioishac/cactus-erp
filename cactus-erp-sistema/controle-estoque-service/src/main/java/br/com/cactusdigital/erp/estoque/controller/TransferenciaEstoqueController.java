package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.estoque.dto.TransferenciaEstoqueRequest;
import br.com.cactusdigital.erp.estoque.dto.TransferenciaEstoqueResponse;
import br.com.cactusdigital.erp.estoque.service.TransferenciaEstoqueService;
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

@Tag(name = "Transferência de Estoque", description = "Transferência entre depósitos/filiais — enviar (saída na origem) e receber (entrada no destino) são passos separados")
@RestController
@RequestMapping("/api/v1/transferencias")
@RequiredArgsConstructor
public class TransferenciaEstoqueController {

    private final TransferenciaEstoqueService service;

    @Operation(summary = "Listar transferências de um depósito", description = "Traz tanto as que saem quanto as que chegam nesse depósito")
    @ApiResponse(responseCode = "200", description = "Página de transferências")
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public PageResponse<TransferenciaEstoqueResponse> listarPorDeposito(@RequestParam Long depositoId, Pageable pageable) {
        return PageResponse.of(service.listarPorDeposito(depositoId, pageable));
    }

    @Operation(summary = "Buscar transferência por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência encontrada"),
            @ApiResponse(responseCode = "404", description = "Transferência não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public TransferenciaEstoqueResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar transferência", description = "Situação inicial SOLICITADA — ainda não afeta saldo, só enviar() de fato baixa o estoque da origem")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transferência criada"),
            @ApiResponse(responseCode = "404", description = "Depósito de origem ou destino não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Depósito de origem e destino são o mesmo",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ESTOQUE_TRANSFERIR')")
    public ResponseEntity<TransferenciaEstoqueResponse> criar(@Valid @RequestBody TransferenciaEstoqueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Enviar transferência", description = "Gera TRANSFERENCIA_SAIDA no depósito origem e marca EM_TRANSITO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência enviada"),
            @ApiResponse(responseCode = "404", description = "Transferência não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Transferência não está em SOLICITADA",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Saldo insuficiente no depósito de origem",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/enviar")
    @PreAuthorize("hasAuthority('ESTOQUE_TRANSFERIR')")
    public TransferenciaEstoqueResponse enviar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.enviar(id, jwt.getClaim("usuarioId"));
    }

    @Operation(summary = "Receber transferência", description = "Gera TRANSFERENCIA_ENTRADA no depósito destino e marca CONCLUIDA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência recebida"),
            @ApiResponse(responseCode = "404", description = "Transferência não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Transferência não está em EM_TRANSITO",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/receber")
    @PreAuthorize("hasAuthority('ESTOQUE_TRANSFERIR')")
    public TransferenciaEstoqueResponse receber(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return service.receber(id, jwt.getClaim("usuarioId"));
    }

    @Operation(summary = "Cancelar transferência", description = "Só permitido enquanto ainda está SOLICITADA (não enviada)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência cancelada"),
            @ApiResponse(responseCode = "404", description = "Transferência não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Transferência não está em SOLICITADA",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('ESTOQUE_TRANSFERIR')")
    public TransferenciaEstoqueResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
