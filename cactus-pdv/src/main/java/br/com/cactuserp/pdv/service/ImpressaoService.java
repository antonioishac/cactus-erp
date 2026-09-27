package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.config.util.FormaterCurrency;
import br.com.cactuserp.pdv.entity.PdvCaixa;
import br.com.cactuserp.pdv.entity.PdvConfiguracao;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import br.com.cactuserp.pdv.entity.SimNao;
import br.com.cactuserp.pdv.repository.PdvConfiguracaoRepository;
import br.com.cactuserp.pdv.repository.PdvTotalTipoPagamentoRepository;
import br.com.cactuserp.pdv.repository.PdvVendaDetalheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import java.util.List;

/**
 * Impressão térmica do cupom (ESC/POS) e acionamento da gaveta de dinheiro.
 *
 * <p>Envia bytes RAW pra impressora configurada em
 * {@code PDV_CONFIGURACAO.NOME_IMPRESSORA_CUPOM} via {@code javax.print}
 * (JDK, sem dependência nova). O nome deve bater exatamente com o nome da
 * impressora cadastrada no sistema operacional.
 *
 * <p>⚠️ <b>Não testado contra impressora física.</b> Envio RAW via
 * {@code javax.print} funciona na maioria dos drivers Windows de
 * impressora térmica ("Generic / Text Only" ou driver ESC/POS nativo que
 * aceite pass-through), mas alguns drivers podem exigir outra rota (porta
 * de impressora compartilhada direta, ex: {@code \\.\LPT1} ou socket TCP
 * pra modelos de rede). Os comandos ESC/POS em si
 * ({@link EscPosBuilder}) seguem o padrão universal e têm alta confiança;
 * a variável real é como os bytes chegam até o hardware nesta máquina.
 */
@Service
@Transactional(readOnly = true)
public class ImpressaoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImpressaoService.class);

    private final PdvConfiguracaoRepository configuracaoRepository;
    private final PdvVendaDetalheRepository detalheRepository;
    private final PdvTotalTipoPagamentoRepository pagamentoRepository;

    public ImpressaoService(PdvConfiguracaoRepository configuracaoRepository,
                            PdvVendaDetalheRepository detalheRepository,
                            PdvTotalTipoPagamentoRepository pagamentoRepository) {
        this.configuracaoRepository = configuracaoRepository;
        this.detalheRepository = detalheRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    /**
     * Imprime o cupom de uma venda finalizada e abre a gaveta se houver
     * pagamento em dinheiro e a loja tiver gaveta configurada.
     *
     * <p>Nunca lança exceção — falha de impressão não pode impedir o
     * fechamento da venda (já paga/persistida). Loga e retorna.
     */
    public void imprimirCupom(PdvCaixa caixa, PdvVendaCabecalho cupom) {
        if (caixa == null || cupom == null) return;

        PdvConfiguracao config = configuracaoRepository.findFirstByCaixaId(caixa.getId()).orElse(null);
        if (config == null || config.getNomeImpressoraCupom() == null
                || config.getNomeImpressoraCupom().isBlank()) {
            LOGGER.debug("Impressora não configurada para o caixa {} — cupom {} não impresso.",
                    caixa.getNome(), cupom.getId());
            return;
        }

        List<PdvVendaDetalhe> itens = detalheRepository
                .findByPdvVendaCabecalhoAndCanceladoOrderByItemAsc(cupom, SimNao.N);
        List<PdvTotalTipoPagamento> pagamentos = pagamentoRepository.findByIdPdvVendaCabecalho(cupom.getId());

        EscPosBuilder builder = new EscPosBuilder().inicializar();
        montarCupom(builder, cupom, itens, pagamentos);

        boolean temDinheiro = pagamentos.stream().anyMatch(p ->
                p.getPdvTipoPagamento() != null
                        && ConferenciaCaixaService.CODIGO_DINHEIRO.equals(p.getPdvTipoPagamento().getCodigo()));
        if (temDinheiro && config.temGaveta()) {
            boolean invertido = "S".equalsIgnoreCase(config.getGavetaSinalInvertido());
            builder.abrirGaveta(0, invertido);
        }

        builder.cortarPapel();

        enviarParaImpressora(config.getNomeImpressoraCupom(), builder.build());
    }

    private void montarCupom(EscPosBuilder b, PdvVendaCabecalho cupom,
                             List<PdvVendaDetalhe> itens, List<PdvTotalTipoPagamento> pagamentos) {
        b.alinhamento(1).negrito(true).linha("CACTUS PDV").negrito(false);
        b.linha("Cupom nao fiscal");
        b.alinhamento(0).separador();

        if (cupom.getDataVenda() != null) {
            b.linha("Data: " + cupom.getDataVenda() + "  Hora: "
                    + (cupom.getHoraVenda() != null ? cupom.getHoraVenda() : ""));
        }
        if (cupom.getNomeCliente() != null && !cupom.getNomeCliente().isBlank()) {
            b.linha("Cliente: " + cupom.getNomeCliente());
        }
        b.separador();

        for (PdvVendaDetalhe item : itens) {
            String descricao = item.getProduto() != null && item.getProduto().getDescricaoPdv() != null
                    ? item.getProduto().getDescricaoPdv() : "Item";
            b.linha(descricao);
            b.linha(String.format("  %s x %s = %s",
                    item.getQuantidade(), item.getValorUnitario(),
                    FormaterCurrency.fieldFormated(item.getValorTotal())));
        }

        b.separador();
        b.negrito(true).linha("TOTAL: " + FormaterCurrency.fieldFormated(cupom.getValorTotalDocumento())).negrito(false);
        b.separador();

        for (PdvTotalTipoPagamento p : pagamentos) {
            String descricaoForma = p.getPdvTipoPagamento() != null ? p.getPdvTipoPagamento().getDescricao() : "?";
            b.linha(descricaoForma + ": " + FormaterCurrency.fieldFormated(p.getValor()));
        }
        if (cupom.getValorTroco() != null && cupom.getValorTroco().signum() > 0) {
            b.linha("Troco: " + FormaterCurrency.fieldFormated(cupom.getValorTroco()));
        }

        b.separador();
        b.alinhamento(1).linha("Obrigado pela preferencia!").alinhamento(0);
    }

    private void enviarParaImpressora(String nomeImpressora, byte[] dados) {
        PrintService destino = localizarImpressora(nomeImpressora);
        if (destino == null) {
            LOGGER.error("Impressora '{}' não encontrada entre as impressoras do sistema.", nomeImpressora);
            return;
        }

        try {
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(dados, flavor, null);
            DocPrintJob job = destino.createPrintJob();
            job.print(doc, null);
            LOGGER.info("Cupom enviado para a impressora '{}'.", nomeImpressora);
        } catch (Exception ex) {
            LOGGER.error("Falha ao imprimir na impressora '{}': {}", nomeImpressora, ex.getMessage(), ex);
        }
    }

    private PrintService localizarImpressora(String nome) {
        for (PrintService ps : PrintServiceLookup.lookupPrintServices(null, null)) {
            if (ps.getName().equalsIgnoreCase(nome)) return ps;
        }
        return null;
    }
}
