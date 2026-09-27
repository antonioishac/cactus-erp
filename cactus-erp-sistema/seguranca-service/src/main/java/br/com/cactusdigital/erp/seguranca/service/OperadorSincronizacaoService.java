package br.com.cactusdigital.erp.seguranca.service;

import br.com.cactusdigital.erp.seguranca.dto.OperadorSincronizacaoResponse;
import br.com.cactusdigital.erp.seguranca.entity.Papel;
import br.com.cactusdigital.erp.seguranca.entity.Usuario;
import br.com.cactusdigital.erp.seguranca.entity.UsuarioPapelEmpresa;
import br.com.cactusdigital.erp.seguranca.repository.UsuarioPapelEmpresaRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Composição pro pull de operador do {@code sincronizacao-service} (Fase 6) — só
 * dados deste próprio serviço (Usuario/Papel/UsuarioPapelEmpresa), sem chamada
 * cross-serviço: o payload de {@code PDV_OPERADOR} não precisa de nada do
 * {@code Colaborador} (nome etc.), que já sincroniza separadamente. Ver Javadoc de
 * {@link OperadorSincronizacaoResponse}.
 */
@Service
@RequiredArgsConstructor
public class OperadorSincronizacaoService {

    /**
     * Precedência de papel → nível de autorização do PDV (G=Gerente | S=Supervisor |
     * O=Operador | E=Entregador). Um usuário pode acumular papéis na mesma empresa (o
     * RBAC do ERP é N:N, mais flexível que o campo fixo do PDV) — quando isso acontece,
     * prevalece o de maior precedência. {@code ADMIN} não é nível do PDV (é exclusivo do
     * ERP, ver CLAUDE.md) — decisão: mapeia pra 'G', mesmo nível de um Gerente, já que
     * ADMIN é superconjunto de permissões. Papel sem mapeamento conhecido é ignorado
     * (usuário não vira operador de PDV a partir dele).
     */
    private static final Map<String, String> NIVEL_POR_PAPEL = Map.of(
            "ADMIN", "G",
            "GERENTE", "G",
            "SUPERVISOR", "S",
            "OPERADOR", "O",
            "ENTREGADOR", "E");

    private static final List<String> PRECEDENCIA = List.of("G", "S", "O", "E");

    private final UsuarioPapelEmpresaRepository usuarioPapelEmpresaRepository;

    @Transactional(readOnly = true)
    public List<OperadorSincronizacaoResponse> listarPorEmpresa(Long empresaId) {
        List<UsuarioPapelEmpresa> vinculos = usuarioPapelEmpresaRepository.findByEmpresaId(empresaId);

        Map<Usuario, List<UsuarioPapelEmpresa>> porUsuario = vinculos.stream()
                .collect(Collectors.groupingBy(UsuarioPapelEmpresa::getUsuario));

        return porUsuario.entrySet().stream()
                .map(entry -> paraResponse(entry.getKey(), entry.getValue()))
                .filter(response -> response != null)
                .toList();
    }

    private OperadorSincronizacaoResponse paraResponse(Usuario usuario, List<UsuarioPapelEmpresa> vinculos) {
        // Sem colaborador vinculado não dá pra virar PDV_OPERADOR — fenix.sql/BANCO_PDV.md
        // exigem ID_COLABORADOR; aqui é opcional (contas de sistema/suporte), então essas
        // simplesmente não sincronizam.
        if (usuario.getColaboradorId() == null) {
            return null;
        }
        String nivel = vinculos.stream()
                .map(UsuarioPapelEmpresa::getPapel)
                .map(Papel::getNome)
                .map(NIVEL_POR_PAPEL::get)
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(PRECEDENCIA::indexOf))
                .orElse(null);
        if (nivel == null) {
            return null;
        }
        return new OperadorSincronizacaoResponse(
                usuario.getId(), usuario.getColaboradorId(), usuario.getLogin(), usuario.getSenhaHash(),
                nivel, usuario.isAtivo(), usuario.isBloqueado());
    }
}
