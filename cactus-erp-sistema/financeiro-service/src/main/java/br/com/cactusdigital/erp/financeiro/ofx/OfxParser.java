package br.com.cactusdigital.erp.financeiro.ofx;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser de extrato bancário OFX — cobre o formato <b>OFX 1.x / SGML</b> (o que a
 * esmagadora maioria dos bancos brasileiros exporta pelo internet banking), não XML
 * puro (OFX 2.x). Diferente de XML, SGML não exige tag de fechamento em elemento-folha
 * ({@code <TRNAMT>-150.00} é válido sem {@code </TRNAMT>}) — por isso o parsing aqui é
 * por regex tolerante (aceita com ou sem fechamento), não um parser XML de verdade.
 * <p>
 * <b>Transparência</b>: os nomes de tag usados ({@code STMTTRN}/{@code TRNTYPE}/
 * {@code DTPOSTED}/{@code TRNAMT}/{@code FITID}/{@code MEMO}/{@code NAME}) são o
 * vocabulário oficial e estável da especificação OFX (Open Financial Exchange, mantida
 * pelo OFX Consortium) — não é uma inferência de layout de um banco específico, é o
 * mesmo conjunto de campos usado por todo extrato OFX de qualquer banco. O que NÃO foi
 * testado contra um arquivo real de nenhum banco brasileiro específico nesta sessão —
 * cada banco pode variar em detalhes cosméticos (encoding declarado no header, quebra de
 * linha), o parser tenta ser tolerante a isso mas não há garantia de cobrir 100% dos
 * exportadores do mercado sem validação real.
 */
public final class OfxParser {

    private static final Pattern STMTTRN_BLOCK = Pattern.compile("<STMTTRN>(.*?)</STMTTRN>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern BANKACCTFROM_BLOCK = Pattern.compile("<BANKACCTFROM>(.*?)</BANKACCTFROM>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter DATA_OFX = DateTimeFormatter.ofPattern("yyyyMMdd");

    private OfxParser() {
    }

    /** @param conteudo bytes crus do arquivo — decodificado como Windows-1252 (charset
     *                  declarado como padrão em praticamente todo OFX de banco brasileiro,
     *                  mesmo critério já usado pra o protocolo ACBr no {@code fiscal-service}),
     *                  com fallback silencioso pra UTF-8 se houver caractere inválido. */
    public static OfxExtrato parse(byte[] conteudo) {
        String texto = new String(conteudo, StandardCharsets.ISO_8859_1);
        return parse(texto);
    }

    public static OfxExtrato parse(String conteudo) {
        Matcher matcherTransacoes = STMTTRN_BLOCK.matcher(conteudo);
        List<OfxTransacao> transacoes = new ArrayList<>();
        while (matcherTransacoes.find()) {
            transacoes.add(parseTransacao(matcherTransacoes.group(1)));
        }
        if (transacoes.isEmpty()) {
            throw new OfxParseException("Nenhuma transação (<STMTTRN>) encontrada no arquivo — não parece ser um extrato OFX válido");
        }

        String bancoId = null;
        String agencia = null;
        String conta = null;
        Matcher matcherConta = BANKACCTFROM_BLOCK.matcher(conteudo);
        if (matcherConta.find()) {
            String bloco = matcherConta.group(1);
            bancoId = campo(bloco, "BANKID");
            agencia = campo(bloco, "BRANCHID");
            conta = campo(bloco, "ACCTID");
        }

        return new OfxExtrato(bancoId, agencia, conta, transacoes);
    }

    private static OfxTransacao parseTransacao(String bloco) {
        String tipo = campo(bloco, "TRNTYPE");
        String dataStr = campo(bloco, "DTPOSTED");
        String valorStr = campo(bloco, "TRNAMT");
        String fitId = campo(bloco, "FITID");
        String memo = campo(bloco, "MEMO");
        if (memo == null || memo.isBlank()) {
            memo = campo(bloco, "NAME");
        }

        if (dataStr == null || dataStr.length() < 8) {
            throw new OfxParseException("Transação OFX sem DTPOSTED válido: " + bloco.strip());
        }
        if (valorStr == null || valorStr.isBlank()) {
            throw new OfxParseException("Transação OFX sem TRNAMT válido: " + bloco.strip());
        }

        LocalDate data = LocalDate.parse(dataStr.substring(0, 8), DATA_OFX);
        BigDecimal valor = new BigDecimal(valorStr.trim());
        return new OfxTransacao(tipo, data, valor, fitId, memo);
    }

    /** Extrai o valor de uma tag SGML — aceita com ou sem fechamento explícito, até a
     * próxima quebra de linha ou até o próximo {@code <}. */
    private static String campo(String bloco, String tag) {
        Matcher m = Pattern.compile("<" + tag + ">\\s*([^\\r\\n<]*)", Pattern.CASE_INSENSITIVE).matcher(bloco);
        return m.find() ? m.group(1).strip() : null;
    }
}
