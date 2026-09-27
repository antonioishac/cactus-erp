package br.com.cactusdigital.erp.financeiro.service;

import br.com.cactusdigital.erp.common.exception.BusinessException;
import br.com.cactusdigital.erp.common.exception.ConflictException;
import br.com.cactusdigital.erp.common.exception.NotFoundException;
import br.com.cactusdigital.erp.financeiro.dto.ConciliarAutomaticoResponse;
import br.com.cactusdigital.erp.financeiro.dto.ExtratoBancarioImportacaoResponse;
import br.com.cactusdigital.erp.financeiro.dto.ExtratoBancarioItemResponse;
import br.com.cactusdigital.erp.financeiro.dto.GerarAjusteConciliacaoRequest;
import br.com.cactusdigital.erp.financeiro.dto.MovimentoCaixaBancoResponse;
import br.com.cactusdigital.erp.financeiro.dto.PendenciasConciliacaoResponse;
import br.com.cactusdigital.erp.financeiro.entity.ExtratoBancarioImportacao;
import br.com.cactusdigital.erp.financeiro.entity.ExtratoBancarioItem;
import br.com.cactusdigital.erp.financeiro.entity.MovimentoCaixaBanco;
import br.com.cactusdigital.erp.financeiro.entity.OrigemMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.entity.TipoMovimentoCaixa;
import br.com.cactusdigital.erp.financeiro.exception.FinanceiroErrorCode;
import br.com.cactusdigital.erp.financeiro.mapper.CaixaBancoMapper;
import br.com.cactusdigital.erp.financeiro.ofx.OfxExtrato;
import br.com.cactusdigital.erp.financeiro.ofx.OfxParseException;
import br.com.cactusdigital.erp.financeiro.ofx.OfxParser;
import br.com.cactusdigital.erp.financeiro.ofx.OfxTransacao;
import br.com.cactusdigital.erp.financeiro.repository.ContaBancariaRepository;
import br.com.cactusdigital.erp.financeiro.repository.ExtratoBancarioImportacaoRepository;
import br.com.cactusdigital.erp.financeiro.repository.ExtratoBancarioItemRepository;
import br.com.cactusdigital.erp.financeiro.repository.MovimentoCaixaBancoRepository;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Conciliação Bancária — import de extrato via arquivo OFX (upload manual, decisão
 * confirmada com o usuário: sem integração com API/Open Finance de banco parceiro, que
 * não temos) + matching automático/manual contra {@link MovimentoCaixaBanco}. Ver
 * {@code OfxParser} pro parsing em si.
 */
@Service
@RequiredArgsConstructor
public class ConciliacaoBancariaService {

    /** Tolerância de data no matching automático — a data de "compensação" que o banco
     * registra no extrato frequentemente diverge em 1-3 dias úteis da data que o
     * lançamento interno foi registrado (fim de semana, processamento em lote do banco). */
    private static final long TOLERANCIA_DIAS = 3;

    private final ExtratoBancarioImportacaoRepository importacaoRepository;
    private final ExtratoBancarioItemRepository itemRepository;
    private final MovimentoCaixaBancoRepository movimentoRepository;
    private final ContaBancariaRepository contaBancariaRepository;
    private final MovimentoCaixaBancoService movimentoService;
    private final CaixaBancoMapper mapper;

