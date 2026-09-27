package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.ConciliarAutomaticoResponse;
import br.com.cactusdigital.erp.financeiro.dto.ConciliarManualRequest;
import br.com.cactusdigital.erp.financeiro.dto.ExtratoBancarioImportacaoResponse;
import br.com.cactusdigital.erp.financeiro.dto.ExtratoBancarioItemResponse;
import br.com.cactusdigital.erp.financeiro.dto.GerarAjusteConciliacaoRequest;
import br.com.cactusdigital.erp.financeiro.dto.PendenciasConciliacaoResponse;
import br.com.cactusdigital.erp.financeiro.service.ConciliacaoBancariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

@Tag(name = "Conciliação Bancária", description = "Import de extrato bancário via arquivo OFX (upload manual) + matching automático/manual contra MovimentoCaixaBanco")
@RestController
@RequestMapping("/api/v1/conciliacao-bancaria")
@RequiredArgsConstructor
public class ConciliacaoBancariaController {

    private final ConciliacaoBancariaService service;

    @Operation(summary = "Listar importações de extrato", description = "Filtra por contaBancariaId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de importações")
    @GetMapping("/importacoes")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<ExtratoBancarioImportacaoResponse> listarImportacoes(@RequestParam(required = false) Long contaBancariaId, Pageable pageable) {
        return PageResponse.of(service.listarImportacoes(contaBancariaId, pageable));
    }

    @Operation(summary = "Listar itens de uma importação")
    @ApiResponse(responseCode = "200", description = "Página de itens do extrato")
    @GetMapping("/importacoes/{importacaoId}/itens")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<ExtratoBancarioItemResponse> listarItens(@PathVariable Long importacaoId, Pageable pageable) {
        return PageResponse.of(service.listarItens(importacaoId, pageable));
    }

    @Operation(summary = "Importar extrato bancário (arquivo OFX)", description = "Upload manual — dedup automático por FITID contra importações anteriores da mesma conta, não concilia automaticamente (ver /conciliar-automatico)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Extrato importado"),
            @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Arquivo não é um OFX válido", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping(value = "/importar-extrato", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ResponseEntity<ExtratoBancarioImportacaoResponse> importar(
            @RequestParam Long contaBancariaId,
            @RequestPart("arquivo") MultipartFile arquivo,
            @RequestParam(required = false) Long usuarioId) {
        byte[] conteudo;
        try {
            conteudo = arquivo.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Falha lendo o arquivo OFX enviado", e);
        }
        String nomeArquivo = arquivo.getOriginalFilename();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.importar(contaBancariaId, nomeArquivo, conteudo, usuarioId));
    }

    @Operation(summary = "Pendências de conciliação", description = "Itens do extrato ainda não conciliados + movimentos internos ainda não conciliados, lado a lado")
    @ApiResponse(responseCode = "200", description = "Pendências de conciliação")
    @GetMapping("/pendencias")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PendenciasConciliacaoResponse pendencias(@RequestParam Long contaBancariaId) {
        return service.pendencias(contaBancariaId);
    }

    @Operation(summary = "Conciliar automaticamente", description = "Casa item de extrato × movimento interno por mesma direção/valor exato/data com tolerância de 3 dias — só concilia quando há exatamente 1 candidato")
    @ApiResponse(responseCode = "200", description = "Resultado da conciliação automática")
    @PostMapping("/{contaBancariaId}/conciliar-automatico")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ConciliarAutomaticoResponse conciliarAutomatico(@PathVariable Long contaBancariaId) {
        return service.conciliarAutomatico(contaBancariaId);
    }

    @Operation(summary = "Conciliar manualmente", description = "Override do operador — liga um item de extrato a um movimento interno específico, sem validar valor/data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item conciliado"),
            @ApiResponse(responseCode = "404", description = "Item ou movimento não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Item ou movimento já conciliado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/itens/{itemId}/conciliar-manual")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ExtratoBancarioItemResponse conciliarManual(@PathVariable Long itemId, @Valid @RequestBody ConciliarManualRequest request) {
        return service.conciliarManual(itemId, request.movimentoCaixaBancoId());
    }

    @Operation(summary = "Desconciliar item", description = "Desfaz o vínculo item×movimento (auto ou manual) — os dois voltam a ficar pendentes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item desconciliado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Item não está conciliado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/itens/{itemId}/desconciliar")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ExtratoBancarioItemResponse desconciliar(@PathVariable Long itemId) {
        return service.desconciliar(itemId);
    }

    @Operation(summary = "Gerar ajuste a partir de item sem lançamento correspondente", description = "Cria um MovimentoCaixaBanco real (tarifa/rendimento não lançado antes) a partir da linha do extrato e já concilia os dois")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ajuste gerado e item conciliado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Item já conciliado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/itens/{itemId}/gerar-ajuste")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ExtratoBancarioItemResponse gerarAjuste(@PathVariable Long itemId, @Valid @RequestBody GerarAjusteConciliacaoRequest request) {
        return service.gerarAjuste(itemId, request);
    }
}
