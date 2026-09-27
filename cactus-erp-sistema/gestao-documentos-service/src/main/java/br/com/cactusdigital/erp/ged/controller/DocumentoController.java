package br.com.cactusdigital.erp.ged.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.ged.dto.ConteudoDocumentoResponse;
import br.com.cactusdigital.erp.ged.dto.DocumentoRequest;
import br.com.cactusdigital.erp.ged.dto.DocumentoResponse;
import br.com.cactusdigital.erp.ged.dto.DocumentoVersaoResponse;
import br.com.cactusdigital.erp.ged.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Documento", description = "Documento com versionamento — cada versão é um arquivo físico, armazenamento abstraído por interface")
@RestController
@RequestMapping("/api/v1/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService service;

    @Operation(summary = "Listar documentos", description = "Filtro opcional por vínculo cross-serviço (entidadeTipo+entidadeId) — ex: documentos anexados a um produto específico")
    @ApiResponse(responseCode = "200", description = "Página de documentos")
    @GetMapping
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public PageResponse<DocumentoResponse> listar(
            @RequestParam(required = false) String entidadeTipo, @RequestParam(required = false) Long entidadeId, Pageable pageable) {
        return PageResponse.of(service.listar(entidadeTipo, entidadeId, pageable));
    }

    @Operation(summary = "Buscar documento por id", description = "Inclui o histórico de versões")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento encontrado"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public DocumentoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Listar versões de um documento")
    @ApiResponse(responseCode = "200", description = "Histórico de versões, mais recente primeiro")
    @GetMapping("/{id}/versoes")
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public List<DocumentoVersaoResponse> listarVersoes(@PathVariable Long id) {
        return service.listarVersoes(id);
    }

    @Operation(summary = "Enviar novo documento", description = "multipart/form-data: metadados (JSON) + arquivo. Valida o tamanho do arquivo contra o TipoDocumento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Documento criado, primeira versão registrada"),
            @ApiResponse(responseCode = "404", description = "Tipo de documento ou pasta não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Arquivo excede o tamanho máximo do tipo de documento",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('GED_GERENCIAR')")
    public ResponseEntity<DocumentoResponse> criar(
            @Valid @RequestPart("metadados") DocumentoRequest metadados,
            @RequestPart("arquivo") MultipartFile arquivo,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("usuarioId");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(metadados, arquivo, usuarioId));
    }

    @Operation(summary = "Enviar nova versão", description = "multipart/form-data: arquivo (+ observacao opcional). Só permitido se o documento permitir alteração")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nova versão registrada"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Documento não permite alteração, ou arquivo excede o tamanho máximo",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(value = "/{id}/versoes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('GED_GERENCIAR')")
    public ResponseEntity<DocumentoVersaoResponse> adicionarVersao(
            @PathVariable Long id,
            @RequestPart("arquivo") MultipartFile arquivo,
            @RequestParam(required = false) String observacao,
            @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("usuarioId");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adicionarVersao(id, arquivo, observacao, usuarioId));
    }

    @Operation(summary = "Baixar a última versão do documento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteúdo do arquivo"),
            @ApiResponse(responseCode = "404", description = "Documento ou versão não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}/conteudo")
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public ResponseEntity<Resource> baixarConteudo(@PathVariable Long id) {
        return respostaDeConteudo(service.baixarUltimaVersao(id));
    }

    @Operation(summary = "Baixar uma versão específica do documento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteúdo do arquivo"),
            @ApiResponse(responseCode = "404", description = "Documento ou versão não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Versão não tem arquivo associado (foi gerada por uma exclusão lógica)",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}/versoes/{versaoId}/conteudo")
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public ResponseEntity<Resource> baixarConteudoVersao(@PathVariable Long id, @PathVariable Long versaoId) {
        return respostaDeConteudo(service.baixarVersao(id, versaoId));
    }

    @Operation(summary = "Excluir documento (lógico)", description = "Só permitido se o documento permitir exclusão — registra uma versão EXCLUSAO e muda a situação pra INATIVO")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Documento excluído"),
            @ApiResponse(responseCode = "404", description = "Documento não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Documento já está inativo",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Documento está marcado como não excluível",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GED_GERENCIAR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("usuarioId");
        service.excluir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Resource> respostaDeConteudo(ConteudoDocumentoResponse conteudo) {
        String nomeArquivo = conteudo.nomeArquivoOriginal() != null ? conteudo.nomeArquivoOriginal() : "arquivo";
        MediaType tipo = conteudo.contentType() != null
                ? MediaType.parseMediaType(conteudo.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nomeArquivo, StandardCharsets.UTF_8).build().toString())
                .body(conteudo.conteudo());
    }
}
