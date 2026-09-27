package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.fiscal.client.dto.ClienteFiscalDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronica;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalEletronicaItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Monta o payload {@code NFe.INI} (modelo 55) e o comando ACBr Monitor correspondente —
 * adaptado de {@code cactus-pdv.AcbrComandoFiscalBuilder} (que monta {@code NFCe.INI},
 * modelo 65, já validado contra uma instância real; ver Javadoc de lá e a seção
 * "Integração fiscal" do CLAUDE.md deste monorepo pro protocolo). Mesmo formato de
 * comando: {@code NFE.CriarEnviarNFe(<ini>,<lote>,0,1)}.
 *
 * <h3>Diferenças reais entre NFC-e e NF-e no layout INI (não são o mesmo documento com
 * {@code mod} trocado)</h3>
 * <ul>
 *   <li>{@code mod=55} em vez de {@code 65}.</li>
 *   <li>Destinatário é <b>sempre obrigatório</b> em NF-e (não opcional como em NFC-e,
 *       onde "consumidor não identificado" é válido) — CNPJ/CPF, IE e endereço completo
 *       (seção {@code [Destinatario]} carrega o endereço inline, mesmo padrão INI do
 *       ACBr pra NFe).</li>
 *   <li>{@code idDest} varia por UF (1=interna, 2=interestadual) em vez de fixo — esta
 *       versão só cobre operação interna (mesma UF do emitente) como simplificação
 *       registrada, DIFAL/ICMS interestadual fica fora de escopo desta fase.</li>
 *   <li>Seção {@code [Transportadora]} (opcional, quando a venda tem transportadora) —
 *       não existe em NFC-e (venda de balcão não tem transporte formal).</li>
 * </ul>
 *
 * <h3>Simplificações conscientes (documentadas, não escondidas, mesmo padrão do PDV)</h3>
 * <ul>
 *   <li>Só operação interna (mesma UF emitente/destinatário) — sem DIFAL/ICMS-ST
 *       interestadual, que dependeria do motor tributário completo (Fase 1b do
 *       cadastro-base já resolve isso pra outras operações, não plugado aqui ainda).</li>
 *   <li>{@code natOp} fixo em "Venda de mercadoria".</li>
 *   <li>{@code CFOP} fixo em {@code 5102} (venda de mercadoria dentro do estado, fora do
 *       Simples/consumidor final) — mesmo default do PDV.</li>
 *   <li>Sem seção {@code [cobr]}/duplicatas (condição de pagamento da Venda não é
 *       traduzida em parcelas de fatura na NF-e nesta fase).</li>
 *   <li><b>Nunca testado contra uma instância real do ACBr Monitor</b> — decisão do
 *       usuário (2026-09-03): o ambiente local tem um certificado que parece ser de
 *       produção configurado (`Ambiente=1` no `ACBrMonitor.ini`), então nenhuma emissão
 *       real foi disparada nesta sessão. Mesmo padrão de honestidade que o próprio
 *       {@code EmissaoFiscalService} do PDV usou antes de ser validado.</li>
 * </ul>
 */
@Component
public class NfeIniBuilder {

    private static final DateTimeFormatter FORMATO_DATA_HORA_NFE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String CFOP_PADRAO = "5102";
    private static final String MODELO_NFE = "55";

    /** Monta o comando completo pronto pra enviar via {@code AcbrMonitorClient}. */
    public String construirComando(NotaFiscalEletronica nota, EmpresaFiscalDTO emitente,
                                    ClienteFiscalDTO destinatario, ClienteFiscalDTO transportadora, int lote) {
        String ini = construirIniNFe(nota, emitente, destinatario, transportadora);
        return "NFE.CriarEnviarNFe(" + ini + "," + lote + ",0,1)";
    }

    public String construirIniNFe(NotaFiscalEletronica nota, EmpresaFiscalDTO emitente,
                                   ClienteFiscalDTO destinatario, ClienteFiscalDTO transportadora) {
        StringBuilder ini = new StringBuilder();

        secaoInfNFe(ini);
        secaoIdentificacao(ini, nota);
        secaoEmitente(ini, emitente);
        secaoDestinatario(ini, destinatario);
        if (transportadora != null) {
            secaoTransportadora(ini, transportadora);
        }

        for (NotaFiscalEletronicaItem item : nota.getItens()) {
            secaoProduto(ini, item);
            secaoIcms(ini, item);
            secaoIpi(ini, item);
            secaoPis(ini, item);
            secaoCofins(ini, item);
        }

        secaoTotal(ini, nota);

        return ini.toString();
    }

