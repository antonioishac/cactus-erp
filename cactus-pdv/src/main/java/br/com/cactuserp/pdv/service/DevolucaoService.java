package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.EstoqueMovimento;
import br.com.cactuserp.pdv.entity.PdvDevolucao;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.entity.StatusVenda;
import br.com.cactuserp.pdv.entity.TipoMovimentoEstoque;
import br.com.cactuserp.pdv.repository.PdvDevolucaoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Devolução/troca pós-fechamento de item de venda já finalizada.
 *
 * <p>Cobre o lado <b>operacional</b>: valida quantidade, exige autorização
 * gerencial, gera reentrada de estoque via {@link EstoqueMovimentoService}
 * (tipo {@code D}) e grava o registro auditável em {@link PdvDevolucao}.
 *
 * <p><b>Não cobre o lado fiscal nem o financeiro</b>:
 * <ul>
 *   <li>Não emite documento fiscal de devolução — depende da integração
 *       ACBr (ver {@code EmissaoFiscalService}), não validada.</li>
 *   <li>Não decide COMO o valor é devolvido ao cliente (dinheiro da
 *       gaveta via sangria, crédito em loja, estorno de cartão) — isso é
 *       uma decisão operacional da loja, fora do escopo desta classe.
 *       {@link #registrarDevolucao} só registra QUANTO deveria ser
 *       devolvido ({@code valorDevolvido}).</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class DevolucaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevolucaoService.class);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final PdvDevolucaoRepository devolucaoRepository;
    private final PdvVendaDetalheRepository vendaDetalheRepository;
    private final EstoqueMovimentoService estoqueMovimentoService;
    private final DispositivoInfoService dispositivoInfo;

    public DevolucaoService(PdvDevolucaoRepository devolucaoRepository,
                            PdvVendaDetalheRepository vendaDetalheRepository,
                            EstoqueMovimentoService estoqueMovimentoService,
                            DispositivoInfoService dispositivoInfo) {
        this.devolucaoRepository = devolucaoRepository;
        this.vendaDetalheRepository = vendaDetalheRepository;
        this.estoqueMovimentoService = estoqueMovimentoService;
        this.dispositivoInfo = dispositivoInfo;
    }

    /** Quanto do item já foi devolvido anteriormente (soma de devoluções prévias). */
    public BigDecimal quantidadeJaDevolvida(PdvVendaDetalhe item) {
        return devolucaoRepository.somarQuantidadeDevolvida(item).orElse(BigDecimal.ZERO);
    }

    /** Quanto ainda pode ser devolvido desse item (quantidade original - já devolvido). */
    public BigDecimal quantidadeDisponivelParaDevolucao(PdvVendaDetalhe item) {
        BigDecimal original = item.getQuantidade() != null ? item.getQuantidade() : BigDecimal.ZERO;
        return original.subtract(quantidadeJaDevolvida(item));
    }

    /**
     * Registra a devolução de (parte de) um item de venda já finalizada.
     *
     * @param item                item original (de um cupom com StatusVenda.F)
     * @param quantidade          quantidade devolvida (deve caber no saldo disponível)
     * @param motivo              texto livre opcional
     * @param operador            operador que processou a devolução
     * @param gerenteAutorizador  gerente/supervisor que autorizou — SEMPRE obrigatório
     * @return o registro de devolução persistido
     */
    @Transactional
    public PdvDevolucao registrarDevolucao(PdvVendaDetalhe item, BigDecimal quantidade, String motivo,
                                           PdvOperador operador, PdvOperador gerenteAutorizador) {
        if (item == null) {
            throw new IllegalArgumentException("Item de venda é obrigatório.");
        }
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade a devolver deve ser maior que zero.");
        }
        if (operador == null) {
            throw new IllegalArgumentException("Operador é obrigatório.");
        }
        if (gerenteAutorizador == null) {
            throw new IllegalArgumentException(
                    "Devolução sempre exige autorização gerencial.");
        }

        PdvVendaDetalhe itemGerenciado = vendaDetalheRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Item de venda #" + item.getId() + " não encontrado."));

        if (itemGerenciado.getPdvVendaCabecalho() == null
                || itemGerenciado.getPdvVendaCabecalho().getStatusVenda() != StatusVenda.F) {
            throw new IllegalStateException(
                    "Só é possível devolver itens de vendas já finalizadas.");
        }
        if (itemGerenciado.getCancelado() == SimNao.S) {
            throw new IllegalStateException("Item já estava cancelado na própria venda — não há o que devolver.");
        }

        BigDecimal disponivel = quantidadeDisponivelParaDevolucao(itemGerenciado);
        if (quantidade.compareTo(disponivel) > 0) {
            throw new IllegalStateException(
                    "Quantidade a devolver (" + quantidade + ") maior que a disponível (" + disponivel + ").");
        }

        BigDecimal valorUnitario = itemGerenciado.getValorUnitario() != null
                ? itemGerenciado.getValorUnitario() : BigDecimal.ZERO;
        BigDecimal valorDevolvido = valorUnitario.multiply(quantidade);
        // Simplificação consciente: não rateia desconto/acréscimo do item
        // proporcionalmente — valor devolvido é preço unitário x quantidade.

        // Reentrada de estoque (TipoMovimentoEstoque.D)
        EstoqueMovimento movimento = estoqueMovimentoService.registrarMovimento(
                itemGerenciado.getProduto(), TipoMovimentoEstoque.D, quantidade,
                itemGerenciado, operador,
                "Devolução — item #" + itemGerenciado.getId() + " do cupom #"
                        + itemGerenciado.getPdvVendaCabecalho().getId());

        PdvDevolucao devolucao = new PdvDevolucao();
        devolucao.setVendaCabecalho(itemGerenciado.getPdvVendaCabecalho());
        devolucao.setVendaDetalhe(itemGerenciado);
        devolucao.setQuantidadeDevolvida(quantidade);
        devolucao.setValorDevolvido(valorDevolvido);
        devolucao.setMotivo(motivo);
        devolucao.setOperador(operador);
        devolucao.setGerenteAutorizador(gerenteAutorizador);
        devolucao.setDataDevolucao(LocalDate.now());
        devolucao.setHoraDevolucao(LocalTime.now().format(FMT_HORA));
        devolucao.setEstoqueMovimento(movimento);
        devolucao.setIdDispositivo(dispositivoInfo.getHostname());

        PdvDevolucao salva = devolucaoRepository.save(devolucao);
        LOGGER.info("Devolução registrada: item={}, quantidade={}, valor={}, autorizador={}",
                itemGerenciado.getId(), quantidade, valorDevolvido, gerenteAutorizador.getId());
        return salva;
    }
}
