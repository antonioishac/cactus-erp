package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.financeiro.entity.ContaBancaria;
import br.com.cactusdigital.erp.financeiro.entity.TipoContaBancaria;
import br.com.cactusdigital.erp.financeiro.repository.ContaBancariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Conta {@code CAIXA_INTERNO} de um terminal do PDV, identificada por
 * <b>empresa + dispositivo + terminal</b>: o id do terminal é local de cada banco de PDV,
 * então duas instalações na mesma loja podem ter um "Caixa 1" cada — sem o dispositivo
 * na chave, o dinheiro das duas cairia na mesma conta.
 *
 * <p>Criada no primeiro envio do terminal, segura contra corrida (abertura e primeira
 * venda de um terminal novo chegam juntas pela fila do financeiro e pela chamada do
 * vendas-service):
 * <ol>
 *   <li>busca normal (sem lock: um {@code FOR UPDATE} numa chave inexistente faz gap
 *       lock no InnoDB e serializaria à toa);</li>
 *   <li>se não existe, {@code INSERT ... ON DUPLICATE KEY UPDATE} nativo na MESMA
 *       transação — a chave única {@code (empresa, dispositivo, terminal)} resolve a
 *       corrida dentro do MySQL: quem chega depois espera o primeiro e não duplica;</li>
 *   <li>relê com leitura travada (leitura ATUAL, enxerga a linha que outra transação
 *       commitou depois da primeira leitura desta).</li>
 * </ol>
 * <b>Não usa {@code REQUIRES_NEW}</b> de propósito: a primeira versão criava a conta numa
 * transação separada e, com 20 envios simultâneos, cada requisição segurava uma conexão
 * esperando uma segunda — o pool (10) esgotou e todas falharam por timeout (achado no
 * teste de carga, 2026-10-25).
 */
@Service
@RequiredArgsConstructor
public class ContaTerminalPdvService {

    private final ContaBancariaRepository repository;

    @Transactional(propagation = Propagation.MANDATORY)
    public ContaBancaria obter(Long empresaId, String dispositivoId, Long pdvCaixaId, String nomeTerminal) {
        return repository.findByEmpresaIdAndPdvDispositivoIdAndPdvCaixaId(empresaId, dispositivoId, pdvCaixaId)
                .orElseGet(() -> {
                    String nome = "Caixa PDV " + dispositivoId + " / " + (nomeTerminal != null ? nomeTerminal : "#" + pdvCaixaId);
                    repository.criarContaTerminalSeAusente(empresaId, dispositivoId, pdvCaixaId,
                            nome.length() <= 100 ? nome : nome.substring(0, 100), TipoContaBancaria.CAIXA_INTERNO.name());
                    return repository.buscarTerminalTravado(empresaId, dispositivoId, pdvCaixaId)
                            .orElseThrow(() -> new IllegalStateException("Conta do terminal " + dispositivoId + "/" + pdvCaixaId
                                    + " da empresa #" + empresaId + " não encontrada depois de criada"));
                });
    }
}
