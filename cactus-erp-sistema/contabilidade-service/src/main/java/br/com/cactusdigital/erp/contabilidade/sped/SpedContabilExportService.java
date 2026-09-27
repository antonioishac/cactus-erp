package br.com.cactusdigital.erp.contabilidade.sped;

import br.com.cactusdigital.erp.common.sped.SpedLineBuilder;
import br.com.cactusdigital.erp.contabilidade.entity.CentroCusto;
import br.com.cactusdigital.erp.contabilidade.entity.LancamentoContabil;
import br.com.cactusdigital.erp.contabilidade.entity.NaturezaConta;
import br.com.cactusdigital.erp.contabilidade.entity.PartidaContabil;
import br.com.cactusdigital.erp.contabilidade.entity.PlanoConta;
import br.com.cactusdigital.erp.contabilidade.repository.CentroCustoRepository;
import br.com.cactusdigital.erp.contabilidade.repository.LancamentoContabilRepository;
import br.com.cactusdigital.erp.contabilidade.repository.PartidaContabilRepository;
import br.com.cactusdigital.erp.contabilidade.repository.PlanoContaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Export do SPED Contábil (ECD — Escrituração Contábil Digital), Bloco I, a partir do
 * livro contábil já lançado neste serviço.
 * <p>
 * <b>Transparência sobre o nível de confiança deste builder</b> (mesmo padrão de
 * honestidade já usado pra ACBr/OFX/CNAB 240 no resto do monorepo): a estrutura de
 * blocos/registros (0000 → I050 → I100 → I150/I155 → I200/I250 → 9999) e o formato geral
 * (arquivo texto delimitado por {@code |}) foram confirmados por pesquisa em múltiplas
 * fontes secundárias — ver ANÁLISE: MÓDULO CONTÁBIL E FISCAL no CLAUDE.md raiz — mas o
 * layout campo-a-campo (posição exata, domínio de valores, tamanho) de cada registro
 * NUNCA foi validado contra o Programa Validador e Assinador (PVA) oficial da Receita
 * nem contra o Manual de Orientação da ECD na íntegra. Duas simplificações deliberadas,
 * registradas explicitamente:
 * <ul>
 *   <li>O registro {@code 0000} não resolve dados cadastrais da empresa (razão social/
 *   CNPJ/UF) — este serviço não tem client cross-serviço pro {@code cadastro-base-service}
 *   nesta fase (Resource Server puro, ver Javadoc do módulo no {@code pom.xml});</li>
 *   <li>O campo de natureza da conta no {@code I050} ({@code COD_NAT}) usa o NOME do
 *   {@link NaturezaConta}/{@code TipoContaContabil} (ex: {@code "ATIVO"}), não o código
 *   numérico oficial (01-09) da tabela do Ato COTEPE — esse código não foi confirmado
 *   nesta sessão e inventá-lo seria exatamente o tipo de "alucinação" fiscal que este
 *   projeto se recusa a fazer (ver CLAUDE.md raiz, instrução "não podemos admitir erros").
 * </ul>
 * Suficiente pra provar a arquitetura do export (dado real do livro contábil, estrutura
 * de blocos correta) — não suficiente pra submeter a um ambiente de produção da Receita
 * sem uma rodada de validação dedicada.
 */
@Service
@RequiredArgsConstructor
public class SpedContabilExportService {

    private final PlanoContaRepository planoContaRepository;
    private final CentroCustoRepository centroCustoRepository;
    private final PartidaContabilRepository partidaContabilRepository;
    private final LancamentoContabilRepository lancamentoContabilRepository;

    @Transactional(readOnly = true)
    public String gerarEcd(Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        List<String> linhas = new ArrayList<>();
        linhas.add(registro0000(dataInicio, dataFim));

        List<PlanoConta> contas = planoContaRepository.findByEmpresaIdOrderByCodigoAsc(empresaId);
        contas.stream().map(this::registroI050).forEach(linhas::add);

        centroCustoRepository.findByEmpresaIdOrderByCodigoAsc(empresaId).stream()
                .map(SpedContabilExportService::registroI100)
                .forEach(linhas::add);

        adicionarSaldosPeriodicos(linhas, empresaId, contas, dataInicio, dataFim);
        adicionarLancamentos(linhas, empresaId, dataInicio, dataFim);

        linhas.add(registro9999(linhas.size() + 1));
        return String.join("\r\n", linhas) + "\r\n";
    }

