package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.FormaPagamentoRequest;
import br.com.cactusdigital.erp.cadastro.dto.FormaPagamentoResponse;
import br.com.cactusdigital.erp.cadastro.service.FormaPagamentoService;
import org.springframework.http.ProblemDetail;
import br.com.cactusdigital.erp.common.rest.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Empresa — Forma de Pagamento", description = "Master data que o cactus-pdv espera importar da retaguarda (PDV_TIPO_PAGAMENTO)")
@RestController
@RequestMapping("/api/v1/formas-pagamento")
@RequiredArgsConstructor
public class FormaPagamentoController {

    private final FormaPagamentoService service;

    @Operation(summary = "Listar formas de pagamento", description = "Busca paginada por descrição (opcional) ou por cursor de sincronização (desde) — os dois filtros não se combinam, desde tem prioridade")
    @ApiResponse(responseCode = "200", description = "Página de formas de pagamento")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public PageResponse<FormaPagamentoResponse> listar(
            @RequestParam(required = false) String descricao,
            @Parameter(description = "Cursor de sincronização — só formas de pagamento alteradas depois desse instante (ISO-8601)")
            @RequestParam(required = false) Instant desde,
            Pageable pageable) {
        return PageResponse.of(desde != null ? service.listar(desde, pageable) : service.listar(descricao, pageable));
    }

    @Operation(summary = "Buscar forma de pagamento por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Forma de pagamento encontrada"),
            @ApiResponse(responseCode = "404", description = "Forma de pagamento não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CONSULTAR')")
    public FormaPagamentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar forma de pagamento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Forma de pagamento criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_CRIAR')")
    public ResponseEntity<FormaPagamentoResponse> criar(@Valid @RequestBody FormaPagamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar forma de pagamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Forma de pagamento atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Forma de pagamento não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EDITAR')")
    public FormaPagamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody FormaPagamentoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir forma de pagamento")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Forma de pagamento excluída"),
            @ApiResponse(responseCode = "404", description = "Forma de pagamento não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_EMPRESA_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
