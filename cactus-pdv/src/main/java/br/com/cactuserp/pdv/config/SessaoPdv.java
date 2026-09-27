package br.com.cactuserp.pdv.config;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvMovimento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.StatusMovimento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Mantém o estado da sessão atual do PDV.
 *
 * <p>Singleton — uma única instância para toda a aplicação.
 *
 * <h3>Conceitos importantes</h3>
 * <ul>
 *   <li><b>Sessão</b>: operador autenticado e usando o sistema.</li>
 *   <li><b>Movimento</b>: caixa fiscalmente aberto, capaz de receber vendas.</li>
 * </ul>
 *
 * <p>Os dois conceitos são INDEPENDENTES: o operador pode ter sessão ativa
 * sem ter movimento aberto (modo restrito — só consultas). E ao fechar
 * movimento, a sessão continua até o operador fazer logoff explícito.
 *
 * <p>Mantemos também o {@link PdvOperador} numa referência própria para
 * preservá-lo mesmo após {@link #encerrarMovimento()}.
 */
@Component
public class SessaoPdv {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessaoPdv.class);

    private PdvMovimento movimentoAtivo;
    private PdvOperador operadorLogado;
    private PdvVendaCabecalho cupomAtivo;

    // ====================================================================
    //  CICLO DE VIDA DA SESSÃO
    // ====================================================================

    /**
     * Inicia a sessão com um movimento recém-aberto.
     * Chamado pelo LoginController após autenticação + abertura de movimento.
     */
    public void iniciarSessao(PdvMovimento movimento) {
        if (movimento == null) {
            throw new IllegalArgumentException("Movimento não pode ser nulo.");
        }
        this.movimentoAtivo = movimento;
        this.operadorLogado = movimento.getOperador();
        this.cupomAtivo = null;
        LOGGER.info("Sessão iniciada: movimento ID={}, operador={}, caixa={}",
                movimento.getId(),
                movimento.getOperador().getColaborador().getNome(),
                movimento.getCaixa().getNome());
    }

    /**
     * Inicia a sessão APENAS com o operador, sem movimento associado.
     *
     * <p>Usado quando o operador faz login mas não há movimento ativo no
     * caixa — entra em "modo restrito" (só consultas). Mais tarde, se ele
     * decidir vender, abre um movimento via Menu Principal.
     */
    public void iniciarSessaoApenasOperador(PdvOperador operador) {
        if (operador == null) {
            throw new IllegalArgumentException("Operador não pode ser nulo.");
        }
        this.movimentoAtivo = null;
        this.operadorLogado = operador;
        this.cupomAtivo = null;
        LOGGER.info("Sessão iniciada em MODO RESTRITO: operador={}",
                operador.getColaborador().getNome());
    }

    /**
     * Atualiza o movimento após mudanças (suprimento, sangria, troca de
     * operador, fechamento, etc).
     */
    public void atualizarMovimento(PdvMovimento movimento) {
        if (movimento == null) {
            throw new IllegalArgumentException("Movimento não pode ser nulo.");
        }
        this.movimentoAtivo = movimento;
    }

    /**
     * Encerra APENAS o movimento — preserva o operador logado.
     * <p>Usado após Fechar Movimento (Z): o operador continua no sistema
     * em "modo restrito" (só consultas), até dar logoff explícito.
     */
    public void encerrarMovimento() {
        if (movimentoAtivo != null) {
            LOGGER.info("Movimento encerrado: ID={}. Operador {} permanece logado.",
                    movimentoAtivo.getId(),
                    operadorLogado != null
                            ? operadorLogado.getColaborador().getNome()
                            : "—");
        }
        this.movimentoAtivo = null;
        this.cupomAtivo = null;
        // operadorLogado preservado
    }

    /**
     * Encerra a sessão completa — operador + movimento.
     * <p>Usado pelo botão SAIR do menu principal (logoff total).
     */
    public void encerrarSessao() {
        if (movimentoAtivo != null) {
            LOGGER.info("Sessão encerrada com movimento aberto ID={}", movimentoAtivo.getId());
        }
        if (operadorLogado != null) {
            LOGGER.info("Logoff do operador {}", operadorLogado.getColaborador().getNome());
        }
        this.movimentoAtivo = null;
        this.operadorLogado = null;
        this.cupomAtivo = null;
    }

    // ====================================================================
    //  Cupom (venda) ativo
    // ====================================================================

    public void setCupomAtivo(PdvVendaCabecalho cupom) {
        this.cupomAtivo = cupom;
        if (cupom != null) {
            LOGGER.debug("Cupom ativo setado: ID={}", cupom.getId());
        }
    }

    public PdvVendaCabecalho getCupomAtivoOuNulo() {
        return cupomAtivo;
    }

    public boolean temCupomAtivo() {
        return cupomAtivo != null;
    }

    public void limparCupom() {
        if (cupomAtivo != null) {
            LOGGER.debug("Cupom ativo limpo: era ID={}", cupomAtivo.getId());
        }
        this.cupomAtivo = null;
    }

    // ====================================================================
    //  Consultas de estado
    // ====================================================================

    /** Indica se há operador autenticado (com ou sem movimento). */
    public boolean temOperadorLogado() {
        return operadorLogado != null;
    }

    public boolean temMovimentoAtivo() {
        return movimentoAtivo != null;
    }

    public boolean movimentoEstaAberto() {
        return movimentoAtivo != null
                && movimentoAtivo.getStatusMovimento() == StatusMovimento.A;
    }

    public boolean movimentoEstaBloqueadoTemporariamente() {
        return movimentoAtivo != null
                && movimentoAtivo.getStatusMovimento() == StatusMovimento.T;
    }

    /**
     * Indica se o sistema está em "modo restrito": operador logado mas
     * sem movimento ativo. Apenas consultas devem ser permitidas.
     */
    public boolean estaEmModoRestrito() {
        return temOperadorLogado() && !movimentoEstaAberto();
    }

    public boolean ehOperadorAtual(PdvOperador operador) {
        if (operador == null) return false;
        PdvOperador atual = getOperadorAtual();
        return atual != null && atual.getId().equals(operador.getId());
    }

    // ====================================================================
    //  Getters do movimento
    // ====================================================================

    public PdvMovimento getMovimentoAtivo() {
        if (movimentoAtivo == null) {
            throw new IllegalStateException(
                    "Nenhum movimento de caixa ativo. Inicie o movimento antes de operar o PDV.");
        }
        return movimentoAtivo;
    }

    public PdvMovimento getMovimentoAtivoOuNulo() {
        return movimentoAtivo;
    }

    /**
     * Retorna o operador atual.
     * <p>Tenta primeiro pelo movimento (caso clássico); se não houver
     * movimento, retorna o operador da sessão (modo restrito).
     */
    public PdvOperador getOperadorAtual() {
        if (movimentoAtivo != null) {
            return movimentoAtivo.getOperador();
        }
        return operadorLogado;
    }

    public PdvCaixa getCaixaAtual() {
        return movimentoAtivo != null ? movimentoAtivo.getCaixa() : null;
    }

    public Long getIdMovimentoAtivo() {
        return movimentoAtivo != null ? movimentoAtivo.getId() : null;
    }

    public String getNomeOperadorAtual() {
        PdvOperador op = getOperadorAtual();
        if (op == null) return "Sem operador";
        return op.getColaborador().getNome();
    }

    public String getNomeCaixaAtual() {
        if (movimentoAtivo == null) return "";
        return movimentoAtivo.getCaixa().getNome();
    }
}