    private void adicionarSaldosPeriodicos(List<String> linhas, Long empresaId, List<PlanoConta> contas, LocalDate dataInicio, LocalDate dataFim) {
        linhas.add(registroI150(dataInicio, dataFim));

        Map<Long, PlanoConta> contasPorId = contas.stream().collect(Collectors.toMap(PlanoConta::getId, Function.identity()));
        Map<Long, BigDecimal[]> saldosAntes = comoMapaDebitoCredito(partidaContabilRepository.somarDebitoCreditoAntesDe(empresaId, dataInicio));
        Map<Long, BigDecimal[]> saldosPeriodo = comoMapaDebitoCredito(partidaContabilRepository.somarDebitoCredito(empresaId, dataInicio, dataFim));

        saldosPeriodo.keySet().stream()
                .sorted(Comparator.comparing(contaId -> contasPorId.get(contaId).getCodigo()))
                .map(contaId -> registroI155(
                        contasPorId.get(contaId),
                        saldosAntes.getOrDefault(contaId, new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO}),
                        saldosPeriodo.get(contaId)))
                .forEach(linhas::add);
    }

    private void adicionarLancamentos(List<String> linhas, Long empresaId, LocalDate dataInicio, LocalDate dataFim) {
        for (LancamentoContabil lancamento : lancamentoContabilRepository.buscarLancadosNoPeriodoComPartidas(empresaId, dataInicio, dataFim)) {
            linhas.add(registroI200(lancamento));
            lancamento.getPartidas().stream()
                    .map(partida -> registroI250(lancamento, partida))
                    .forEach(linhas::add);
        }
    }

    private static Map<Long, BigDecimal[]> comoMapaDebitoCredito(List<Object[]> somas) {
        return somas.stream().collect(Collectors.toMap(
                linha -> (Long) linha[0],
                linha -> new BigDecimal[] {(BigDecimal) linha[1], (BigDecimal) linha[2]}));
    }

    private static String registro0000(LocalDate dataInicio, LocalDate dataFim) {
        return new SpedLineBuilder()
                .campo("0000")
                .campo("LECD")
                .data(dataInicio)
                .data(dataFim)
                .build();
    }

    private String registroI050(PlanoConta conta) {
        return new SpedLineBuilder()
                .campo("I050")
                .campo(conta.getTipo().name() + "/" + conta.getNatureza().name()) // ver disclaimer da classe: não é o COD_NAT oficial (01-09)
                .campo(conta.isContaAnalitica() ? "A" : "S")
                .campo(nivelHierarquia(conta))
                .campo(conta.getCodigo())
                .campo(conta.getContaPai() != null ? conta.getContaPai().getCodigo() : "")
                .campo(conta.getNome())
                .build();
    }

    private static String registroI100(CentroCusto centroCusto) {
        return new SpedLineBuilder()
                .campo("I100")
                .campo(centroCusto.getCodigo())
                .campo(centroCusto.getNome())
                .build();
    }

    private static String registroI150(LocalDate dataInicio, LocalDate dataFim) {
        return new SpedLineBuilder().campo("I150").data(dataInicio).data(dataFim).build();
    }

    /**
     * BUG REAL encontrado e corrigido na validação ao vivo (2026-09-13): a primeira versão
     * calculava o indicador D/C a partir do saldo já ajustado pela {@link NaturezaConta}
     * (a mesma convenção usada no Balancete/Razão pra exibição — "saldo positivo = a favor
     * da natureza esperada da conta") — mas o campo {@code IND_DC} da ECD não é isso: é a
     * comparação CRUA débito−crédito, independente da natureza da conta (débito maior =
     * saldo devedor/{@code D}, crédito maior = saldo credor/{@code C}). Pra conta
     * {@code DEVEDORA} os dois cálculos coincidem (mesmo sinal), então o bug só se
     * manifestava em conta {@code CREDORA} (Passivo/PL/Receita) — confirmado ao vivo:
     * "Fornecedores a Pagar" com saldo credor real de R$250,50 saía com indicador
     * {@code D} (errado) antes deste fix.
     */
    private static String registroI155(PlanoConta conta, BigDecimal[] saldoAntes, BigDecimal[] saldoPeriodo) {
        BigDecimal saldoInicialBruto = saldoAntes[0].subtract(saldoAntes[1]);
        BigDecimal totalDebitoPeriodo = saldoPeriodo[0];
        BigDecimal totalCreditoPeriodo = saldoPeriodo[1];
        BigDecimal saldoFinalBruto = saldoInicialBruto.add(totalDebitoPeriodo).subtract(totalCreditoPeriodo);

        return new SpedLineBuilder()
                .campo("I155")
                .campo(conta.getCodigo())
                .valor(saldoInicialBruto.abs())
                .campo(indicadorDebitoCredito(saldoInicialBruto))
                .valor(totalDebitoPeriodo)
                .valor(totalCreditoPeriodo)
                .valor(saldoFinalBruto.abs())
                .campo(indicadorDebitoCredito(saldoFinalBruto))
                .build();
    }

    private static String registroI200(LancamentoContabil lancamento) {
        BigDecimal valorTotal = lancamento.getPartidas().stream()
                .filter(partida -> partida.getTipoPartida() == br.com.cactusdigital.erp.contabilidade.entity.TipoPartida.DEBITO)
                .map(PartidaContabil::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SpedLineBuilder()
                .campo("I200")
                .campo(lancamento.getId())
                .data(lancamento.getDataLancamento())
                .valor(valorTotal)
                .build();
    }

    private static String registroI250(LancamentoContabil lancamento, PartidaContabil partida) {
        return new SpedLineBuilder()
                .campo("I250")
                .campo(partida.getConta().getCodigo())
                .campo(partida.getCentroCusto() != null ? partida.getCentroCusto().getCodigo() : "")
                .valor(partida.getValor())
                .campo(partida.getTipoPartida() == br.com.cactusdigital.erp.contabilidade.entity.TipoPartida.DEBITO ? "D" : "C")
                .campo(lancamento.getId())
                .campo(partida.getHistoricoComplementar() != null ? partida.getHistoricoComplementar() : lancamento.getHistorico())
                .build();
    }

    private static String registro9999(int quantidadeLinhas) {
        return new SpedLineBuilder().campo("9999").campo(quantidadeLinhas).build();
    }

    private static int nivelHierarquia(PlanoConta conta) {
        int nivel = 1;
        PlanoConta atual = conta.getContaPai();
        while (atual != null) {
            nivel++;
            atual = atual.getContaPai();
        }
        return nivel;
    }

    /** {@code saldoBruto} é sempre débito−crédito CRU, sem ajuste de natureza (ver Javadoc de {@link #registroI155}). */
    private static String indicadorDebitoCredito(BigDecimal saldoBruto) {
        return saldoBruto.signum() < 0 ? "C" : "D";
    }
}
