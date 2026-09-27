package br.com.cactusdigital.erp.seguranca.controller;

import br.com.cactusdigital.erp.seguranca.dto.EmpresaDisponivelResponse;
import br.com.cactusdigital.erp.seguranca.dto.MeResponse;
import br.com.cactusdigital.erp.seguranca.dto.TrocarEmpresaRequest;
import br.com.cactusdigital.erp.seguranca.dto.TrocarEmpresaResponse;
import br.com.cactusdigital.erp.seguranca.service.TokenClaimsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sessão do usuário autenticado: quem ele é, em quais empresas ele tem papel
 * atribuído, e como trocar a empresa ativa do token sem passar de novo pelo fluxo
 * completo de login (Authorization Code).
 * <p>
 * {@link #trocarEmpresa} não usa o endpoint {@code /oauth2/token} do Authorization
 * Server — ele assina um JWT novo diretamente com o mesmo {@link JwtEncoder} que o
 * Authorization Server usa internamente (mesma chave, mesmas claims resolvidas por
 * {@link TokenClaimsService}), então o token resultante é validado pelos outros
 * serviços exatamente como qualquer outro. Justamente por isso <b>não devolve
 * {@code refresh_token}</b> — não é um {@code OAuth2Authorization} persistido, é um
 * JWT solto de vida curta (30 min, mesma duração do access token normal).
 * <p>
 * <b>Padrão de uso esperado no frontend</b>: guarda o {@code access_token}/
 * {@code refresh_token} originais do login (esses sim renovam pelo fluxo OAuth2
 * padrão) e usa eles pra manter a sessão viva; guarda também qual empresa está ativa.
 * Sempre que o usuário estiver operando fora da empresa padrão E o token renovado por
 * {@code refresh_token} tiver voltado a resolver a empresa padrão (ver limitação
 * documentada em {@code AuthorizationServerConfig#jwtTokenCustomizer}), chama
 * {@link #trocarEmpresa} de novo pra reobter um token escopado pra empresa certa antes
 * de usar a API de negócio.
 */
@Tag(name = "Autenticação", description = "Sessão do usuário autenticado — quem é, em quais empresas tem papel, troca de empresa ativa")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Duration DURACAO_TOKEN = Duration.ofMinutes(30);

    private final TokenClaimsService tokenClaimsService;
    private final JwtEncoder jwtEncoder;

    @Value("${cactus.security.issuer-uri:http://localhost:8082}")
    private String issuerUri;

    @Operation(summary = "Dados do usuário autenticado", description = "Extrai id, login, empresa ativa e authorities direto das claims do JWT")
    @ApiResponse(responseCode = "200", description = "Dados do usuário")
    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Jwt jwt) {
        return new MeResponse(
                jwt.getClaim("usuarioId"),
                jwt.getClaim("login"),
                jwt.getClaim("empresaId"),
                jwt.getClaimAsStringList("authorities"));
    }

    @Operation(summary = "Listar empresas disponíveis", description = "Empresas em que o usuário logado tem algum papel atribuído (UsuarioPapelEmpresa)")
    @ApiResponse(responseCode = "200", description = "Empresas disponíveis")
    @GetMapping("/empresas-disponiveis")
    public List<EmpresaDisponivelResponse> empresasDisponiveis(@AuthenticationPrincipal Jwt jwt) {
        String login = jwt.getClaim("login");
        return tokenClaimsService.empresasDisponiveis(login).stream()
                .map(e -> new EmpresaDisponivelResponse(e.empresaId(), e.papeis()))
                .toList();
    }

    @Operation(summary = "Trocar empresa ativa", description = "Emite um JWT novo (mesma chave do Authorization Server) com as authorities resolvidas pra outra empresa, sem repetir o fluxo de login")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token reemitido para a nova empresa ativa"),
            @ApiResponse(responseCode = "422", description = "Usuário não tem papel atribuído nessa empresa",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/trocar-empresa")
    public TrocarEmpresaResponse trocarEmpresa(@AuthenticationPrincipal Jwt jwtAtual, @Valid @RequestBody TrocarEmpresaRequest request) {
        String login = jwtAtual.getClaim("login");
        TokenClaimsService.ClaimsResolvidas claims = tokenClaimsService.resolverParaEmpresa(login, request.empresaId());

        Instant agora = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(issuerUri)
                .subject(login)
                .issuedAt(agora)
                .expiresAt(agora.plus(DURACAO_TOKEN))
                .claim("authorities", List.copyOf(claims.authorities()))
                .claim("usuarioId", claims.usuarioId())
                .claim("login", claims.login())
                .claim("empresaId", claims.empresaId())
                .build();

        Jwt novoJwt = jwtEncoder.encode(JwtEncoderParameters.from(claimsSet));

        return new TrocarEmpresaResponse(
                novoJwt.getTokenValue(),
                "Bearer",
                DURACAO_TOKEN.toSeconds(),
                claims.empresaId(),
                List.copyOf(claims.authorities()));
    }
}
