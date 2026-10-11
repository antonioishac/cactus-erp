package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.financeiro.dto.TipoTituloRelatorio;
import br.com.cactusdigital.erp.financeiro.dto.TituloEmAbertoResponse;
import br.com.cactusdigital.erp.financeiro.entity.SituacaoTitulo;
import br.com.cactusdigital.erp.financeiro.mapper.FinanceiroMapper;
import br.com.cactusdigital.erp.financeiro.repository.ContaPagarRepository;
import br.com.cactusdigital.erp.financeiro.repository.ContaReceberRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Relatório de títulos em aberto (ABERTA/PARCIAL) por vencimento — Contas a Receber ou
 * Contas a Pagar, conforme {@link TipoTituloRelatorio}. Calculado sob demanda, sem
 * snapshot (mesmo critério de {@link FluxoCaixaService}).
 * <p>
 * Juros/multa/dias de atraso usam os MESMOS métodos de cálculo de {@link FinanceiroMapper}
 * das consultas de título (override do título, senão default de
 * {@code ConfiguracaoFinanceiroEmpresa}) — o relatório nunca calcula diferente da tela.
 * Não passa por {@code FinanceiroMapper#toResponse} de propósito: ele mapeia também a
 * coleção lazy {@code baixas}, o que dispararia 1 query por título (N+1) sem que o
 * relatório precise das baixas. Os percentuais da empresa são resolvidos uma vez só por
 * chamada (o relatório é sempre de uma empresa), não uma vez por título.
 */
@Service
@RequiredArgsConstructor
public class TitulosEmAbertoService {

    private static final List<SituacaoTitulo> SITUACOES_EM_ABERTO = List.of(SituacaoTitulo.ABERTA, SituacaoTitulo.PARCIAL);
    /** Limites do tipo DATE do MySQL — usados quando o filtro de vencimento é omitido. */
    private static final LocalDate VENCIMENTO_MINIMO = LocalDate.of(1000, 1, 1);
    private static final LocalDate VENCIMENTO_MAXIMO = LocalDate.of(9999, 12, 31);

    private final ContaReceberRepository contaReceberRepository;
    private final ContaPagarRepository contaPagarRepository;
    private final ConfiguracaoFinanceiroEmpresaService configuracaoService;
    private final FinanceiroMapper mapper;

    @Transactional(readOnly = true)
    public List<TituloEmAbertoResponse> listar(TipoTituloRelatorio tipo, Long empresaId,
            LocalDate dataInicio, LocalDate dataFim, Long pessoaId) {
        LocalDate inicio = dataInicio != null ? dataInicio : VENCIMENTO_MINIMO;
        LocalDate fim = dataFim != null ? dataFim : VENCIMENTO_MAXIMO;
        BigDecimal multaEmpresa = configuracaoService.percentualMultaDaEmpresa(empresaId);
        BigDecimal jurosDiaEmpresa = configuracaoService.percentualJurosDiaDaEmpresa(empresaId);

        List<TituloEmAbertoResponse> titulos = switch (tipo) {
            case RECEBER -> contaReceberRepository
                    .findByEmpresaIdAndDataVencimentoBetweenAndSituacaoIn(empresaId, inicio, fim, SITUACOES_EM_ABERTO).stream()
                    .filter(conta -> pessoaId == null || pessoaId.equals(conta.getClienteId()))
                    .map(conta -> linha(conta.getId(), conta.getClienteId(), conta.getNumeroDocumento(), conta.getDescricao(),
                            conta.getDataEmissao(), conta.getDataVencimento(), conta.getSituacao(),
                            conta.getValorOriginal(), conta.getValorPago(), conta.getValorSaldo(),
                            valorOuDefault(conta.getPercentualMultaAtraso(), multaEmpresa),
                            valorOuDefault(conta.getPercentualJurosDiaAtraso(), jurosDiaEmpresa)))
                    .toList();
            case PAGAR -> contaPagarRepository
                    .findByEmpresaIdAndDataVencimentoBetweenAndSituacaoIn(empresaId, inicio, fim, SITUACOES_EM_ABERTO).stream()
                    .filter(conta -> pessoaId == null || pessoaId.equals(conta.getFornecedorId()))
                    .map(conta -> linha(conta.getId(), conta.getFornecedorId(), conta.getNumeroDocumento(), conta.getDescricao(),
                            conta.getDataEmissao(), conta.getDataVencimento(), conta.getSituacao(),
                            conta.getValorOriginal(), conta.getValorPago(), conta.getValorSaldo(),
                            valorOuDefault(conta.getPercentualMultaAtraso(), multaEmpresa),
                            valorOuDefault(conta.getPercentualJurosDiaAtraso(), jurosDiaEmpresa)))
                    .toList();
        };

        return titulos.stream()
                .sorted(Comparator.comparing(TituloEmAbertoResponse::dataVencimento)
                        .thenComparing(TituloEmAbertoResponse::tituloId))
                .toList();
    }

    private BigDecimal valorOuDefault(BigDecimal overrideDoTitulo, BigDecimal defaultDaEmpresa) {
        return overrideDoTitulo != null ? overrideDoTitulo : defaultDaEmpresa;
    }

    private TituloEmAbertoResponse linha(Long tituloId, Long pessoaId, String numeroDocumento, String descricao,
            LocalDate dataEmissao, LocalDate dataVencimento, SituacaoTitulo situacao,
            BigDecimal valorOriginal, BigDecimal valorPago, BigDecimal valorSaldo,
            BigDecimal percentualMulta, BigDecimal percentualJurosDia) {
        BigDecimal valorMulta = mapper.valorMulta(valorSaldo, dataVencimento, situacao, percentualMulta);
        BigDecimal valorJuros = mapper.valorJuros(valorSaldo, dataVencimento, situacao, percentualJurosDia);
        return new TituloEmAbertoResponse(tituloId, pessoaId, numeroDocumento, descricao, dataEmissao, dataVencimento,
                situacao.name(), mapper.diasAtraso(dataVencimento, situacao),
                valorOriginal, valorPago, valorSaldo, valorMulta, valorJuros,
                valorSaldo.add(valorMulta).add(valorJuros));
    }
}
