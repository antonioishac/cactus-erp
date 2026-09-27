package br.com.cactusdigital.erp.sincronizacao.controller;

import br.com.cactusdigital.erp.sincronizacao.dto.ClienteSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.ConfiguracaoLojaSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.FormaPagamentoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.OperadorSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.ProdutoBalancaSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.ProdutoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.PullResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.TabelaPrecoSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.service.LojaAutorizacaoService;
import br.com.cactusdigital.erp.sincronizacao.service.SincronizacaoPullService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Isolamento dos endpoints com {@code lojaId} feito via {@link LojaAutorizacaoService}
 * (não por authority — ver Javadoc daquela classe e do
 * {@code SincronizacaoPushController}, mesmo critério). Endpoints sem {@code lojaId}
 * (clientes/tabelas de preço/formas de pagamento) são catálogos globais, sem dado
 * específico de loja pra isolar.
 */
@Tag(name = "Sincronização — Pull (ERP→PDV)", description = "Cursor de alteração (desde), não full dump — ver seção 5.1 do FASE6_SINCRONIZACAO_PDV.md")
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SincronizacaoPullController {

    private final SincronizacaoPullService service;
    private final LojaAutorizacaoService lojaAutorizacaoService;

    @Operation(summary = "Pull de produtos", description = "Tributação já resolvida + saldo de estoque agregado da loja (soma de todos os depósitos)")
    @ApiResponse(responseCode = "200", description = "Página de produtos")
    @GetMapping("/produtos")
    @PreAuthorize("isAuthenticated()")
    public PullResponse<ProdutoSincronizacaoResponse> pullProdutos(
            @RequestParam Long lojaId,
            @Parameter(description = "Cursor — ausente = carga completa (primeira sincronização)") @RequestParam(required = false) Instant desde,
            @RequestParam(defaultValue = "0") int pagina,
            @AuthenticationPrincipal Jwt jwt) {
        lojaAutorizacaoService.validarAcesso(jwt, lojaId);
        return service.pullProdutos(lojaId, desde, pagina);
    }

    @Operation(summary = "Pull de produtos pra balança de rede autônoma", description = "Caminho B — payload reduzido: código, descrição truncada, preço, peso-variável")
    @ApiResponse(responseCode = "200", description = "Página de produtos (formato balança)")
    @GetMapping("/produtos/balanca")
    @PreAuthorize("isAuthenticated()")
    public PullResponse<ProdutoBalancaSincronizacaoResponse> pullProdutosBalanca(
            @RequestParam Long lojaId, @RequestParam(required = false) Instant desde, @RequestParam(defaultValue = "0") int pagina,
            @AuthenticationPrincipal Jwt jwt) {
        lojaAutorizacaoService.validarAcesso(jwt, lojaId);
        return service.pullProdutosBalanca(lojaId, desde, pagina);
    }

    @Operation(summary = "Pull de clientes")
    @ApiResponse(responseCode = "200", description = "Página de clientes")
    @GetMapping("/clientes")
    @PreAuthorize("isAuthenticated()")
    public PullResponse<ClienteSincronizacaoResponse> pullClientes(
            @RequestParam(required = false) Instant desde, @RequestParam(defaultValue = "0") int pagina) {
        return service.pullClientes(desde, pagina);
    }

    @Operation(summary = "Pull de tabelas de preço")
    @ApiResponse(responseCode = "200", description = "Página de tabelas de preço")
    @GetMapping("/tabelas-preco")
    @PreAuthorize("isAuthenticated()")
    public PullResponse<TabelaPrecoSincronizacaoResponse> pullTabelasPreco(
            @RequestParam(required = false) Instant desde, @RequestParam(defaultValue = "0") int pagina) {
        return service.pullTabelasPreco(desde, pagina);
    }

    @Operation(summary = "Pull de formas de pagamento", description = "Fecha o gap que o próprio DDL do PDV aponta (PDV_TIPO_PAGAMENTO)")
    @ApiResponse(responseCode = "200", description = "Página de formas de pagamento")
    @GetMapping("/formas-pagamento")
    @PreAuthorize("isAuthenticated()")
    public PullResponse<FormaPagamentoSincronizacaoResponse> pullFormasPagamento(
            @RequestParam(required = false) Instant desde, @RequestParam(defaultValue = "0") int pagina) {
        return service.pullFormasPagamento(desde, pagina);
    }

    @Operation(summary = "Pull de configuração operacional da loja", description = "Mesas/comandas/delivery + política comercial de desconto/acréscimo — objeto único, sem cursor")
    @ApiResponse(responseCode = "200", description = "Configuração da loja")
    @GetMapping("/configuracao-loja")
    @PreAuthorize("isAuthenticated()")
    public ConfiguracaoLojaSincronizacaoResponse pullConfiguracaoLoja(@RequestParam Long lojaId, @AuthenticationPrincipal Jwt jwt) {
        lojaAutorizacaoService.validarAcesso(jwt, lojaId);
        return service.pullConfiguracaoLoja(lojaId);
    }

    @Operation(summary = "Pull de operadores (PDV_OPERADOR)", description = "Composição Usuario+Papel por empresa — login, hash bcrypt da senha, nivelAutorizacao (G/S/O/E). Lista completa, sem cursor")
    @ApiResponse(responseCode = "200", description = "Operadores da loja")
    @GetMapping("/operadores")
    @PreAuthorize("isAuthenticated()")
    public List<OperadorSincronizacaoResponse> pullOperadores(@RequestParam Long lojaId, @AuthenticationPrincipal Jwt jwt) {
        lojaAutorizacaoService.validarAcesso(jwt, lojaId);
        return service.pullOperadores(lojaId);
    }
}
