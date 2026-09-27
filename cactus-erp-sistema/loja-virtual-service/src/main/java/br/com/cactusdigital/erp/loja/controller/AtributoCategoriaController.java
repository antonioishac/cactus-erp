package br.com.cactusdigital.erp.loja.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaOpcaoRequest;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaOpcaoResponse;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaRequest;
import br.com.cactusdigital.erp.loja.dto.AtributoCategoriaResponse;
import br.com.cactusdigital.erp.loja.service.AtributoCategoriaService;
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

@Tag(name = "Atributos da Categoria", description = "Schema de atributo dinâmico por categoria (Tamanho/Cor pra roupa, Volume/Safra pra vinho...) — o que permite o catálogo atender qualquer nicho sem tabela nova")
@RestController
@RequestMapping("/api/v1/atributos-categoria")
@RequiredArgsConstructor
public class AtributoCategoriaController {

    private final AtributoCategoriaService service;

    @Operation(summary = "Listar atributos", description = "Filtra por categoriaLojaId (opcional). Inclui as opções (quando tipo=LISTA)")
    @ApiResponse(responseCode = "200", description = "Página de atributos")
    @GetMapping
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public PageResponse<AtributoCategoriaResponse> listar(@RequestParam(required = false) Long categoriaLojaId, Pageable pageable) {
        return PageResponse.of(service.listar(categoriaLojaId, pageable));
    }

    @Operation(summary = "Buscar atributo por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atributo encontrado"),
            @ApiResponse(responseCode = "404", description = "Atributo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public AtributoCategoriaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Criar atributo", description = "opcoes só é relevante quando tipo=LISTA")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Atributo criado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<AtributoCategoriaResponse> criar(@Valid @RequestBody AtributoCategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Atualizar dados do atributo", description = "Não toca nas opções — use os sub-recursos de opção pra isso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atributo atualizado"),
            @ApiResponse(responseCode = "404", description = "Atributo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public AtributoCategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtributoCategoriaRequest request) {
        return service.atualizar(id, request);
    }

    @Operation(summary = "Excluir atributo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Atributo excluído"),
            @ApiResponse(responseCode = "404", description = "Atributo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adicionar opção ao atributo (tipo LISTA)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Opção adicionada"),
            @ApiResponse(responseCode = "404", description = "Atributo não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/opcoes")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<AtributoCategoriaOpcaoResponse> adicionarOpcao(@PathVariable Long id, @Valid @RequestBody AtributoCategoriaOpcaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarOpcao(id, request));
    }

    @Operation(summary = "Remover opção do atributo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Opção removida"),
            @ApiResponse(responseCode = "404", description = "Atributo ou opção não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}/opcoes/{opcaoId}")
    @PreAuthorize("hasAuthority('LOJA_GERENCIAR')")
    public ResponseEntity<Void> removerOpcao(@PathVariable Long id, @PathVariable Long opcaoId) {
        service.removerOpcao(id, opcaoId);
        return ResponseEntity.noContent().build();
    }
}
