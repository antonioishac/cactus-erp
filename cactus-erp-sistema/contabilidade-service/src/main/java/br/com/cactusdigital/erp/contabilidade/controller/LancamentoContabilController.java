package br.com.cactusdigital.erp.contabilidade.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.contabilidade.dto.EstornarLancamentoRequest;
import br.com.cactusdigital.erp.contabilidade.dto.LancamentoContabilRequest;
import br.com.cactusdigital.erp.contabilidade.dto.LancamentoContabilResponse;
import br.com.cactusdigital.erp.contabilidade.service.LancamentoContabilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Lançamentos Contábeis", description = "Livro Diário — partida dobrada validada no servidor, imutável depois de lançado (só estorno)")
@RestController
@RequestMapping("/api/v1/lancamentos-contabeis")
@RequiredArgsConstructor
public class LancamentoContabilController {

    private final LancamentoContabilService service;

    @Operation(summary = "Listar lançamentos contábeis", description = "dataInicio/dataFim opcionais — sem eles, lista tudo da empresa")
    @ApiResponse(responseCode = "200", description = "Página de lançamentos, com as partidas já carregadas")
    @GetMapping
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public PageResponse<LancamentoContabilResponse> listar(
            @RequestParam Long empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Pageable pageable) {
        return PageResponse.of(service.listar(empresaId, dataInicio, dataFim, pageable));
    }

    @Operation(summary = "Buscar lançamento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lançamento encontrado"),
            @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public LancamentoContabilResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Lançar (criar) um lançamento contábil",
            description = "Pelo menos 2 partidas, soma dos débitos EXATAMENTE igual à soma dos créditos — validado no servidor, sem tolerância de arredondamento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lançamento criado"),
            @ApiResponse(responseCode = "404", description = "Conta ou centro de custo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Partida dobrada divergente, menos de 2 partidas, ou conta não analítica/de outra empresa", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CONTABIL_LANCAR')")
    public ResponseEntity<LancamentoContabilResponse> criar(@Valid @RequestBody LancamentoContabilRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Estornar lançamento",
            description = "Gera um NOVO lançamento com as partidas invertidas — o original nunca é editado/apagado, só marcado ESTORNADO")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estorno criado"),
            @ApiResponse(responseCode = "404", description = "Lançamento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Lançamento já estornado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Este lançamento já é um estorno de outro", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/estornar")
    @PreAuthorize("hasAuthority('CONTABIL_LANCAR')")
    public ResponseEntity<LancamentoContabilResponse> estornar(@PathVariable Long id, @Valid @RequestBody EstornarLancamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.estornar(id, request));
    }
}
