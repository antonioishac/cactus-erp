package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.*;
import br.com.cactuserp.pdv.repository.PdvMovimentoRepository;
import br.com.cactuserp.pdv.repository.PdvOperadorRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LoginService {

    private final PdvOperadorRepository operadorRepository;
    private final PdvMovimentoRepository movimentoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginService(PdvOperadorRepository operadorRepository,
                        PdvMovimentoRepository movimentoRepository,
                        BCryptPasswordEncoder passwordEncoder) {
        this.operadorRepository = operadorRepository;
        this.movimentoRepository = movimentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica um operador por login/senha. Usado tanto para operadores
     * comuns quanto para validar autorização de gerente/supervisor.
     */
    public PdvOperador autenticar(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Informe login e senha.");
        }

        PdvOperador op = operadorRepository.findByLogin(login.trim())
                .orElseThrow(() -> new IllegalArgumentException("Login ou senha inválidos."));

        if (!passwordEncoder.matches(senha, op.getSenha())) {
            throw new IllegalArgumentException("Login ou senha inválidos.");
        }
        return op;
    }

    /**
     * Autentica e exige nível de gerente ou supervisor.
     * Usado em pontos que exigem autorização (abertura, troca de operador, etc).
     */
    public PdvOperador autenticarGerenteSupervisor(String login, String senha) {
        PdvOperador op = autenticar(login, senha);
        NivelAutorizacao nivel = op.getColaborador().getNivelAutorizacao();
        if (nivel == null || !nivel.podeAutorizarMovimento()) {
            throw new IllegalArgumentException(
                    "Usuário não tem permissão. Necessário Gerente ou Supervisor.");
        }
        return op;
    }

    /**
     * Decide o que fazer após o login bem-sucedido, com base no estado
     * do movimento no caixa.
     */
    public ResultadoLogin processarLogin(PdvOperador operador, PdvCaixa caixa) {
        Optional<PdvMovimento> movABerto = movimentoRepository
                .findFirstByCaixaIdAndStatusMovimentoOrderByIdDesc(caixa.getId(), StatusMovimento.A);

        if (movABerto.isPresent()) {
            PdvMovimento m = movABerto.get();
            boolean mesmoOperador = m.getOperador().getId().equals(operador.getId());
            return mesmoOperador
                    ? ResultadoLogin.abrirPdv(m)
                    : ResultadoLogin.trocarOperador(m);
        }

        Optional<PdvMovimento> movTemporario = movimentoRepository
                .findFirstByCaixaIdAndStatusMovimentoOrderByIdDesc(caixa.getId(), StatusMovimento.T);

        if (movTemporario.isPresent()) {
            return ResultadoLogin.retomarMovimento(movTemporario.get());
        }

        // Nenhum movimento → operador entra em modo restrito (só consultas).
        // Para vender, ele abrirá movimento ativamente via F12 → Abrir Movimento.
        return ResultadoLogin.modoRestrito();
    }
}