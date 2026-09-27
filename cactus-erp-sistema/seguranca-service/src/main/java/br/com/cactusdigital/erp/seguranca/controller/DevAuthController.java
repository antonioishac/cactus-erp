package br.com.cactusdigital.erp.seguranca.controller;

import br.com.cactusdigital.erp.seguranca.dto.DevLoginRequest;
import br.com.cactusdigital.erp.seguranca.dto.DevLoginResponse;
import br.com.cactusdigital.erp.seguranca.entity.Usuario;
import br.com.cactusdigital.erp.seguranca.repository.UsuarioRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Atalho de autenticação <b>só pra desenvolvimento local</b>: login+senha direto,
 * devolve um JWT já pronto — sem passar pelo fluxo Authorization Code + PKCE completo
 * (redirect de navegador, tela de login HTML, troca de code por token), que é o correto
 * pra um frontend de verdade mas é tedioso demais pra testar API na mão via curl/Postman.
 * <p>
 * Existe <b>apenas fora do perfil {@code prod}</b> (bean nem é criado — não é só uma
 * checagem em runtime, o endpoint literalmente não existe) e mesmo assim reaproveita
 * exatamente o mesmo {@link JwtEncoder}/{@link TokenClaimsService} que o Authorization
 * Server usa de verdade — o token resultante tem o mesmo formato/claims de um token
 * emitido pelo fluxo padrão, só o caminho pra emiti-lo é mais curto.
 */
@Profile("!prod")
@Tag(name = "Autenticação (dev)", description = "Atalho de login só para ambiente local — nunca disponível em produção")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class DevAuthController {

    private static final Logger log = LoggerFactory.getLogger(DevAuthController.class);
    private static final Duration DURACAO_TOKEN = Duration.ofMinutes(30);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenClaimsService tokenClaimsService;
    private final JwtEncoder jwtEncoder;

    @Value("${cactus.security.issuer-uri:http://localhost:8082}")
    private String issuerUri;

    @Operation(summary = "[DEV] Login direto por login+senha",
            description = "Atalho só de desenvolvimento local para obter um token sem passar pelo fluxo Authorization Code + PKCE. Nunca existe em produção (perfil prod).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token emitido"),
            @ApiResponse(responseCode = "401", description = "Login ou senha inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/dev-login")
    public DevLoginResponse devLogin(@Valid @RequestBody DevLoginRequest request) {
        Usuario usuario = usuarioRepository.findByLogin(request.login())
                .filter(u -> u.isAtivo() && !u.isBloqueado())
                .filter(u -> passwordEncoder.matches(request.senha(), u.getSenhaHash()))
                .orElseThrow(() -> new BadCredentialsException("Login ou senha inválidos"));

        log.warn("Token emitido via /dev-login para '{}' — endpoint de desenvolvimento, nunca disponível em produção", usuario.getLogin());

        TokenClaimsService.ClaimsResolvidas claims = tokenClaimsService.resolverEmpresaPadrao(usuario.getLogin());
        Instant agora = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(issuerUri)
                .subject(usuario.getLogin())
                .issuedAt(agora)
                .expiresAt(agora.plus(DURACAO_TOKEN))
                .claim("authorities", List.copyOf(claims.authorities()))
                .claim("usuarioId", claims.usuarioId())
                .claim("login", claims.login())
                .claim("empresaId", claims.empresaId())
                .build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claimsSet));

        return new DevLoginResponse(
                jwt.getTokenValue(),
                "Bearer",
                DURACAO_TOKEN.toSeconds(),
                claims.empresaId(),
                List.copyOf(claims.authorities()));
    }
}
