package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.FornecedorRequest;
import br.com.cactusdigital.erp.cadastro.dto.FornecedorResponse;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.service.FornecedorService;
import br.com.cactusdigital.erp.common.domain.Situacao;
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

@Tag(name = "Fornecedor", description = "Cadastro de fornecedor — referencia Pessoa (física ou jurídica), mesmo padrão de Cliente/Colaborador")
@RestController
@RequestMapping("/api/v1/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @Operation(summary = "Listar fornecedores", description = "Busca paginada com filtros por nome, documento (CPF/CNPJ), tipo de pessoa e situação")
    @ApiResponse(responseCode = "200", description = "Página de fornecedores")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_FORNECEDOR_CONSULTAR')")
    public PageResponse<FornecedorResponse> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) TipoPessoa tipoPessoa,
            @RequestParam(required = false) Situacao situacao,
            Pageable pageable) {
        return PageResponse.of(fornecedorService.listar(nome, documento, tipoPessoa, situacao, pageable));
    }

    @Operation(summary = "Buscar fornecedor por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fornecedor encontrado"),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_FORNECEDOR_CONSULTAR')")
    public FornecedorResponse buscarPorId(@PathVariable Long id) {
        return fornecedorService.buscarPorId(id);
    }

    @Operation(summary = "Criar fornecedor", description = "CPF obrigatório para pessoa física, CNPJ para pessoa jurídica")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fornecedor criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "CPF/CNPJ ausente para o tipo de pessoa informado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_FORNECEDOR_CRIAR')")
    public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorRequest request) {
        FornecedorResponse response = fornecedorService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Atualizar fornecedor", description = "enderecos/contatos/telefones substituem as listas inteiras; tipo de pessoa não pode ser alterado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fornecedor atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "CPF/CNPJ ausente, ou tentativa de mudar o tipo de pessoa",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_FORNECEDOR_EDITAR')")
    public FornecedorResponse atualizar(@PathVariable Long id, @Valid @RequestBody FornecedorRequest request) {
        return fornecedorService.atualizar(id, request);
    }

    @Operation(summary = "Excluir fornecedor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Fornecedor excluído"),
            @ApiResponse(responseCode = "404", description = "Fornecedor não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_FORNECEDOR_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        fornecedorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
