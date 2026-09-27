package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.TransportadoraRequest;
import br.com.cactusdigital.erp.cadastro.dto.TransportadoraResponse;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.service.TransportadoraService;
import br.com.cactusdigital.erp.common.domain.Situacao;
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

@Tag(name = "Transportadora", description = "Cadastro de transportadora — referencia Pessoa (física ou jurídica), mesmo padrão de Cliente/Colaborador/Fornecedor")
@RestController
@RequestMapping("/api/v1/transportadoras")
@RequiredArgsConstructor
public class TransportadoraController {

    private final TransportadoraService transportadoraService;

    @Operation(summary = "Listar transportadoras", description = "Busca paginada com filtros por nome, documento (CPF/CNPJ), tipo de pessoa e situação")
    @ApiResponse(responseCode = "200", description = "Página de transportadoras")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRANSPORTADORA_CONSULTAR')")
    public PageResponse<TransportadoraResponse> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) TipoPessoa tipoPessoa,
            @RequestParam(required = false) Situacao situacao,
            Pageable pageable) {
        return PageResponse.of(transportadoraService.listar(nome, documento, tipoPessoa, situacao, pageable));
    }

    @Operation(summary = "Buscar transportadora por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transportadora encontrada"),
            @ApiResponse(responseCode = "404", description = "Transportadora não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRANSPORTADORA_CONSULTAR')")
    public TransportadoraResponse buscarPorId(@PathVariable Long id) {
        return transportadoraService.buscarPorId(id);
    }

    @Operation(summary = "Criar transportadora", description = "CPF obrigatório para pessoa física, CNPJ para pessoa jurídica")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transportadora criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "CPF/CNPJ ausente para o tipo de pessoa informado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_TRANSPORTADORA_CRIAR')")
    public ResponseEntity<TransportadoraResponse> criar(@Valid @RequestBody TransportadoraRequest request) {
        TransportadoraResponse response = transportadoraService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualizar transportadora", description = "enderecos/contatos/telefones substituem as listas inteiras; tipo de pessoa não pode ser alterado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transportadora atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Transportadora não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "CPF/CNPJ ausente, ou tentativa de mudar o tipo de pessoa",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRANSPORTADORA_EDITAR')")
    public TransportadoraResponse atualizar(@PathVariable Long id, @Valid @RequestBody TransportadoraRequest request) {
        return transportadoraService.atualizar(id, request);
    }

    @Operation(summary = "Excluir transportadora")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transportadora excluída"),
            @ApiResponse(responseCode = "404", description = "Transportadora não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_TRANSPORTADORA_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        transportadoraService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