    private void secaoInfNFe(StringBuilder ini) {
        secao(ini, "infNFe");
        chave(ini, "versao", "4.00");
    }

    private void secaoIdentificacao(StringBuilder ini, NotaFiscalEletronica nota) {
        secao(ini, "Identificacao");
        chave(ini, "cNF", String.format("%08d", nota.getId() % 100_000_000));
        chave(ini, "natOp", "Venda de mercadoria");
        chave(ini, "mod", MODELO_NFE);
        chave(ini, "serie", nota.getSerie());
        chave(ini, "nNF", str(nota.getNumero()));
        chave(ini, "dEmi", nota.getDataEmissao() != null
                ? LocalDateTime.ofInstant(nota.getDataEmissao(), java.time.ZoneId.systemDefault()).format(FORMATO_DATA_HORA_NFE)
                : LocalDateTime.now().format(FORMATO_DATA_HORA_NFE));
        chave(ini, "tpNF", "1");       // 1 = Saída
        chave(ini, "Finalidade", "1"); // 1 = Normal
        chave(ini, "idDest", "1");     // 1 = Operação interna — ver limitação no Javadoc da classe
        chave(ini, "indFinal", "1");   // 1 = Consumidor final
        chave(ini, "indPres", "0");    // 0 = Não se aplica (venda B2B via ERP, não presencial de balcão)
        chave(ini, "tpImp", "1");      // 1 = DANFE retrato
        chave(ini, "tpAmb", nota.getAmbiente() == AmbienteFiscal.PRODUCAO ? "1" : "2");
    }

    private void secaoEmitente(StringBuilder ini, EmpresaFiscalDTO emitente) {
        secao(ini, "Emitente");
        chave(ini, "CNPJ", emitente.cnpj());
        chave(ini, "IE", emitente.inscricaoEstadual());
        chave(ini, "Razao", emitente.razaoSocial());
        chave(ini, "Fantasia", emitente.nomeFantasia());
        chave(ini, "Fone", emitente.telefone());
        chave(ini, "CEP", emitente.cep());
        chave(ini, "Logradouro", emitente.logradouro());
        chave(ini, "Numero", emitente.numero());
        chave(ini, "Complemento", emitente.complemento());
        chave(ini, "Bairro", emitente.bairro());
        chave(ini, "CidadeCod", str(emitente.codigoIbgeMunicipio()));
        chave(ini, "Cidade", emitente.municipio());
        chave(ini, "UF", emitente.uf());
        chave(ini, "CRT", emitente.crt());
    }

    /** Destinatário é sempre obrigatório em NF-e — ver Javadoc da classe. */
    private void secaoDestinatario(StringBuilder ini, ClienteFiscalDTO destinatario) {
        secao(ini, "Destinatario");
        String documento = destinatario.documento();
        if (documento != null && !documento.isBlank()) {
            chave(ini, documento.length() > 11 ? "CNPJ" : "CPF", documento);
        }
        chave(ini, "NomeRazao", destinatario.nome());
        chave(ini, "Email", destinatario.email());
        chave(ini, "IE", destinatario.inscricaoEstadual());
        if (!destinatario.enderecos().isEmpty()) {
            var endereco = destinatario.enderecos().get(0);
            chave(ini, "Logradouro", endereco.logradouro());
            chave(ini, "Numero", endereco.numero());
            chave(ini, "Complemento", endereco.complemento());
            chave(ini, "Bairro", endereco.bairro());
            chave(ini, "CEP", endereco.cep());
            chave(ini, "CidadeCod", str(endereco.codigoIbgeMunicipio()));
            chave(ini, "Cidade", endereco.municipio());
            chave(ini, "UF", endereco.uf());
        }
    }

    private void secaoTransportadora(StringBuilder ini, ClienteFiscalDTO transportadora) {
        secao(ini, "Transportadora");
        String documento = transportadora.documento();
        if (documento != null && !documento.isBlank()) {
            chave(ini, documento.length() > 11 ? "CNPJ" : "CPF", documento);
        }
        chave(ini, "Nome", transportadora.nome());
        if (!transportadora.enderecos().isEmpty()) {
            chave(ini, "UF", transportadora.enderecos().get(0).uf());
        }
        chave(ini, "modFrete", "0"); // 0 = Contratação do frete por conta do remetente (CIF) — simplificação
    }

