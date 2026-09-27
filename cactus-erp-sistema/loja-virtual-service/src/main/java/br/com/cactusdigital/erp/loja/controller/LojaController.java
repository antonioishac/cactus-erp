package br.com.cactusdigital.erp.loja.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.loja.dto.LojaRequest;
import br.com.cactusdigital.erp.loja.dto.LojaResponse;
import br.com.cactusdigital.erp.loja.service.LojaService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Lojas", description = "Tenants da plataforma white-label — cada Loja isola o catálogo de um lojista, ver Javadoc de Loja")
@RestController
@RequestMapping("/api/v1/lojas")
@RequiredArgsConstructor
public class LojaController {

    private final LojaService service;

    @Operation(summary = "Listar lojas")
    @ApiResponse(responseCode = "200", description = "Página de lojas")
    @GetMapping
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public PageResponse<LojaResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar loja por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loja encontrada"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public LojaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar loja (provisionar novo tenant)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Loja criada"),
            @ApiResponse(responseCode = "409", description = "Subdomínio já utilizado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<LojaResponse> criar(@Valid @RequestBody LojaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar dados da loja")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loja atualizada"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Subdomínio já utilizado por outra loja", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public LojaResponse atualizar(@PathVariable Long id, @Valid @RequestBody LojaRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Cancelar assinatura da loja")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assinatura cancelada"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar-assinatura")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public LojaResponse cancelarAssinatura(@PathVariable Long id) {
        return service.cancelarAssinatura(id);
    }

    @Operation(summary = "Reativar assinatura da loja")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assinatura reativada"),
            @ApiResponse(responseCode = "404", description = "Loja não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/reativar-assinatura")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public LojaResponse reativarAssinatura(@PathVariable Long id) {
        return service.reativarAssinatura(id);
    }
}
