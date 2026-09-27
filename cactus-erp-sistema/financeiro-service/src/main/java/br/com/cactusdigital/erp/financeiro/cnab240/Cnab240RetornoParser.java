package br.com.cactusdigital.erp.financeiro.cnab240;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser de arquivo de retorno CNAB 240 de cobrança — lê o par de segmentos
 * {@code T}+{@code U} de cada título (convenção padrão: T sempre imediatamente seguido do
 * U correspondente) usando só {@code tipoRegistro} (posição 8) e {@code codigoSegmento}
 * (posição 14) pra rotear cada linha, sem precisar parsear header/trailer de arquivo/lote
 * (que não carregam informação relevante pra baixa de título). Posições exatas verificadas
 * contra a especificação FEBRABAN/CAIXA (ver Javadoc de {@link Cnab240RemessaBuilder} pro
 * mesmo disclaimer de confiança).
 */
public final class Cnab240RetornoParser {

    private static final DateTimeFormatter DATA_CNAB = DateTimeFormatter.ofPattern("ddMMyyyy");

    private Cnab240RetornoParser() {
    }

    public static List<Cnab240RetornoRegistro> parse(String conteudo) {
        String[] linhas = conteudo.split("\r\n|\n|\r");
        List<Cnab240RetornoRegistro> registros = new ArrayList<>();

        String nossoNumeroPendente = null;
        String numeroDocumentoPendente = null;
        LocalDate vencimentoPendente = null;
        BigDecimal valorTituloPendente = null;
        String codigoMovimentoPendente = null;

        for (String linha : linhas) {
            if (linha.length() < 240) {
                continue;
            }
            if (linha.charAt(7) != '3') {
                continue;
            }
            char segmento = linha.charAt(13);
            if (segmento == 'T') {
                nossoNumeroPendente = campoTexto(linha, 38, 57);
                numeroDocumentoPendente = campoTexto(linha, 59, 73);
                vencimentoPendente = campoData(linha, 74, 81);
                valorTituloPendente = campoValor(linha, 82, 96);
                codigoMovimentoPendente = campoTexto(linha, 16, 17);
            } else if (segmento == 'U' && nossoNumeroPendente != null) {
                BigDecimal valorPago = campoValor(linha, 78, 92);
                LocalDate dataOcorrencia = campoData(linha, 138, 145);
                registros.add(new Cnab240RetornoRegistro(
                        nossoNumeroPendente, numeroDocumentoPendente, vencimentoPendente,
                        valorTituloPendente, codigoMovimentoPendente, dataOcorrencia, valorPago));
                nossoNumeroPendente = null;
            }
        }

        if (registros.isEmpty()) {
            throw new Cnab240ParseException("Nenhum par de segmentos T+U encontrado — não parece ser um arquivo de retorno CNAB240 de cobrança válido");
        }
        return registros;
    }

    private static String campoTexto(String linha, int posInicio, int posFim) {
        return linha.substring(posInicio - 1, posFim).trim();
    }

    private static LocalDate campoData(String linha, int posInicio, int posFim) {
        String v = campoTexto(linha, posInicio, posFim);
        if (v.isBlank() || v.chars().allMatch(c -> c == '0')) {
            return null;
        }
        try {
            return LocalDate.parse(v, DATA_CNAB);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static BigDecimal campoValor(String linha, int posInicio, int posFim) {
        String v = campoTexto(linha, posInicio, posFim);
        if (v.isBlank()) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(Long.parseLong(v), 2);
    }
}
