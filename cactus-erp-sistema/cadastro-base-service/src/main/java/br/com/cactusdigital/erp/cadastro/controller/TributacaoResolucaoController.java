package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ResolverTributacaoRequest;
import br.com.cactusdigital.erp.cadastro.dto.ResolverTributacaoResponse;
import br.com.cactusdigital.erp.cadastro.service.TributacaoResolucaoService;
import org.springframework.http.ProblemDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de resolução tributária prometido na Fase 1b do CLAUDE.md: recebe grupo ×
 * operação × UF e devolve o resultado achatado (CST/CSOSN/alíquotas), pronto pra
 * alimentar a sincronização com o cactus-pdv (Fase 6).
 */
@Tag(name = "Tributação — Resolução", description = "Resolve CST/CSOSN/alíquotas para uma combinação grupo × operação × UF de destino")
@RestController
@RequestMapping("/api/v1/tributacao")
@RequiredArgsConstructor
public class TributacaoResolucaoController {

    private final TributacaoResolucaoService service;

    @Operation(summary = "Resolver tributação",
            description = "Achata a regra tributária (ICMS/PIS/COFINS/IPI/ISS) para grupo tributário × operação fiscal × UF de destino")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tributação resolvida"),
            @ApiResponse(responseCode = "404", description = "Grupo tributário, operação fiscal ou configuração não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Não há regra de ICMS configurada para a UF informada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/resolver")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public ResolverTributacaoResponse resolver(@Valid @RequestBody ResolverTributacaoRequest request) {
        return service.resolver(request);
    }
}