    @Transactional(readOnly = true)
    public Page<ExtratoBancarioImportacaoResponse> listarImportacoes(Long contaBancariaId, Pageable pageable) {
        Page<ExtratoBancarioImportacao> pagina = contaBancariaId != null
                ? importacaoRepository.findByContaBancariaId(contaBancariaId, pageable)
                : importacaoRepository.findAll(pageable);
        return pagina.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ExtratoBancarioItemResponse> listarItens(Long importacaoId, Pageable pageable) {
        return itemRepository.findByImportacaoId(importacaoId, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PendenciasConciliacaoResponse pendencias(Long contaBancariaId) {
        List<ExtratoBancarioItemResponse> itens = itemRepository.findByContaBancariaIdAndConciliadoFalse(contaBancariaId)
                .stream().map(mapper::toResponse).toList();
        List<MovimentoCaixaBancoResponse> movimentos = movimentoRepository.findByContaBancariaIdAndConciliadoFalse(contaBancariaId)
                .stream().map(mapper::toResponse).toList();
        return new PendenciasConciliacaoResponse(itens, movimentos);
    }

    /**
     * Parseia o OFX e insere 1 {@link ExtratoBancarioItem} por transação — deduplica por
     * {@code fitId} contra tudo que já foi importado antes pra esta conta (reimportar um
     * arquivo sobreposto não duplica linha). Não conciliar automaticamente aqui de
     * propósito — importar e conciliar são ações distintas, o operador decide quando rodar
     * {@link #conciliarAutomatico}.
     */
    @Transactional
    public ExtratoBancarioImportacaoResponse importar(Long contaBancariaId, String nomeArquivo, byte[] conteudo, Long usuarioId) {
        if (!contaBancariaRepository.existsById(contaBancariaId)) {
            throw NotFoundException.of("Conta bancária", contaBancariaId);
        }

        OfxExtrato extrato;
        try {
            extrato = OfxParser.parse(conteudo);
        } catch (OfxParseException e) {
            throw new BusinessException(FinanceiroErrorCode.ARQUIVO_OFX_INVALIDO, e.getMessage());
        }

        Set<String> fitIdsDoArquivo = extrato.transacoes().stream()
                .map(OfxTransacao::fitId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> fitIdsExistentes = fitIdsDoArquivo.isEmpty()
                ? Set.of() : itemRepository.buscarFitIdsExistentes(contaBancariaId, fitIdsDoArquivo);

        ExtratoBancarioImportacao importacao = importacaoRepository.save(ExtratoBancarioImportacao.builder()
                .contaBancariaId(contaBancariaId)
                .nomeArquivo(nomeArquivo)
                .usuarioId(usuarioId)
                .build());

        int inseridos = 0;
        int duplicados = 0;
        for (OfxTransacao transacao : extrato.transacoes()) {
            if (transacao.fitId() != null && fitIdsExistentes.contains(transacao.fitId())) {
                duplicados++;
                continue;
            }
            itemRepository.save(ExtratoBancarioItem.builder()
                    .importacaoId(importacao.getId())
                    .contaBancariaId(contaBancariaId)
                    .dataMovimento(transacao.data())
                    .historico(transacao.memo())
                    .tipoOfx(transacao.tipo())
                    .valor(transacao.valor())
                    .fitId(transacao.fitId())
                    .build());
            inseridos++;
        }
        importacao.setQuantidadeItens(inseridos);
        importacao.setQuantidadeDuplicados(duplicados);

        return mapper.toResponse(importacao);
    }

    /**
     * Casa cada item não conciliado com um {@code MovimentoCaixaBanco} não conciliado da
     * mesma conta — mesma direção (crédito/débito), mesmo valor absoluto exato, data
     * dentro de {@link #TOLERANCIA_DIAS}. Só concilia quando há EXATAMENTE 1 candidato —
     * 0 ou 2+ candidatos ficam pendentes pra decisão manual (evita casar errado por
     * ambiguidade, ex: duas baixas do mesmo valor no mesmo dia).
     */
    @Transactional
    public ConciliarAutomaticoResponse conciliarAutomatico(Long contaBancariaId) {
        List<ExtratoBancarioItem> itens = itemRepository.findByContaBancariaIdAndConciliadoFalse(contaBancariaId);
        List<MovimentoCaixaBanco> movimentos = movimentoRepository.findByContaBancariaIdAndConciliadoFalse(contaBancariaId);

        int conciliados = 0;
        for (ExtratoBancarioItem item : itens) {
            boolean itemCredito = item.getValor().compareTo(BigDecimal.ZERO) > 0;
            BigDecimal valorAbsoluto = item.getValor().abs();

            List<MovimentoCaixaBanco> candidatos = movimentos.stream()
                    .filter(m -> !m.isConciliado())
                    .filter(m -> movimentoService.isCredito(m.getTipoMovimento()) == itemCredito)
                    .filter(m -> m.getValor().compareTo(valorAbsoluto) == 0)
                    .filter(m -> Math.abs(ChronoUnit.DAYS.between(m.getDataMovimento(), item.getDataMovimento())) <= TOLERANCIA_DIAS)
                    .toList();

            if (candidatos.size() == 1) {
                MovimentoCaixaBanco movimento = candidatos.get(0);
                item.setConciliado(true);
                item.setMovimentoCaixaBancoId(movimento.getId());
                movimento.setConciliado(true);
                itemRepository.save(item);
                movimentoRepository.save(movimento);
                conciliados++;
            }
        }
        return new ConciliarAutomaticoResponse(conciliados, itens.size() - conciliados);
    }

    /** Conciliação manual — override do operador, não valida valor/data (ele já está
     * olhando os dois lados na tela e decidiu que batem). */
    @Transactional
    public ExtratoBancarioItemResponse conciliarManual(Long itemId, Long movimentoCaixaBancoId) {
        ExtratoBancarioItem item = buscarItem(itemId);
        if (item.isConciliado()) {
            throw new ConflictException(FinanceiroErrorCode.ITEM_EXTRATO_JA_CONCILIADO);
        }
        MovimentoCaixaBanco movimento = movimentoRepository.findById(movimentoCaixaBancoId)
                .orElseThrow(() -> NotFoundException.of("Movimento de caixa/banco", movimentoCaixaBancoId));
        if (movimento.isConciliado()) {
            throw new ConflictException(FinanceiroErrorCode.MOVIMENTO_JA_CONCILIADO);
        }

        item.setConciliado(true);
        item.setMovimentoCaixaBancoId(movimento.getId());
        movimento.setConciliado(true);
        movimentoRepository.save(movimento);

        return mapper.toResponse(item);
    }

    @Transactional
    public ExtratoBancarioItemResponse desconciliar(Long itemId) {
        ExtratoBancarioItem item = buscarItem(itemId);
        if (!item.isConciliado()) {
            throw new ConflictException(FinanceiroErrorCode.ITEM_EXTRATO_NAO_CONCILIADO);
        }
        if (item.getMovimentoCaixaBancoId() != null) {
            movimentoRepository.findById(item.getMovimentoCaixaBancoId()).ifPresent(movimento -> {
                movimento.setConciliado(false);
                movimentoRepository.save(movimento);
            });
        }
        item.setConciliado(false);
        item.setMovimentoCaixaBancoId(null);
        return mapper.toResponse(item);
    }

    /**
     * Gera um {@code MovimentoCaixaBanco} novo a partir de uma linha do extrato sem
     * lançamento interno correspondente — cenário real: tarifa bancária, IOF, rendimento
     * de conta remunerada, que o banco cobrou/creditou sem nenhum aviso prévio no ERP.
     * Direção (ENTRADA/SAIDA) decidida pelo sinal do {@code valor} do item.
     */
    @Transactional
    public ExtratoBancarioItemResponse gerarAjuste(Long itemId, GerarAjusteConciliacaoRequest request) {
        ExtratoBancarioItem item = buscarItem(itemId);
        if (item.isConciliado()) {
            throw new ConflictException(FinanceiroErrorCode.ITEM_EXTRATO_JA_CONCILIADO);
        }

        boolean credito = item.getValor().compareTo(BigDecimal.ZERO) > 0;
        String historico = request.historico() != null ? request.historico() : item.getHistorico();
        MovimentoCaixaBanco movimento = movimentoService.registrar(
                item.getContaBancariaId(),
                credito ? TipoMovimentoCaixa.ENTRADA : TipoMovimentoCaixa.SAIDA,
                OrigemMovimentoCaixa.CONCILIACAO,
                item.getId(),
                item.getValor().abs(),
                item.getDataMovimento(),
                historico,
                null);
        movimento.setConciliado(true);
        movimentoRepository.save(movimento);

        item.setConciliado(true);
        item.setMovimentoCaixaBancoId(movimento.getId());
        return mapper.toResponse(item);
    }

    private ExtratoBancarioItem buscarItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> NotFoundException.of("Item de extrato bancário", id));
    }
}
