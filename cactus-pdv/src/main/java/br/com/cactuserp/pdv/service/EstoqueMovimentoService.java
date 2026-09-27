package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.EstoqueMovimento;
import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.TipoMovimentoEstoque;
import br.com.cactuserp.pdv.repository.EstoqueMovimentoRepository;
import br.com.cactuserp.pdv.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Ponto único de alteração de {@code PRODUTO.QUANTIDADE_ESTOQUE}.
 *
 * <p>Transforma o saldo corrente do produto num livro-razão auditável: toda
 * entrada/saída/ajuste/perda/devolução gera um {@link EstoqueMovimento} e só
 * então atualiza o saldo. Nenhum outro service deve chamar
 * {@code Produto#setQuantidadeEstoque} diretamente.
 *
 * <p>Fase 5 do roadmap (ver {@code ROADMAP.md}). Base para devolução
 * (fase 8), que deve reaproveitar {@link #registrarMovimento} com
 * {@link TipoMovimentoEstoque#D}.
 */
@Service
@Transactional(readOnly = true)
public class EstoqueMovimentoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EstoqueMovimentoService.class);
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final EstoqueMovimentoRepository movimentoRepository;
    private final ProdutoRepository produtoRepository;
    private final DispositivoInfoService dispositivoInfo;

    public EstoqueMovimentoService(EstoqueMovimentoRepository movimentoRepository,
                                   ProdutoRepository produtoRepository,
                                   DispositivoInfoService dispositivoInfo) {
        this.movimentoRepository = movimentoRepository;
        this.produtoRepository = produtoRepository;
        this.dispositivoInfo = dispositivoInfo;
    }

    /**
     * Registra um movimento de estoque e atualiza o saldo do produto.
     *
     * @param produto      produto afetado (gerenciado ou não — é recarregado)
     * @param tipo         tipo do movimento (define o sinal do efeito)
     * @param quantidade   quantidade movimentada, sempre positiva
     * @param vendaDetalhe item de venda de origem (só para {@code TIPO=S}); pode ser null
     * @param operador     operador responsável; pode ser null (ex: processo automático)
     * @param observacao   texto livre opcional
     * @return o movimento gravado
     */
    @Transactional
    public EstoqueMovimento registrarMovimento(Produto produto,
                                               TipoMovimentoEstoque tipo,
                                               BigDecimal quantidade,
                                               PdvVendaDetalhe vendaDetalhe,
                                               PdvOperador operador,
                                               String observacao) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto é obrigatório.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de movimento é obrigatório.");
        }
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        // Recarrega o produto gerenciado nesta transação — evita concorrência
        // pisando saldo com uma instância desatualizada.
        Produto gerenciado = produtoRepository.findById(produto.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Produto #" + produto.getId() + " não encontrado."));

        BigDecimal saldoAnterior = gerenciado.getQuantidadeEstoque() != null
                ? gerenciado.getQuantidadeEstoque()
                : BigDecimal.ZERO;

        BigDecimal saldoNovo = aplicarEfeito(saldoAnterior, tipo, quantidade);

        EstoqueMovimento mov = new EstoqueMovimento();
        mov.setProduto(gerenciado);
        mov.setTipoMovimento(tipo);
        mov.setQuantidade(quantidade);
        mov.setQuantidadeAnterior(saldoAnterior);
        mov.setQuantidadeNova(saldoNovo);
        mov.setVendaDetalhe(vendaDetalhe);
        mov.setOperador(operador);
        mov.setDataMovimento(LocalDate.now());
        mov.setHoraMovimento(LocalTime.now().format(FMT_HORA));
        mov.setObservacao(observacao);
        mov.setIdDispositivo(dispositivoInfo.getHostname());

        EstoqueMovimento salvo = movimentoRepository.save(mov);

        gerenciado.setQuantidadeEstoque(saldoNovo);
        produtoRepository.save(gerenciado);

        LOGGER.info("Estoque movimentado: produto={}, tipo={}, qtd={}, saldo {} -> {}",
                gerenciado.getId(), tipo, quantidade, saldoAnterior, saldoNovo);

        return salvo;
    }

    /**
     * Baixa de estoque por venda finalizada. Idempotente: se já existe um
     * movimento gerado a partir desse item (reprocessamento acidental), não
     * duplica a baixa.
     */
    @Transactional
    public void registrarSaidaPorVenda(PdvVendaDetalhe item, PdvOperador operador) {
        if (item == null || item.getProduto() == null) return;
        if (item.getQuantidade() == null || item.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) return;
        if (item.getId() != null && movimentoRepository.existsByVendaDetalheId(item.getId())) {
            LOGGER.warn("Baixa de estoque já registrada para o item de venda #{} — ignorando.", item.getId());
            return;
        }

        registrarMovimento(item.getProduto(), TipoMovimentoEstoque.S, item.getQuantidade(),
                item, operador, "Baixa automática por venda — item #" + item.getId());
    }

    /**
     * Calcula o novo saldo aplicando o efeito do tipo de movimento.
     * Entrada/devolução somam; saída/perda subtraem; ajuste usa a
     * quantidade informada como o NOVO saldo absoluto (não delta).
     */
    private BigDecimal aplicarEfeito(BigDecimal saldoAnterior, TipoMovimentoEstoque tipo, BigDecimal quantidade) {
        return switch (tipo) {
            case E, D -> saldoAnterior.add(quantidade);
            case S, P -> saldoAnterior.subtract(quantidade);
            case A -> quantidade; // ajuste: quantidade informada É o novo saldo
        };
    }
}
