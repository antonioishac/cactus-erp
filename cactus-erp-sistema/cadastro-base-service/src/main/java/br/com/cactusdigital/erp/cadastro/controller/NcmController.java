package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.NcmRequest;
import br.com.cactusdigital.erp.cadastro.dto.NcmResponse;
import br.com.cactusdigital.erp.cadastro.service.NcmService;
import org.springframework.http.ProblemDetail;
import br.com.cactusdigital.erp.common.rest.PageResponse;
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

@Tag(name = "Tributação — NCM", description = "Nomenclatura Comum do Mercosul (catálogo, amostra representativa)")
@RestController
@RequestMapping("/api/v1/ncm")
@RequiredArgsConstructor
public class NcmController {

    private final NcmService service;

    @Operation(summary = "Listar NCM", description = "Busca paginada por código ou descrição, opcional")
    @ApiResponse(responseCode = "200", description = "Página de NCM")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public PageResponse<NcmResponse> listar(@RequestParam(required = false) String busca, Pageable pageable) {
        return PageResponse.of(service.listar(busca, pageable));
    }

    @Operation(summary = "Buscar NCM por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "NCM encontrado"),
            @ApiResponse(responseCode = "404", description = "NCM não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public NcmResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar NCM")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "NCM criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CRIAR')")
    public ResponseEntity<NcmResponse> criar(@Valid @RequestBody NcmRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar NCM")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "NCM atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "NCM não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EDITAR')")
    public NcmResponse atualizar(@PathVariable Long id, @Valid @RequestBody NcmRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir NCM")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "NCM excluído"),
            @ApiResponse(responseCode = "404", description = "NCM não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
