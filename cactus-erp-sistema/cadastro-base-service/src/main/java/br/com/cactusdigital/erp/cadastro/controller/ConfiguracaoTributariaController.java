package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoTributariaRequest;
import br.com.cactusdigital.erp.cadastro.dto.ConfiguracaoTributariaResponse;
import br.com.cactusdigital.erp.cadastro.service.ConfiguracaoTributariaService;
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

@Tag(name = "Tributação — Configuração Tributária",
        description = "A célula Grupo Tributário × Operação Fiscal: ICMS por UF de destino, PIS, COFINS e IPI")
@RestController
@RequestMapping("/api/v1/tributacao/configuracoes")
@RequiredArgsConstructor
public class ConfiguracaoTributariaController {

    private final ConfiguracaoTributariaService service;

    @Operation(summary = "Listar configurações tributárias")
    @ApiResponse(responseCode = "200", description = "Página de configurações tributárias")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public PageResponse<ConfiguracaoTributariaResponse> listar(
            @Parameter(description = "Filtro opcional por grupo tributário") @RequestParam(required = false) Long grupoTributarioId,
            @Parameter(description = "Filtro opcional por operação fiscal") @RequestParam(required = false) Long operacaoFiscalId,
            Pageable pageable) {
        return PageResponse.of(service.listar(grupoTributarioId, operacaoFiscalId, pageable));
    }

    @Operation(summary = "Buscar configuração tributária por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuração tributária encontrada"),
            @ApiResponse(responseCode = "404", description = "Configuração tributária não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public ConfiguracaoTributariaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar configuração tributária",
            description = "Uma linha em icmsPorUf por UF de destino onde o grupo×operação incide")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Configuração tributária criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Grupo tributário ou operação fiscal não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Já existe configuração para esse grupo × operação",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CRIAR')")
    public ResponseEntity<ConfiguracaoTributariaResponse> criar(@Valid @RequestBody ConfiguracaoTributariaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar configuração tributária",
            description = "icmsPorUf substitui a lista inteira; pis/cofins/ipi nulos removem a respectiva configuração")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuração tributária atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Configuração, grupo tributário ou operação fiscal não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Já existe configuração para esse grupo × operação",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EDITAR')")
    public ConfiguracaoTributariaResponse atualizar(@PathVariable Long id, @Valid @RequestBody ConfiguracaoTributariaRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir configuração tributária")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Configuração tributária excluída"),
            @ApiResponse(responseCode = "404", description = "Configuração tributária não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