    private void secaoProduto(StringBuilder ini, NotaFiscalEletronicaItem item) {
        secao(ini, "Produto" + pad(item.getNumeroItem()));
        chave(ini, "CFOP", item.getCfop() != null ? item.getCfop() : CFOP_PADRAO);
        chave(ini, "Codigo", str(item.getProdutoId()));
        chave(ini, "cEAN", item.getGtin() != null ? item.getGtin() : "SEM GTIN");
        chave(ini, "Descricao", item.getDescricao());
        chave(ini, "NCM", item.getNcm());
        chave(ini, "Unidade", item.getUnidade() != null ? item.getUnidade() : "UN");
        chave(ini, "Quantidade", str(item.getQuantidade()));
        chave(ini, "ValorUnitario", str(item.getValorUnitario()));
        chave(ini, "ValorTotal", str(item.getValorTotal()));
        chave(ini, "ValorDesconto", str(item.getValorDesconto()));
        chave(ini, "cEANTrib", item.getGtin() != null ? item.getGtin() : "SEM GTIN");
        chave(ini, "uTrib", item.getUnidade() != null ? item.getUnidade() : "UN");
        chave(ini, "qTrib", str(item.getQuantidade()));
        chave(ini, "vUnTrib", str(item.getValorUnitario()));
    }

    private void secaoIcms(StringBuilder ini, NotaFiscalEletronicaItem item) {
        secao(ini, "ICMS" + pad(item.getNumeroItem()));
        if (item.getCsosn() != null && !item.getCsosn().isBlank()) {
            chave(ini, "CSOSN", item.getCsosn());
        } else {
            chave(ini, "CST", item.getCstIcms());
        }
        chave(ini, "Origem", item.getOrigemMercadoria());
        chave(ini, "ValorBase", str(item.getValorBaseIcms()));
        chave(ini, "Aliquota", str(item.getAliquotaIcms()));
        chave(ini, "Valor", str(item.getValorIcms()));
    }

    private void secaoIpi(StringBuilder ini, NotaFiscalEletronicaItem item) {
        if (item.getAliquotaIpi() == null) {
            return; // sem tributação de IPI configurada pro produto — seção omitida
        }
        secao(ini, "IPI" + pad(item.getNumeroItem()));
        chave(ini, "CST", "99"); // 99 = Outras entradas/saídas — simplificação
        chave(ini, "Aliquota", str(item.getAliquotaIpi()));
        chave(ini, "Valor", str(item.getValorIpi()));
    }

    private void secaoPis(StringBuilder ini, NotaFiscalEletronicaItem item) {
        secao(ini, "PIS" + pad(item.getNumeroItem()));
        chave(ini, "CST", item.getCstPis());
        chave(ini, "ValorBase", str(item.getValorTotal()));
        chave(ini, "Aliquota", str(item.getAliquotaPis()));
        chave(ini, "Valor", str(item.getValorPis()));
    }

    private void secaoCofins(StringBuilder ini, NotaFiscalEletronicaItem item) {
        secao(ini, "COFINS" + pad(item.getNumeroItem()));
        chave(ini, "CST", item.getCstCofins());
        chave(ini, "ValorBase", str(item.getValorTotal()));
        chave(ini, "Aliquota", str(item.getAliquotaCofins()));
        chave(ini, "Valor", str(item.getValorCofins()));
    }

    private void secaoTotal(StringBuilder ini, NotaFiscalEletronica nota) {
        secao(ini, "Total");
        chave(ini, "ValorProduto", str(nota.getItens().stream().map(NotaFiscalEletronicaItem::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
        chave(ini, "ValorNota", str(nota.getValorTotal()));
    }

    // ====================================================================
    //  Helpers — mesmo padrão exato de cactus-pdv.AcbrComandoFiscalBuilder
    // ====================================================================

    private void secao(StringBuilder ini, String nome) {
        ini.append('[').append(nome).append(']').append("\r\n");
    }

    private void chave(StringBuilder ini, String nome, String valor) {
        ini.append(nome).append('=').append(valor != null ? valor : "").append("\r\n");
    }

    private String pad(int numero) {
        return String.format("%03d", numero);
    }

    private String str(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private String str(BigDecimal valor) {
        return valor == null ? "0" : valor.toPlainString();
    }
}
