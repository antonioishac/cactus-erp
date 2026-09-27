package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.GerarRemessaRequest;
import br.com.cactusdigital.erp.financeiro.dto.RemessaBancariaResponse;
import br.com.cactusdigital.erp.financeiro.dto.RetornoBancarioResponse;
import br.com.cactusdigital.erp.financeiro.service.EdiBancarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "EDI Bancário", description = "Remessa CNAB 240 (registro de título em carteira de cobrança) e retorno CNAB 240 (baixa automática por nosso número) — só CNAB 240, CNAB 400 fora de escopo")
@RestController
@RequestMapping("/api/v1/edi-bancario")
@RequiredArgsConstructor
public class EdiBancarioController {

    private final EdiBancarioService service;

    @Operation(summary = "Listar remessas geradas", description = "Filtra por contaBancariaId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de remessas")
    @GetMapping("/remessas")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<RemessaBancariaResponse> listarRemessas(@RequestParam(required = false) Long contaBancariaId, Pageable pageable) {
        return PageResponse.of(service.listarRemessas(contaBancariaId, pageable));
    }

    @Operation(summary = "Buscar remessa por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Remessa encontrada"),
            @ApiResponse(responseCode = "404", description = "Remessa não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/remessas/{id}")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public RemessaBancariaResponse buscarRemessaPorId(@PathVariable Long id) {
        return service.buscarRemessaPorId(id);
    }

    @Operation(summary = "Gerar remessa CNAB 240", description = "1 lote, segmentos P+Q por título — só título ABERTO/PARCIAL pode ser incluído. Dados do sacado (nome/documento/endereço) informados no request, não resolvidos automaticamente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Remessa gerada"),
            @ApiResponse(responseCode = "404", description = "Conta bancária, banco ou título não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Título não está ABERTO/PARCIAL", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/remessas/gerar")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ResponseEntity<RemessaBancariaResponse> gerarRemessa(@Valid @RequestBody GerarRemessaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarRemessa(request));
    }

    @Operation(summary = "Baixar arquivo CNAB 240 da remessa", description = "Texto puro, 240 posições por linha, pronto pra enviar ao banco")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo CNAB240"),
            @ApiResponse(responseCode = "404", description = "Remessa não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping(value = "/remessas/{id}/arquivo", produces = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public ResponseEntity<byte[]> baixarArquivoRemessa(@PathVariable Long id) {
        String conteudo = service.baixarArquivoRemessa(id);
        ContentDisposition disposition = ContentDisposition.attachment().filename("remessa-" + id + ".rem", StandardCharsets.ISO_8859_1).build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.TEXT_PLAIN)
                .body(conteudo.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Operation(summary = "Listar retornos importados", description = "Filtra por contaBancariaId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de retornos")
    @GetMapping("/retornos")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<RetornoBancarioResponse> listarRetornos(@RequestParam(required = false) Long contaBancariaId, Pageable pageable) {
        return PageResponse.of(service.listarRetornos(contaBancariaId, pageable));
    }

    @Operation(summary = "Importar retorno CNAB 240", description = "Resolve título por nosso número (contra remessas já geradas) e dispara baixa automática nos títulos liquidados (código de ocorrência 06) — outros códigos ficam só registrados, sem ação automática")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Retorno importado"),
            @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Arquivo não é um retorno CNAB240 válido", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(value = "/retornos/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ResponseEntity<RetornoBancarioResponse> importarRetorno(
            @RequestParam Long contaBancariaId,
            @RequestPart("arquivo") MultipartFile arquivo) {
        byte[] conteudo;
        try {
            conteudo = arquivo.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Falha lendo o arquivo de retorno CNAB240 enviado", e);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.importarRetorno(contaBancariaId, arquivo.getOriginalFilename(), conteudo));
    }
}
