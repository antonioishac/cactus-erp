package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.VendedorMetaRequest;
import br.com.cactusdigital.erp.vendas.dto.VendedorMetaResponse;
import br.com.cactusdigital.erp.vendas.dto.VendedorRequest;
import br.com.cactusdigital.erp.vendas.dto.VendedorResponse;
import br.com.cactusdigital.erp.vendas.dto.VendedorRotaRequest;
import br.com.cactusdigital.erp.vendas.dto.VendedorRotaResponse;
import br.com.cactusdigital.erp.vendas.service.VendedorMetaService;
import br.com.cactusdigital.erp.vendas.service.VendedorRotaService;
import br.com.cactusdigital.erp.vendas.service.VendedorService;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Vendedor", description = "Vendedor (comissão/meta) + roteiro de visita (AFV) e metas por cliente/período")
@RestController
@RequestMapping("/api/v1/vendedores")
@RequiredArgsConstructor
public class VendedorController {

    private final VendedorService service;
    private final VendedorRotaService rotaService;
    private final VendedorMetaService metaService;

    @Operation(summary = "Listar vendedores")
    @ApiResponse(responseCode = "200", description = "Página de vendedores")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public PageResponse<VendedorResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar vendedor por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedor encontrado"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public VendedorResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Cadastrar vendedor", description = "colaboradorId é referência solta pro cadastro-base-service — um vendedor por colaborador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vendedor cadastrado"),
            @ApiResponse(responseCode = "409", description = "Já existe vendedor para este colaborador", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<VendedorResponse> criar(@Valid @RequestBody VendedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar vendedor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedor atualizado"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Já existe vendedor para este colaborador", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public VendedorResponse atualizar(@PathVariable Long id, @Valid @RequestBody VendedorRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir vendedor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vendedor excluído"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar rota (AFV) do vendedor", description = "Roteiro de visita ordenado por posição — sem paginação própria")
    @ApiResponse(responseCode = "200", description = "Lista da rota")
    @GetMapping("/{id}/rota")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public List<VendedorRotaResponse> listarRota(@PathVariable Long id) {
        return rotaService.listarPorVendedor(id);
    }

    @Operation(summary = "Adicionar cliente à rota do vendedor")
    @ApiResponse(responseCode = "201", description = "Item de rota adicionado")
    @PostMapping("/{id}/rota")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<VendedorRotaResponse> adicionarRota(@PathVariable Long id, @Valid @RequestBody VendedorRotaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rotaService.adicionar(id, request));
    }

    @Operation(summary = "Remover cliente da rota do vendedor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item de rota removido"),
            @ApiResponse(responseCode = "404", description = "Vendedor ou item de rota não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/rota/{rotaId}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<Void> removerRota(@PathVariable Long id, @PathVariable Long rotaId) {
        rotaService.remover(id, rotaId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar metas do vendedor por cliente/período", description = "Sem paginação própria")
    @ApiResponse(responseCode = "200", description = "Lista de metas")
    @GetMapping("/{id}/metas")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public List<VendedorMetaResponse> listarMetas(@PathVariable Long id) {
        return metaService.listarPorVendedor(id);
    }

    @Operation(summary = "Adicionar meta ao vendedor")
    @ApiResponse(responseCode = "201", description = "Meta adicionada")
    @PostMapping("/{id}/metas")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<VendedorMetaResponse> adicionarMeta(@PathVariable Long id, @Valid @RequestBody VendedorMetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(metaService.adicionar(id, request));
    }

    @Operation(summary = "Remover meta do vendedor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Meta removida"),
            @ApiResponse(responseCode = "404", description = "Vendedor ou meta não encontrado(a)", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/metas/{metaId}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public ResponseEntity<Void> removerMeta(@PathVariable Long id, @PathVariable Long metaId) {
        metaService.remover(id, metaId);
        return ResponseEntity.noContent().build();
    }
}
