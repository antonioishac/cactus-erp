package br.com.cactusdigital.erp.seguranca.service;

import br.com.cactusdigital.erp.seguranca.entity.Usuario;
import br.com.cactusdigital.erp.seguranca.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Ponte entre {@code Usuario} e o {@code UserDetails} que o Spring Security usa na
 * tela de login do Authorization Server (fluxo Authorization Code). As
 * <b>authorities de negócio</b> (o que o usuário pode fazer) NÃO vêm daqui — vêm do
 * {@link TokenClaimsService}, resolvidas por papel+empresa ativa na hora de montar o
 * JWT. Aqui só validamos "esse login/senha existe e a conta está utilizável".
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String login) {
        var usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));

        return User.withUsername(usuario.getLogin())
                .password(usuario.getSenhaHash())
                .disabled(!usuario.isAtivo())
                .accountLocked(usuario.isBloqueado())
                .authorities(List.of())
                .build();
    }
}
