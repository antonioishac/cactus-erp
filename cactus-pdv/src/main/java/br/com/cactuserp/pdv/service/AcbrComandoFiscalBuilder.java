package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.LojaConfiguracao;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;
import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Monta o payload INI de uma NFC-e e o comando ACBr Monitor correspondente.
 *
 * <h3>Protocolo — validado contra instância real (2026-08-22)</h3>
 * <p>Diferente do desenho anterior desta classe (sequência de comandos tipo
 * {@code IncluirItem}/{@code IncluirPagamento}), o ACBr Monitor NÃO monta o
 * documento incrementalmente — ele recebe o documento inteiro de uma vez
 * em <b>formato INI</b> (layout {@code NFCe.INI} do projeto ACBr) e um
 * único comando:
 * <pre>{@code NFE.CriarEnviarNFe(<conteúdo INI>,<lote>,<imprimeDANFE>,<síncrono>)}</pre>
 * Testado manualmente via socket cru contra um ACBrMonitorPLUS demo
 * rodando localmente (não só documentação): objeto {@code NFE} confirmado
 * na lista de objetos válidos do servidor; conteúdo INI inline (sem
 * arquivo) foi aceito e processado até validação de campo de negócio
 * ({@code "Nome do Município deve ser informado"} pra um INI mínimo de
 * propósito) — ver {@link #construirComando} pro relato completo do teste.
 * Fonte adicional: acbr.sourceforge.io/ACBrMonitor/ModeloNFCeINI.html e
 * acbr.sourceforge.io/ACBrMonitor/NFECriarEnviarNFe.html.
 *
 * <h3>Simplificações conscientes (documentadas, não escondidas)</h3>
 * <ul>
 *   <li><b>{@code nNF}</b> (número da NFC-e) usa o ID do cupom. Em produção
 *       de verdade, o número deveria vir de um contador sequencial por
 *       série sincronizado com a SEFAZ — não implementado.</li>
 *   <li><b>{@code tpAmb}</b> fixo em {@code 2} (Homologação) — não há campo
 *       de ambiente configurável ainda; nunca vai gerar documento de
 *       produção enquanto não for parametrizado.</li>
 *   <li><b>{@code uCom}</b> (unidade comercial) fixa em {@code "UN"} —
 *       {@code PRODUTO.ID_PRODUTO_UNIDADE} é só uma FK numérica na
 *       entidade atual, sem relacionamento JPA resolvido pra sigla.</li>
 *   <li><b>CFOP</b> usa {@code 5102} (venda de mercadoria, dentro do
 *       estado, consumidor final) como default se o item não tiver CFOP
 *       próprio — cobre o caso mais comum de venda de balcão.</li>
 * </ul>
 *
 * <p>SAT e MFE têm layouts de comando totalmente diferentes do NFC-e (não
 * são variações do mesmo formato INI) e <b>não foram implementados</b> —
 * {@link #construirComando} lança {@link UnsupportedOperationException}
 * pra esses módulos, que o {@code EmissaoFiscalService} já trata como
 * qualquer outra falha (cai pra contingência ou pendente).
 */
@Component
public class AcbrComandoFiscalBuilder {

    public static final String MODULO_NFCE = "NFC-E";
    public static final String MODULO_SAT = "SAT";
    public static final String MODULO_MFE = "MFE";

    /** ⚠️ dd/MM/yyyy — confirmado por teste manual, ISO 8601 é rejeitado (ver {@link #formatarDataHora}). */
    private static final DateTimeFormatter FORMATO_DATA_HORA_NFE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String CFOP_PADRAO = "5102";
    private static final String AMBIENTE_HOMOLOGACAO = "2";

    /**
     * Monta o comando completo pronto pra enviar via {@link AcbrMonitorClient}
     * — {@code NFE.CriarEnviarNFe(<ini>,<lote>,0,1)} com o conteúdo INI
     * embutido direto no comando.
     *
     * <p><b>✅ Confirmado contra instância real (2026-08-22)</b>: testei os
     * dois formatos manualmente via socket cru contra um ACBrMonitorPLUS
     * demo rodando localmente — passar um caminho de arquivo inexistente
     * retornou {@code "ERRO: Arquivo: ... não encontrado"} (então caminho
     * funciona), e passar o conteúdo INI direto foi aceito e processado
     * até validação de campo ({@code "ERRO: Nome do Município deve ser
     * informado"} pra um INI mínimo de propósito) — confirma que o
     * primeiro parâmetro aceita o texto do INI diretamente, sem precisar
     * de arquivo. Optei pelo inline por ser mais simples (sem I/O, sem
     * depender de caminho compartilhado caso ACBr rode em outra máquina).
     *
     * <p>⚠️ Não testado: como o parser de comando do ACBr lida com vírgulas
     * <b>dentro</b> de valores de campo (ex: endereço com vírgula) — o
     * teste manual usou conteúdo sem vírgulas. Se um campo de texto (nome
     * do cliente, endereço) tiver vírgula, pode confundir a separação de
     * parâmetros do comando. Validar antes de produção.
     *
     * @throws UnsupportedOperationException se o módulo não for NFC-E
     */
    public String construirComando(String modulo,
                                   PdvVendaCabecalho cupom,
                                   LojaConfiguracao loja,
                                   List<PdvVendaDetalhe> itens,
                                   List<PdvTotalTipoPagamento> pagamentos,
                                   int lote) {
        if (modulo == null || !MODULO_NFCE.equalsIgnoreCase(modulo)) {
            throw new UnsupportedOperationException(
                    "Módulo fiscal '" + modulo + "' não implementado — só NFC-E tem o layout INI mapeado. "
                            + "SAT/MFE usam comandos próprios, ainda não desenhados nesta classe.");
        }

        String ini = construirIniNFCe(cupom, loja, itens, pagamentos);
        return "NFE.CriarEnviarNFe(" + ini + "," + lote + ",0,1)";
    }

    /**
     * Comando do evento de cancelamento da NFC-e:
     * {@code NFE.CancelarNFe("<chave>","<justificativa>","<CNPJ emitente>")}.
     * Assinatura conferida na documentação oficial
     * (acbr.sourceforge.io/ACBrMonitor/NFECancelarNFe.html, 2026-10-25),
     * <b>não exercitada contra o ACBr real</b> (mesmo bloqueio de certificado
     * de produção da emissão). Os três vão entre aspas e sem aspas internas:
     * uma vírgula na justificativa quebraria a lista de parâmetros.
     */
    public String construirComandoCancelamentoNfce(String chaveAcesso, String justificativa, String cnpjEmitente) {
        return "NFE.CancelarNFe(" + entreAspas(chaveAcesso) + "," + entreAspas(justificativa) + ","
                + entreAspas(cnpjEmitente) + ")";
    }

    private static String entreAspas(String valor) {
        return "\"" + (valor == null ? "" : valor.replace("\"", "").trim()) + "\"";
    }

    // ====================================================================
    //  Construção do INI — lógica pura, sem I/O (testável isoladamente)
    // ====================================================================

    /**
     * Monta o conteúdo do arquivo NFCe.INI conforme o layout documentado
     * pelo projeto ACBr.
     */
    public String construirIniNFCe(PdvVendaCabecalho cupom,
                                   LojaConfiguracao loja,
                                   List<PdvVendaDetalhe> itens,
                                   List<PdvTotalTipoPagamento> pagamentos) {
        StringBuilder ini = new StringBuilder();

        secaoInfNFe(ini);
        secaoIdentificacao(ini, cupom);
        secaoEmitente(ini, loja);
        // Destinatário é opcional em NFC-e (consumidor não identificado é válido).
        if (cupom.getCliente() != null || temNomeClienteAvulso(cupom)) {
            secaoDestinatario(ini, cupom);
        }

        int numeroItem = 1;
        for (PdvVendaDetalhe item : itens) {
            secaoProduto(ini, numeroItem, item);
            secaoIcms(ini, numeroItem, item, loja);
            secaoPis(ini, numeroItem, item);
            secaoCofins(ini, numeroItem, item);
            numeroItem++;
        }

        secaoTotal(ini, cupom);

        int numeroPagamento = 1;
        for (PdvTotalTipoPagamento pagamento : pagamentos) {
            secaoPagamento(ini, numeroPagamento, pagamento);
            numeroPagamento++;
        }

        return ini.toString();
    }

    private void secaoInfNFe(StringBuilder ini) {
        secao(ini, "infNFe");
        chave(ini, "versao", "4.00");
    }

    private void secaoIdentificacao(StringBuilder ini, PdvVendaCabecalho cupom) {
        secao(ini, "Identificacao");
        chave(ini, "cNF", String.format("%08d", cupom.getId() % 100_000_000));
        chave(ini, "natOp", "Venda");
        chave(ini, "mod", "65"); // 65 = NFC-e (fixo)
        chave(ini, "serie", "1");
        chave(ini, "nNF", String.valueOf(cupom.getId())); // simplificação — ver javadoc da classe
        chave(ini, "dEmi", cupom.getDataVenda() != null && cupom.getHoraVenda() != null
                ? formatarDataHora(cupom.getDataVenda(), cupom.getHoraVenda())
                : java.time.LocalDateTime.now().format(FORMATO_DATA_HORA_NFE));
        chave(ini, "tpNF", "1");       // 1 = Saída
        chave(ini, "Finalidade", "1"); // 1 = Normal
        chave(ini, "idDest", "1");     // 1 = Operação interna
        chave(ini, "indFinal", "1");   // 1 = Consumidor final
        chave(ini, "indPres", "1");    // 1 = Presencial
        chave(ini, "tpImp", "4");      // 4 = DANFE NFC-e
        chave(ini, "tpAmb", AMBIENTE_HOMOLOGACAO);
    }

    /**
     * ⚠️ Formato confirmado por teste manual (2026-08-22): {@code dd/MM/yyyy HH:mm:ss}.
     * O formato ISO 8601 com "T" ({@code yyyy-MM-dd'T'HH:mm:ss}) foi
     * REJEITADO pelo ACBr real ({@code "... is not a valid time"}) — o
     * parser de data/hora do Delphi/Lazarus por trás do ACBr Monitor
     * espera o formato de data brasileiro, não ISO 8601.
     */
    private String formatarDataHora(java.time.LocalDate data, String hora) {
        return data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " + hora;
    }

    private void secaoEmitente(StringBuilder ini, LojaConfiguracao loja) {
        secao(ini, "Emitente");
        if (loja == null) {
            // Sem LOJA_CONFIGURACAO cadastrada não há como montar um Emitente
            // válido — deixa a seção praticamente vazia; ACBr vai rejeitar,
            // o que é o comportamento correto (não inventar CNPJ).
            return;
        }
        chave(ini, "CNPJ", loja.getCnpj());
        chave(ini, "IE", loja.getInscricaoEstadual());
        chave(ini, "Razao", loja.getRazaoSocial());
        chave(ini, "Fantasia", loja.getNomeFantasia());
        chave(ini, "Fone", loja.getTelefone());
        chave(ini, "CEP", loja.getCep());
        chave(ini, "Logradouro", loja.getLogradouro());
        chave(ini, "Numero", loja.getNumero());
        chave(ini, "Complemento", loja.getComplemento());
        chave(ini, "Bairro", loja.getBairro());
        chave(ini, "CidadeCod", str(loja.getCodigoIbgeCidade()));
        chave(ini, "Cidade", loja.getCidade());
        chave(ini, "UF", loja.getUf());
        chave(ini, "CRT", loja.getCrt());
    }

    private boolean temNomeClienteAvulso(PdvVendaCabecalho cupom) {
        return cupom.getNomeCliente() != null && !cupom.getNomeCliente().isBlank();
    }

    private void secaoDestinatario(StringBuilder ini, PdvVendaCabecalho cupom) {
        secao(ini, "Destinatario");
        String cpfCnpj = cupom.getCliente() != null ? cupom.getCliente().getCpfCnpj() : cupom.getCpfCnpjCliente();
        String nome = cupom.getCliente() != null ? cupom.getCliente().getNome() : cupom.getNomeCliente();
        if (cpfCnpj != null && !cpfCnpj.isBlank()) {
            chave(ini, cpfCnpj.length() > 11 ? "CNPJ" : "CPF", cpfCnpj);
        }
        chave(ini, "NomeRazao", nome);
    }

    private void secaoProduto(StringBuilder ini, int numero, PdvVendaDetalhe item) {
        secao(ini, "Produto" + pad(numero));
        chave(ini, "CFOP", item.getCfop() != null ? String.valueOf(item.getCfop()) : CFOP_PADRAO);
        chave(ini, "Codigo", item.getProduto() != null ? str(item.getProduto().getId()) : "");
        chave(ini, "cEAN", item.getGtin() != null ? item.getGtin() : "SEM GTIN");
        chave(ini, "Descricao", descricaoProduto(item));
        chave(ini, "NCM", item.getProduto() != null ? item.getProduto().getCodigoNcm() : null);
        chave(ini, "Unidade", "UN"); // simplificação — ver javadoc da classe
        chave(ini, "Quantidade", str(item.getQuantidade()));
        chave(ini, "ValorUnitario", str(item.getValorUnitario()));
        chave(ini, "ValorTotal", str(item.getValorTotal()));
        chave(ini, "ValorDesconto", str(item.getValorDesconto()));
        chave(ini, "cEANTrib", item.getGtin() != null ? item.getGtin() : "SEM GTIN");
        chave(ini, "uTrib", "UN");
        chave(ini, "qTrib", str(item.getQuantidade()));
        chave(ini, "vUnTrib", str(item.getValorUnitario()));
    }

    private String descricaoProduto(PdvVendaDetalhe item) {
        if (item.getProduto() == null) return "Item";
        String desc = item.getProduto().getDescricaoPdv();
        return desc != null && !desc.isBlank() ? desc : item.getProduto().getDescricao();
    }

    /**
     * ⚠️ Regra confirmada por teste manual (2026-08-22): empresas do
     * Simples Nacional (CRT 1 ou 2) usam {@code CSOSN} em vez de
     * {@code CST} no grupo de ICMS — o ACBr monta um XML diferente
     * ({@code ICMSSN}) baseado no CRT do emitente, e rejeita o documento
     * se o campo esperado pro regime não vier preenchido
     * ({@code "N12a/CSOSN ... Nenhum valor informado"} foi o erro real
     * obtido testando com CRT=1 e só {@code CST} preenchido).
     */
    private void secaoIcms(StringBuilder ini, int numero, PdvVendaDetalhe item, LojaConfiguracao loja) {
        secao(ini, "ICMS" + pad(numero));
        if (ehSimplesNacional(loja)) {
            String csosn = item.getProduto() != null ? item.getProduto().getCsosn() : null;
            chave(ini, "CSOSN", csosn);
        } else {
            chave(ini, "CST", item.getCst() != null ? item.getCst() : "");
        }
        chave(ini, "Origem", "0"); // 0 = Nacional — simplificação (produto não guarda origem hoje)
        chave(ini, "ValorBase", str(item.getValorBaseIcms()));
        chave(ini, "Aliquota", str(item.getTaxaIcms()));
        chave(ini, "Valor", str(item.getValorIcms()));
    }

    /** CRT 1 (Simples Nacional) ou 2 (Simples excesso sublimite) — ver PDV.md LOJA_CONFIGURACAO.CRT. */
    private boolean ehSimplesNacional(LojaConfiguracao loja) {
        String crt = loja != null ? loja.getCrt() : null;
        return "1".equals(crt) || "2".equals(crt);
    }

    private void secaoPis(StringBuilder ini, int numero, PdvVendaDetalhe item) {
        secao(ini, "PIS" + pad(numero));
        chave(ini, "CST", "01"); // simplificação — tributação normal
        chave(ini, "ValorBase", str(item.getValorTotal()));
        chave(ini, "Aliquota", str(item.getTaxaPis()));
        chave(ini, "Valor", str(item.getValorPis()));
    }

    private void secaoCofins(StringBuilder ini, int numero, PdvVendaDetalhe item) {
        secao(ini, "COFINS" + pad(numero));
        chave(ini, "CST", "01");
        chave(ini, "ValorBase", str(item.getValorTotal()));
        chave(ini, "Aliquota", str(item.getTaxaCofins()));
        chave(ini, "Valor", str(item.getValorCofins()));
    }

    private void secaoTotal(StringBuilder ini, PdvVendaCabecalho cupom) {
        secao(ini, "Total");
        chave(ini, "BaseICMS", str(cupom.getValorBaseIcms()));
        chave(ini, "ValorICMS", str(cupom.getValorIcms()));
        chave(ini, "ValorProduto", str(cupom.getValorTotalProdutos()));
        chave(ini, "ValorDesconto", str(cupom.getValorDesconto()));
        chave(ini, "ValorPIS", str(cupom.getValorPis()));
        chave(ini, "ValorCOFINS", str(cupom.getValorCofins()));
        chave(ini, "ValorNota", str(cupom.getValorTotalDocumento()));
    }

    private void secaoPagamento(StringBuilder ini, int numero, PdvTotalTipoPagamento pagamento) {
        secao(ini, "pag" + pad(numero));
        String tPag = pagamento.getPdvTipoPagamento() != null
                ? pagamento.getPdvTipoPagamento().getCodigoPagamentoNfce() : null;
        chave(ini, "tPag", tPag != null && !tPag.isBlank() ? tPag : "99");
        chave(ini, "vPag", str(pagamento.getValor()));
    }

    // ====================================================================
    //  Helpers
    // ====================================================================

    /**
     * Escreve um cabeçalho de seção {@code [Nome]}.
     *
     * <p>⚠️ Bug real encontrado em teste manual (2026-08-22): antes,
     * {@link #chave} usava {@code valor == null} pra decidir entre "isto é
     * uma seção" e "isto é uma chave sem valor" — mas chaves legítimas
     * (ex: {@code Fone}, {@code Complemento}) podem ser {@code null} nos
     * dados reais (campo opcional não preenchido), o que corrompia a
     * estrutura do INI virando uma seção falsa no meio dos dados do
     * Emitente. Agora são dois métodos sem ambiguidade.
     */
    private void secao(StringBuilder ini, String nome) {
        ini.append('[').append(nome).append(']').append("\r\n");
    }

    /** Escreve uma chave {@code Nome=valor}. {@code null} vira string vazia, nunca uma seção. */
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
