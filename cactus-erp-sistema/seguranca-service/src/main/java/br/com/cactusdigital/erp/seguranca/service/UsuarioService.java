package br.com.cactusdigital.erp.seguranca.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.seguranca.dto.AlterarSenhaRequest;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioPapelEmpresaRequest;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioPapelEmpresaResponse;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioRequest;
import br.com.cactusdigital.erp.seguranca.dto.UsuarioResponse;
import br.com.cactusdigital.erp.seguranca.entity.Papel;
import br.com.cactusdigital.erp.seguranca.entity.Usuario;
import br.com.cactusdigital.erp.seguranca.entity.UsuarioPapelEmpresa;
import br.com.cactusdigital.erp.seguranca.exception.SegurancaErrorCode;
import br.com.cactusdigital.erp.seguranca.mapper.SegurancaMapper;
import br.com.cactusdigital.erp.seguranca.repository.PapelRepository;
import br.com.cactusdigital.erp.seguranca.repository.UsuarioPapelEmpresaRepository;
import br.com.cactusdigital.erp.seguranca.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * {@code colaboradorId}/{@code empresaId} não são validados contra o cadastro-base-service
 * (referência solta, cross-serviço — ver Javadoc de {@link Usuario}/{@link UsuarioPapelEmpresa}).
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;
    private final UsuarioPapelEmpresaRepository usuarioPapelEmpresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SegurancaMapper mapper;

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(String busca, Pageable pageable) {
        Page<Usuario> pagina = StringUtils.hasText(busca)
                ? usuarioRepository.findByNomeContainingIgnoreCaseOrLoginContainingIgnoreCase(busca, busca, pageable)
                : usuarioRepository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        if (!StringUtils.hasText(request.senha())) {
            throw new BusinessException(SegurancaErrorCode.USUARIO_SENHA_OBRIGATORIA);
        }
        Usuario usuario = Usuario.builder()
                .login(request.login())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .nome(request.nome())
                .email(request.email())
                .colaboradorId(request.colaboradorId())
                .ativo(request.ativo())
                .bloqueado(false)
                .build();
        return mapper.toResponse(usuarioRepository.save(usuario));
    }

    /** Não mexe em {@code ativo}/{@code bloqueado} — ver {@link #inativar}/{@link #reativar}/{@link #bloquear}/{@link #desbloquear}. */
    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        usuario.setLogin(request.login());
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setColaboradorId(request.colaboradorId());
        if (StringUtils.hasText(request.senha())) {
            usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        }
        return mapper.toResponse(usuario);
    }

    @Transactional
    public void alterarSenha(Long id, AlterarSenhaRequest request) {
        Usuario usuario = buscarEntidade(id);
        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
    }

    /** Travamento temporário (ex: excesso de tentativas de login) — reversível via {@link #desbloquear}. */
    @Transactional
    public UsuarioResponse bloquear(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setBloqueado(true);
        return mapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse desbloquear(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setBloqueado(false);
        return mapper.toResponse(usuario);
    }

    /** Desativação deliberada (equivalente a "excluído" sem perder histórico/auditoria) — reversível via {@link #reativar}. */
    @Transactional
    public UsuarioResponse inativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(false);
        return mapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse reativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(true);
        return mapper.toResponse(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        usuarioRepository.delete(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<UsuarioPapelEmpresaResponse> listarPapeisEmpresa(Long usuarioId) {
        buscarEntidade(usuarioId);
        return usuarioPapelEmpresaRepository.findByUsuarioIdOrderByIdAsc(usuarioId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public UsuarioPapelEmpresaResponse atribuirPapelEmpresa(Long usuarioId, UsuarioPapelEmpresaRequest request) {
        Usuario usuario = buscarEntidade(usuarioId);
        Papel papel = papelRepository.findById(request.papelId())
                .orElseThrow(() -> NotFoundException.of("Papel", request.papelId()));

        if (usuarioPapelEmpresaRepository.existsByUsuarioIdAndPapelIdAndEmpresaId(usuarioId, papel.getId(), request.empresaId())) {
            throw new ConflictException(SegurancaErrorCode.USUARIO_PAPEL_EMPRESA_DUPLICADO);
        }

        UsuarioPapelEmpresa associacao = UsuarioPapelEmpresa.builder()
                .usuario(usuario)
                .papel(papel)
                .empresaId(request.empresaId())
                .build();
        return mapper.toResponse(usuarioPapelEmpresaRepository.save(associacao));
    }

    @Transactional
    public void revogarPapelEmpresa(Long usuarioId, Long usuarioPapelEmpresaId) {
        UsuarioPapelEmpresa associacao = usuarioPapelEmpresaRepository.findById(usuarioPapelEmpresaId)
                .orElseThrow(() -> NotFoundException.of("Atribuição de papel", usuarioPapelEmpresaId));
        if (!associacao.getUsuario().getId().equals(usuarioId)) {
            throw NotFoundException.of("Atribuição de papel do usuário " + usuarioId, usuarioPapelEmpresaId);
        }
        usuarioPapelEmpresaRepository.delete(associacao);
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> NotFoundException.of("Usuário", id));
    }
}
