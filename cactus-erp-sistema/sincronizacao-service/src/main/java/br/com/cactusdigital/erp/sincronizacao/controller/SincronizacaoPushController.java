package br.com.cactusdigital.erp.sincronizacao.controller;

import br.com.cactusdigital.erp.sincronizacao.dto.LoteSincronizacaoRequest;
import br.com.cactusdigital.erp.sincronizacao.dto.LoteSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.service.LojaAutorizacaoService;
import br.com.cactusdigital.erp.sincronizacao.service.LoteSincronizacaoPushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code isAuthenticated()} em vez de uma authority específica de propósito: o
 * isolamento aqui não é por papel/authority, é por {@code lojaId} (ver
 * {@link LojaAutorizacaoService}) — um client "um por loja" (provisionado via
 * {@code ClienteLojaService}, no seguranca-service, ver FASE6_SINCRONIZACAO_PDV.md seção
 * 9.2/11.3) só pode empurrar dado da própria loja; um token sem a claim {@code lojaId}
 * (serviço interno/admin) passa sem restrição adicional.
 */
@Tag(name = "Sincronização — Push (PDV→ERP)", description = "Envelope de lote de operação de caixa empurrado pelo PDV — hoje só ESTOQUE_MOVIMENTO tem destino real, ver FASE6_SINCRONIZACAO_PDV.md")
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SincronizacaoPushController {

    private final LoteSincronizacaoPushService service;
    private final LojaAutorizacaoService lojaAutorizacaoService;

    @Operation(summary = "Empurrar lote de sincronização", description = "Resposta síncrona e imediata — ACEITO só garante que não será "
            + "perdido (outbox), não que o consumidor de domínio já processou. Reenvio do mesmo idOrigem é idempotente, devolve o resultado já registrado")
    @ApiResponse(responseCode = "201", description = "Lote recebido — ver resultados por item no corpo")
    @PostMapping("/lotes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LoteSincronizacaoResponse> receberLote(@Valid @RequestBody LoteSincronizacaoRequest request, @AuthenticationPrincipal Jwt jwt) {
        lojaAutorizacaoService.validarAcesso(jwt, request.lojaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.receber(request));
    }
}
