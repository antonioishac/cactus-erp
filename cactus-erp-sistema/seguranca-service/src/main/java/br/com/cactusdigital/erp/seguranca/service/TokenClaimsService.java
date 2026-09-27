package br.com.cactusdigital.erp.seguranca.service;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.seguranca.entity.Funcionalidade;
import br.com.cactusdigital.erp.seguranca.entity.Usuario;
import br.com.cactusdigital.erp.seguranca.entity.UsuarioPapelEmpresa;
import br.com.cactusdigital.erp.seguranca.exception.SegurancaErrorCode;
import br.com.cactusdigital.erp.seguranca.repository.UsuarioPapelEmpresaRepository;
import br.com.cactusdigital.erp.seguranca.repository.UsuarioRepository;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resolve, para um usuário + empresa ativa, o conjunto de {@code authorities}
 * (códigos de {@link Funcionalidade}) que vai pro claim {@code authorities} do JWT.
 * Reaproveitado tanto pelo login normal (Authorization Code — ver
 * {@code br.com.cactusdigital.erp.seguranca.config.AuthorizationServerConfig}) quanto
 * pelo endpoint de troca de empresa ativa
 * ({@code br.com.cactusdigital.erp.seguranca.controller.AuthController#trocarEmpresa}) —
 * mesma regra nos dois casos, um único lugar pra manter.
 * <p>
 * Não resolve {@code empresaNome} — este serviço não tem cópia local de {@code Empresa}
 * (mora no cadastro-base-service, outro schema); resolver o nome aqui exigiria uma
 * chamada síncrona ao cadastro-base em toda emissão de token, o que tornaria o login
 * dependente da disponibilidade de outro serviço. O claim carrega só {@code empresaId}.
 */
@Service
@RequiredArgsConstructor
public class TokenClaimsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioPapelEmpresaRepository usuarioPapelEmpresaRepository;

    public record ClaimsResolvidas(Long usuarioId, String login, Long empresaId, Set<String> authorities) {
    }

    public record EmpresaDisponivel(Long empresaId, List<String> papeis) {
    }

    /** Usada no login: pega a primeira empresa que o usuário tem acesso (a mais antiga atribuição). */
    @Transactional(readOnly = true)
    public ClaimsResolvidas resolverEmpresaPadrao(String login) {
        Usuario usuario = buscarUsuario(login);
        List<UsuarioPapelEmpresa> associacoes = usuarioPapelEmpresaRepository.findByUsuarioIdOrderByIdAsc(usuario.getId());
        Long empresaId = associacoes.isEmpty() ? null : associacoes.get(0).getEmpresaId();
        return montar(usuario, empresaId, associacoes);
    }

    /** Usada na troca de empresa ativa — valida que o usuário realmente tem acesso a ela. */
    @Transactional(readOnly = true)
    public ClaimsResolvidas resolverParaEmpresa(String login, Long empresaId) {
        Usuario usuario = buscarUsuario(login);
        List<UsuarioPapelEmpresa> associacoes = usuarioPapelEmpresaRepository.findByUsuarioIdOrderByIdAsc(usuario.getId());
        boolean temAcesso = associacoes.stream().anyMatch(a -> a.getEmpresaId().equals(empresaId));
        if (!temAcesso) {
            throw new BusinessException(SegurancaErrorCode.USUARIO_SEM_ACESSO_EMPRESA);
        }
        return montar(usuario, empresaId, associacoes);
    }

    @Transactional(readOnly = true)
    public List<EmpresaDisponivel> empresasDisponiveis(String login) {
        Usuario usuario = buscarUsuario(login);
        Map<Long, EmpresaDisponivelAcumulador> porEmpresa = new LinkedHashMap<>();
        for (UsuarioPapelEmpresa associacao : usuarioPapelEmpresaRepository.findByUsuarioIdOrderByIdAsc(usuario.getId())) {
            porEmpresa
                    .computeIfAbsent(associacao.getEmpresaId(), EmpresaDisponivelAcumulador::new)
                    .papeis.add(associacao.getPapel().getNome());
        }
        return porEmpresa.values().stream()
                .map(a -> new EmpresaDisponivel(a.empresaId, List.copyOf(a.papeis)))
                .toList();
    }

    private ClaimsResolvidas montar(Usuario usuario, Long empresaId, List<UsuarioPapelEmpresa> todasAssociacoes) {
        if (empresaId == null) {
            return new ClaimsResolvidas(usuario.getId(), usuario.getLogin(), null, Set.of());
        }
        Set<String> authorities = new HashSet<>();
        for (UsuarioPapelEmpresa associacao : todasAssociacoes) {
            if (!associacao.getEmpresaId().equals(empresaId)) {
                continue;
            }
            for (Funcionalidade funcionalidade : associacao.getPapel().getFuncionalidades()) {
                if (funcionalidade.getSituacao() == Situacao.ATIVO) {
                    authorities.add(funcionalidade.getCodigo());
                }
            }
        }
        return new ClaimsResolvidas(usuario.getId(), usuario.getLogin(), empresaId, authorities);
    }

    private Usuario buscarUsuario(String login) {
        return usuarioRepository.findByLogin(login).orElseThrow(() -> NotFoundException.of("Usuário", login));
    }

    private static final class EmpresaDisponivelAcumulador {
        private final Long empresaId;
        private final List<String> papeis = new java.util.ArrayList<>();

        private EmpresaDisponivelAcumulador(Long empresaId) {
            this.empresaId = empresaId;
        }
    }
}
