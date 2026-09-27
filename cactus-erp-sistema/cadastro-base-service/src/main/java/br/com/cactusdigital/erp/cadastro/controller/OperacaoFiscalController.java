package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.OperacaoFiscalRequest;
import br.com.cactusdigital.erp.cadastro.dto.OperacaoFiscalResponse;
import br.com.cactusdigital.erp.cadastro.service.OperacaoFiscalService;
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

@Tag(name = "Tributação — Operação Fiscal", description = "Tipo de operação de venda (CFOP + ISS quando for serviço)")
@RestController
@RequestMapping("/api/v1/tributacao/operacoes-fiscais")
@RequiredArgsConstructor
public class OperacaoFiscalController {

    private final OperacaoFiscalService service;

    @Operation(summary = "Listar operações fiscais", description = "Busca paginada por nome, opcional")
    @ApiResponse(responseCode = "200", description = "Página de operações fiscais")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public PageResponse<OperacaoFiscalResponse> listar(
            @Parameter(description = "Filtro por nome, contém, sem distinção de caixa") @RequestParam(required = false) String nome,
            Pageable pageable) {
        return PageResponse.of(service.listar(nome, pageable));
    }

    @Operation(summary = "Buscar operação fiscal por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operação fiscal encontrada"),
            @ApiResponse(responseCode = "404", description = "Operação fiscal não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public OperacaoFiscalResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar operação fiscal", description = "Preencher issConfiguracao só quando a operação for de serviço")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Operação fiscal criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "CFOP informado não existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CRIAR')")
    public ResponseEntity<OperacaoFiscalResponse> criar(@Valid @RequestBody OperacaoFiscalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar operação fiscal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operação fiscal atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Operação fiscal ou CFOP não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EDITAR')")
    public OperacaoFiscalResponse atualizar(@PathVariable Long id, @Valid @RequestBody OperacaoFiscalRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir operação fiscal")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Operação fiscal excluída"),
            @ApiResponse(responseCode = "404", description = "Operação fiscal não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
