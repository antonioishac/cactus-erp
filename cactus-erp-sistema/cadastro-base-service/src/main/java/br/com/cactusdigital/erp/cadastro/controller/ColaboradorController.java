package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ColaboradorRequest;
import br.com.cactusdigital.erp.cadastro.dto.ColaboradorResponse;
import br.com.cactusdigital.erp.cadastro.service.ColaboradorService;
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

@Tag(name = "Colaborador", description = "Cadastro de colaborador (RH básico) — referencia Pessoa, cargo/setor, comissão e veículo de entregador")
@RestController
@RequestMapping("/api/v1/colaboradores")
@RequiredArgsConstructor
public class ColaboradorController {

    private final ColaboradorService colaboradorService;

    @Operation(summary = "Listar colaboradores", description = "Busca paginada com filtros por nome, documento, cargo, setor e situação")
    @ApiResponse(responseCode = "200", description = "Página de colaboradores")
    @GetMapping
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_CONSULTAR')")
    public PageResponse<ColaboradorResponse> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) Long cargoId,
            @RequestParam(required = false) Long setorId,
            @RequestParam(required = false) Situacao situacao,
            Pageable pageable) {
        return PageResponse.of(colaboradorService.listar(nome, documento, cargoId, setorId, situacao, pageable));
    }

    @Operation(summary = "Buscar colaborador por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Colaborador encontrado"),
            @ApiResponse(responseCode = "404", description = "Colaborador não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_CONSULTAR')")
    public ColaboradorResponse buscarPorId(@PathVariable Long id) {
        return colaboradorService.buscarPorId(id);
    }

    @Operation(summary = "Criar colaborador", description = "CPF obrigatório para pessoa física, CNPJ para pessoa jurídica")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Colaborador criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Cargo ou setor informado não existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "CPF/CNPJ ausente para o tipo de pessoa informado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_CRIAR')")
    public ResponseEntity<ColaboradorResponse> criar(@Valid @RequestBody ColaboradorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(colaboradorService.criar(request));
    }

    @Operation(summary = "Atualizar colaborador", description = "tipo de pessoa não pode ser alterado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Colaborador atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Colaborador, cargo ou setor não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "CPF/CNPJ ausente, ou tentativa de mudar o tipo de pessoa",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_EDITAR')")
    public ColaboradorResponse atualizar(@PathVariable Long id, @Valid @RequestBody ColaboradorRequest request) {
        return colaboradorService.atualizar(id, request);
    }

    @Operation(summary = "Excluir colaborador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Colaborador excluído"),
            @ApiResponse(responseCode = "404", description = "Colaborador não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CADASTRO_COLABORADOR_EXCLUIR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        colaboradorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
