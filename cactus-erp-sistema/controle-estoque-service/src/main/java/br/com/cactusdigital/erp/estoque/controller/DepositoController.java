package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.estoque.dto.DepositoRequest;
import br.com.cactusdigital.erp.estoque.dto.DepositoResponse;
import br.com.cactusdigital.erp.estoque.service.DepositoService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Depósito", description = "Depósitos/armazéns de uma empresa/filial — saldo é sempre por produto×depósito")
@RestController
@RequestMapping("/api/v1/depositos")
@RequiredArgsConstructor
public class DepositoController {

    private final DepositoService service;

    @Operation(summary = "Listar depósitos", description = "Busca paginada, opcionalmente filtrada por empresa")
    @ApiResponse(responseCode = "200", description = "Página de depósitos")
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public PageResponse<DepositoResponse> listar(@RequestParam(required = false) Long empresaId, Pageable pageable) {
        return PageResponse.of(service.listar(empresaId, pageable));
    }

    @Operation(summary = "Listar depósitos ativos de uma empresa", description = "Sem paginação — uso típico: popular combo de seleção de depósito")
    @ApiResponse(responseCode = "200", description = "Lista de depósitos")
    @GetMapping("/empresa/{empresaId}/ativos")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public List<DepositoResponse> listarAtivosDaEmpresa(@PathVariable Long empresaId) {
        return service.listarAtivosDaEmpresa(empresaId);
    }

    @Operation(summary = "Buscar depósito por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito encontrado"),
            @ApiResponse(responseCode = "404", description = "Depósito não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public DepositoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar depósito")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Depósito criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe depósito com esse código nessa empresa",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ESTOQUE_GERENCIAR')")
    public ResponseEntity<DepositoResponse> criar(@Valid @RequestBody DepositoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar depósito")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito atualizado"),
            @ApiResponse(responseCode = "404", description = "Depósito não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe depósito com esse código nessa empresa",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_GERENCIAR')")
    public DepositoResponse atualizar(@PathVariable Long id, @Valid @RequestBody DepositoRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir depósito", description = "Só recomendado pra depósito nunca movimentado — considere inativar (situacao=I) em vez de excluir")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Depósito excluído"),
            @ApiResponse(responseCode = "404", description = "Depósito não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ESTOQUE_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
