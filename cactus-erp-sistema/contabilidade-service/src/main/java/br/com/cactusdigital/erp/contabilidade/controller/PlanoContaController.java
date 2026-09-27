package br.com.cactusdigital.erp.contabilidade.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.contabilidade.dto.PlanoContaRequest;
import br.com.cactusdigital.erp.contabilidade.dto.PlanoContaResponse;
import br.com.cactusdigital.erp.contabilidade.service.PlanoContaService;
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

@Tag(name = "Plano de Contas", description = "Hierarquia de contas contábeis (sintéticas/analíticas) por empresa")
@RestController
@RequestMapping("/api/v1/plano-contas")
@RequiredArgsConstructor
public class PlanoContaController {

    private final PlanoContaService service;

    @Operation(summary = "Listar contas do plano de contas (paginado)")
    @ApiResponse(responseCode = "200", description = "Página de contas")
    @GetMapping
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public PageResponse<PlanoContaResponse> listar(@RequestParam Long empresaId, Pageable pageable) {
        return PageResponse.of(service.listar(empresaId, pageable));
    }

    @Operation(summary = "Listar todas as contas da empresa, sem paginação", description = "Pra popular Select no frontend — catálogo pequeno por empresa")
    @ApiResponse(responseCode = "200", description = "Todas as contas, ordenadas por código")
    @GetMapping("/todas")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public List<PlanoContaResponse> listarTodas(@RequestParam Long empresaId) {
        return service.listarTodas(empresaId);
    }

    @Operation(summary = "Buscar conta por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta encontrada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public PlanoContaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar conta", description = "contaPaiId, se informado, precisa ser uma conta SINTÉTICA (contaAnalitica=false) da mesma empresa")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conta criada"),
            @ApiResponse(responseCode = "409", description = "Código já usado nesta empresa", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Conta pai inválida (não sintética, ou de outra empresa)", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CONTABIL_GERENCIAR')")
    public ResponseEntity<PlanoContaResponse> criar(@Valid @RequestBody PlanoContaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar conta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conta atualizada"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Código já usado nesta empresa", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Conta pai inválida ou criaria um ciclo na hierarquia", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTABIL_GERENCIAR')")
    public PlanoContaResponse atualizar(@PathVariable Long id, @Valid @RequestBody PlanoContaRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir conta", description = "Só permitido se a conta não tiver lançamentos nem subcontas vinculadas")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conta excluída"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conta com lançamento ou subconta vinculada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTABIL_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
