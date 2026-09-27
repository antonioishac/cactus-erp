package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvConfiguracaoBalanca;
import br.com.cactuserp.pdv.entity.Produto;
import br.com.cactuserp.pdv.entity.ProdutoUnidade;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoBalancaRepository;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.ProdutoRepository;
import br.com.cactuserp.pdv.repository.ProdutoUnidadeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Resolve um código de barras de balança (peso/valor embutido) pra um
 * produto + quantidade, usando a configuração do caixa atual — e também
 * dispara a pesagem AO VIVO na balança ligada ao terminal (ver
 * {@link #lerPesoAoVivo}), pra produto sem etiqueta nenhuma.
 *
 * <p>Cobre os <b>dois cenários</b> de balança do PDV (ver
 * {@code CLAUDE.md}): (1) etiqueta pré-impressa — orquestra
 * {@link BalancaCodigoDecoder} (decodificação pura) +
 * {@link ProdutoRepository#findByCodigoBalanca}; (2) pesagem ao vivo —
 * orquestra {@link BalancaSerialService} (leitura da porta serial) +
 * {@link #vendidoPorPeso} (decide se o produto selecionado admite pesagem).
 * Usado por {@code Pdv4Controller}.
 */
@Service
@Transactional(readOnly = true)
public class BalancaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BalancaService.class);

    private final PdvConfiguracaoRepository configuracaoRepository;
    private final PdvConfiguracaoBalancaRepository configuracaoBalancaRepository;
    private final ProdutoRepository produtoRepository;
    private final ProdutoUnidadeRepository produtoUnidadeRepository;
    private final BalancaCodigoDecoder decoder;
    private final BalancaSerialService balancaSerialService;

    public BalancaService(PdvConfiguracaoRepository configuracaoRepository,
                          PdvConfiguracaoBalancaRepository configuracaoBalancaRepository,
                          ProdutoRepository produtoRepository,
                          ProdutoUnidadeRepository produtoUnidadeRepository,
                          BalancaCodigoDecoder decoder,
                          BalancaSerialService balancaSerialService) {
        this.configuracaoRepository = configuracaoRepository;
        this.configuracaoBalancaRepository = configuracaoBalancaRepository;
        this.produtoRepository = produtoRepository;
        this.produtoUnidadeRepository = produtoUnidadeRepository;
        this.decoder = decoder;
        this.balancaSerialService = balancaSerialService;
    }

    /**
     * Tenta resolver o texto escaneado como código de balança.
     * Retorna vazio (sem lançar exceção) em qualquer caso de não-aplicação:
     * caixa sem configuração de balança, texto não numérico/13 dígitos,
     * dígito verificador inválido, ou produto não cadastrado com esse
     * código — deixa o caller seguir pro fluxo normal de "produto não
     * encontrado".
     */
    public Optional<ResultadoLeituraBalanca> tentarDecodificar(PdvCaixa caixa, String textoScaneado) {
        if (caixa == null || caixa.getId() == null) return Optional.empty();
        if (textoScaneado == null || !textoScaneado.matches("\\d{13}")) return Optional.empty();

        PdvConfiguracao config = configuracaoRepository.findFirstByCaixaId(caixa.getId()).orElse(null);
        if (config == null) return Optional.empty();

        PdvConfiguracaoBalanca configBalanca = configuracaoBalancaRepository
                .findFirstByConfiguracaoId(config.getId()).orElse(null);
        if (configBalanca == null || configBalanca.getTipoConfiguracao() == null
                || configBalanca.getTipoConfiguracao().isBlank()) {
            return Optional.empty();
        }

        BalancaCodigoDecoder.LeituraBalanca leitura;
        try {
            leitura = decoder.decodificar(textoScaneado, configBalanca.getTipoConfiguracao());
        } catch (IllegalArgumentException ex) {
            LOGGER.debug("Texto '{}' não corresponde à máscara de balança configurada: {}",
                    textoScaneado, ex.getMessage());
            return Optional.empty();
        }

        if (!leitura.isDigitoVerificadorValido()) {
            LOGGER.warn("Código de balança '{}' com dígito verificador inválido — ignorando.", textoScaneado);
            return Optional.empty();
        }

        Integer codigoProduto;
        try {
            codigoProduto = Integer.valueOf(leitura.getCodigoProduto());
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }

        Produto produto = produtoRepository.findByCodigoBalanca(codigoProduto).orElse(null);
        if (produto == null) {
            LOGGER.warn("Código de balança decodificado ({}) não corresponde a nenhum produto cadastrado.",
                    codigoProduto);
            return Optional.empty();
        }

        BigDecimal quantidade;
        if (leitura.ehPorPeso()) {
            quantidade = leitura.getPesoEmKg();
        } else {
            BigDecimal valorVenda = produto.getValorVenda();
            if (valorVenda == null || valorVenda.signum() <= 0) {
                LOGGER.warn("Produto {} sem valor de venda — não é possível calcular quantidade a partir do valor da etiqueta.",
                        produto.getId());
                return Optional.empty();
            }
            quantidade = leitura.getValorEmReais().divide(valorVenda, 3, RoundingMode.HALF_UP);
        }

        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        return Optional.of(new ResultadoLeituraBalanca(produto, quantidade));
    }

    /**
     * {@code true} só se o produto é vendido por unidade de MASSA (KG/GR —
     * ver {@link ProdutoUnidade#ehUnidadeDePeso()}). Usado pra decidir
     * quando o PDV tenta ler a balança física automaticamente ao
     * resolver o produto — produto vendido por UN, PCT, CX etc não faz
     * sentido mandar pesar, e produto vendido por LT/ML (fracionável mas
     * NÃO é massa) também não — ninguém põe um litro de óleo na balança
     * de checkout. <b>Bug real corrigido (2026-08-23)</b>: antes usava
     * {@link ProdutoUnidade#podeSerFracionado()}, que também é {@code true}
     * pra LT/ML — qualquer produto vendido por litro tentava ler a
     * balança e caía no erro "balança não encontrada".
     */
    public boolean vendidoPorPeso(Produto produto) {
        if (produto == null || produto.getProdutoUnidade() == null) return false;
        return produtoUnidadeRepository.findById(produto.getProdutoUnidade())
                .map(ProdutoUnidade::ehUnidadeDePeso)
                .orElse(false);
    }

    /**
     * Lê o peso ATUAL da balança ligada ao terminal do caixa informado —
     * cenário 2 do javadoc da classe (produto sem etiqueta nenhuma, ex:
     * bananas soltas). Delega pra {@link BalancaSerialService}, que faz a
     * leitura serial de verdade.
     *
     * @return peso em kg, ou vazio se o caixa não tem balança configurada
     *         ou a leitura falhar (porta indisponível, timeout, balança
     *         desligada) — nunca lança exceção, é chamado direto de um
     *         botão da tela de venda.
     */
    public Optional<BigDecimal> lerPesoAoVivo(PdvCaixa caixa) {
        if (caixa == null || caixa.getId() == null) return Optional.empty();

        PdvConfiguracao config = configuracaoRepository.findFirstByCaixaId(caixa.getId()).orElse(null);
        if (config == null) return Optional.empty();

        PdvConfiguracaoBalanca configBalanca = configuracaoBalancaRepository
                .findFirstByConfiguracaoId(config.getId()).orElse(null);
        if (configBalanca == null || configBalanca.getPorta() == null || configBalanca.getPorta().isBlank()) {
            LOGGER.debug("Caixa {} sem balança serial configurada — pesagem ao vivo indisponível.", caixa.getNome());
            return Optional.empty();
        }

        return Optional.ofNullable(balancaSerialService.lerPesoAtual(configBalanca));
    }

    public static class ResultadoLeituraBalanca {
        private final Produto produto;
        private final BigDecimal quantidade;

        public ResultadoLeituraBalanca(Produto produto, BigDecimal quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public Produto getProduto() { return produto; }
        public BigDecimal getQuantidade() { return quantidade; }
    }
}
