package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.TabelaPrecoRequest;
import br.com.cactusdigital.erp.cadastro.dto.TabelaPrecoResponse;
import br.com.cactusdigital.erp.cadastro.service.TabelaPrecoService;
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

@Tag(name = "Produto — Tabela de Preço", description = "Precificação por canal/perfil (Varejo, Atacado, Cliente VIP...), complementar à faixa de preço por quantidade")
@RestController
@RequestMapping("/api/v1/tabelas-precos")
@RequiredArgsConstructor
public class TabelaPrecoController {

    private final TabelaPrecoService service;

    @Operation(summary = "Listar tabelas de preço", description = "Filtro por nome ou por cursor de sincronização (desde) — os dois não se combinam, desde tem prioridade")
    @ApiResponse(responseCode = "200", description = "Página de tabelas de preço")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public PageResponse<TabelaPrecoResponse> listar(
            @Parameter(description = "Filtro por nome, contém, sem distinção de caixa") @RequestParam(required = false) String nome,
            @Parameter(description = "Cursor de sincronização — só tabelas alteradas depois desse instante (ISO-8601)")
            @RequestParam(required = false) Instant desde,
            Pageable pageable) {
        return PageResponse.of(desde != null ? service.listar(desde, pageable) : service.listar(nome, pageable));
    }

    @Operation(summary = "Buscar tabela de preço por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tabela de preço encontrada"),
            @ApiResponse(responseCode = "404", description = "Tabela de preço não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public TabelaPrecoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar tabela de preço", description = "produtos substitui a lista inteira de itens precificados")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tabela de preço criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Algum produto informado não existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CRIAR')")
    public ResponseEntity<TabelaPrecoResponse> criar(@Valid @RequestBody TabelaPrecoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar tabela de preço")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tabela de preço atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Tabela de preço ou algum produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EDITAR')")
    public TabelaPrecoResponse atualizar(@PathVariable Long id, @Valid @RequestBody TabelaPrecoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir tabela de preço")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tabela de preço excluída"),
            @ApiResponse(responseCode = "404", description = "Tabela de preço não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
