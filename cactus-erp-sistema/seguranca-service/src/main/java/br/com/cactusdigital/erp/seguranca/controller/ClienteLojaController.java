package br.com.cactusdigital.erp.seguranca.controller;

import br.com.cactusdigital.erp.seguranca.dto.ClienteLojaRequest;
import br.com.cactusdigital.erp.seguranca.dto.ClienteLojaResponse;
import br.com.cactusdigital.erp.seguranca.dto.ClienteLojaResumoResponse;
import br.com.cactusdigital.erp.seguranca.service.ClienteLojaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provisionamento de client OAuth2 "um por loja" pra sincronização com o cactus-pdv (ver
 * FASE6_SINCRONIZACAO_PDV.md, seção 9.2/11.3). Vive no seguranca-service porque é o
 * Authorization Server do ecossistema — o {@code sincronizacao-service} só consome o
 * resultado (a claim {@code lojaId} no token emitido).
 */
@Tag(name = "Segurança — Cliente de Loja (sincronização)",
        description = "Client OAuth2 Client Credentials um-por-loja, usado pelo bridge/PDV pra chamar o sincronizacao-service com isolamento por lojaId")
@RestController
@RequestMapping("/api/v1/sincronizacao/clientes-loja")
@RequiredArgsConstructor
public class ClienteLojaController {

    private final ClienteLojaService service;

    @Operation(summary = "Provisionar client de sincronização para uma loja",
            description = "clientSecret só aparece nesta resposta — nunca mais recuperável depois (só o hash fica persistido)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client provisionado"),
            @ApiResponse(responseCode = "409", description = "Já existe client para essa loja",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public ResponseEntity<ClienteLojaResponse> provisionar(@Valid @RequestBody ClienteLojaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.provisionar(request));
    }

    @Operation(summary = "Listar clients de sincronização provisionados", description = "Sem o secret — só metadado administrativo")
    @ApiResponse(responseCode = "200", description = "Lista de clients")
    @GetMapping
    @PreAuthorize("hasAuthority('SEGURANCA_GERENCIAR')")
    public List<ClienteLojaResumoResponse> listar() {
        return service.listar();
    }
}
