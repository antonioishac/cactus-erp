package br.com.cactusdigital.erp.fiscal.service;

import br.com.cactusdigital.erp.fiscal.client.dto.ItemPedidoLojaClienteDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.PedidoLojaClienteDTO;
import br.com.cactusdigital.erp.fiscal.client.dto.EmpresaFiscalDTO;
import br.com.cactusdigital.erp.fiscal.entity.AmbienteFiscal;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalConsumidor;
import br.com.cactusdigital.erp.fiscal.entity.NotaFiscalConsumidorItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Monta o payload {@code NFCe.INI} e o comando ACBr Monitor correspondente — portado de
 * {@code cactus-pdv.AcbrComandoFiscalBuilder}, que é o layout com a MAIOR confiança de
 * todo o `fiscal-service`: já testado manualmente contra um ACBrMonitorPLUS real
 * (2026-08-22, ver Javadoc da classe original), diferente de NF-e/CT-e (pesquisa geral)
 * e NFS-e (comando incerto). Mesmo comando de tiro único:
 * {@code NFE.CriarEnviarNFe(<ini>,<lote>,0,1)} (a família de comando é a mesma pra
 * NF-e/NFC-e — só {@code mod} muda no INI).
 *
 * <h3>Achados reais já corrigidos na origem, preservados aqui</h3>
 * <ul>
 *   <li>Data/hora em {@code dd/MM/yyyy HH:mm:ss} — ISO 8601 é REJEITADO pelo ACBr real.</li>
 *   <li>{@code null} numa chave vira string vazia, nunca uma seção nova (bug de
 *       ambiguidade já corrigido na origem, ver helper {@link #chave}).</li>
 *   <li><b>CSOSN, nunca CST de regime normal</b> — o modo standalone da Loja Virtual só
 *       modela Simples Nacional (ver Javadoc de {@link NotaFiscalConsumidor}), então o
 *       branch condicional CRT-based da origem virou incondicional aqui.</li>
 * </ul>
 *
 * <h3>Simplificações conscientes herdadas da origem (documentadas, não escondidas)</h3>
 * <ul>
 *   <li>{@code tpAmb} resolvido da configuração fiscal real da empresa (diferente da
 *       origem, que tinha fixo em Homologação por não ter campo configurável ainda —
 *       aqui já existe via {@code EmpresaConfiguracaoFiscal}).</li>
 *   <li>{@code Unidade}/{@code uTrib} fixos em {@code "UN"} — {@code VarianteProdutoLoja}
 *       não tem unidade de medida configurável nesta fase.</li>
 *   <li>PIS/COFINS com {@code CST} fixo em {@code "01"} e sem base/alíquota (a Loja
 *       Virtual não calcula esses tributos no modo standalone) — mesma simplificação já
 *       aceita na origem.</li>
 *   <li>Sem seção de pagamento ({@code pag001}) nesta v1 — {@code Pedido.formaPagamento}
 *       é só um enum simplificado (PIX/CARTAO_CREDITO/BOLETO) sem o código de pagamento
 *       NFC-e (tabela {@code tPag} da SEFAZ) mapeado ainda; registrado como próximo
 *       passo, não inventado aqui.</li>
 *   <li><b>Nunca testado contra uma instância real do ACBr Monitor NESTA integração</b>
 *       (mesma decisão de segurança das Fases 8/9/11) — só o layout NFCe.INI em si
 *       (não este builder específico) foi validado, na origem, contra um ACBrMonitorPLUS
 *       demo.</li>
 * </ul>
 */
@Component
public class NfceIniBuilder {

    private static final DateTimeFormatter FORMATO_DATA_HORA_NFCE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String CFOP_PADRAO = "5102";
    private static final String MODELO_NFCE = "65";

    public String construirComando(NotaFiscalConsumidor nota, EmpresaFiscalDTO emitente,
                                    PedidoLojaClienteDTO pedido, int lote) {
        String ini = construirIniNfce(nota, emitente, pedido);
        return "NFE.CriarEnviarNFe(" + ini + "," + lote + ",0,1)";
    }

    public String construirIniNfce(NotaFiscalConsumidor nota, EmpresaFiscalDTO emitente, PedidoLojaClienteDTO pedido) {
        StringBuilder ini = new StringBuilder();

        secaoInfNFe(ini);
        secaoIdentificacao(ini, nota);
        secaoEmitente(ini, emitente);
        // Destinatário é opcional em NFC-e — só escreve se tiver CPF/nome (guest checkout sem CPF é válido).
        if (pedido.clienteCpf() != null && !pedido.clienteCpf().isBlank()) {
            secaoDestinatario(ini, pedido);
        }

        for (NotaFiscalConsumidorItem item : nota.getItens()) {
            secaoProduto(ini, item);
            secaoIcms(ini, item);
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

    private void secaoIdentificacao(StringBuilder ini, NotaFiscalConsumidor nota) {
        secao(ini, "Identificacao");
        chave(ini, "cNF", String.format("%08d", nota.getId() % 100_000_000));
        chave(ini, "natOp", "Venda");
        chave(ini, "mod", MODELO_NFCE);
        chave(ini, "serie", nota.getSerie());
        chave(ini, "nNF", str(nota.getNumero()));
        chave(ini, "dEmi", nota.getDataEmissao() != null
                ? LocalDateTime.ofInstant(nota.getDataEmissao(), java.time.ZoneId.systemDefault()).format(FORMATO_DATA_HORA_NFCE)
                : LocalDateTime.now().format(FORMATO_DATA_HORA_NFCE));
        chave(ini, "tpNF", "1");       // 1 = Saída
        chave(ini, "Finalidade", "1"); // 1 = Normal
        chave(ini, "idDest", "1");     // 1 = Operação interna
        chave(ini, "indFinal", "1");   // 1 = Consumidor final
        chave(ini, "indPres", "0");    // 0 = Não presencial (venda pela loja virtual, diferente da origem que era balcão físico)
        chave(ini, "tpImp", "4");      // 4 = DANFE NFC-e
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

    private void secaoDestinatario(StringBuilder ini, PedidoLojaClienteDTO pedido) {
        secao(ini, "Destinatario");
        String cpf = pedido.clienteCpf();
        chave(ini, cpf.length() > 11 ? "CNPJ" : "CPF", cpf);
        chave(ini, "NomeRazao", pedido.clienteNome());
    }

    private void secaoProduto(StringBuilder ini, NotaFiscalConsumidorItem item) {
        secao(ini, "Produto" + pad(item.getNumeroItem()));
        chave(ini, "CFOP", CFOP_PADRAO);
        chave(ini, "Codigo", str(item.getVarianteProdutoLojaId()));
        chave(ini, "cEAN", "SEM GTIN");
        chave(ini, "Descricao", item.getDescricao());
        chave(ini, "NCM", item.getNcm());
        chave(ini, "Unidade", "UN");
        chave(ini, "Quantidade", str(item.getQuantidade()));
        chave(ini, "ValorUnitario", str(item.getValorUnitario()));
        chave(ini, "ValorTotal", str(item.getValorTotal()));
        chave(ini, "cEANTrib", "SEM GTIN");
        chave(ini, "uTrib", "UN");
        chave(ini, "qTrib", str(item.getQuantidade()));
        chave(ini, "vUnTrib", str(item.getValorUnitario()));
    }

    /** Só CSOSN (Simples Nacional) — ver Javadoc da classe. */
    private void secaoIcms(StringBuilder ini, NotaFiscalConsumidorItem item) {
        secao(ini, "ICMS" + pad(item.getNumeroItem()));
        chave(ini, "CSOSN", item.getCsosn());
        chave(ini, "Origem", "0"); // 0 = Nacional — simplificação, ver Javadoc da classe
    }

    private void secaoPis(StringBuilder ini, NotaFiscalConsumidorItem item) {
        secao(ini, "PIS" + pad(item.getNumeroItem()));
        chave(ini, "CST", "01");
        chave(ini, "ValorBase", str(item.getValorTotal()));
    }

    private void secaoCofins(StringBuilder ini, NotaFiscalConsumidorItem item) {
        secao(ini, "COFINS" + pad(item.getNumeroItem()));
        chave(ini, "CST", "01");
        chave(ini, "ValorBase", str(item.getValorTotal()));
    }

    private void secaoTotal(StringBuilder ini, NotaFiscalConsumidor nota) {
        secao(ini, "Total");
        chave(ini, "ValorProduto", str(nota.getItens().stream().map(NotaFiscalConsumidorItem::getValorTotal)
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
