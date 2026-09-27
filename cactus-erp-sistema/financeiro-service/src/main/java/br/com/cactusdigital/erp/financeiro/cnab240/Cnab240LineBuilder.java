package br.com.cactusdigital.erp.financeiro.cnab240;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Monta uma linha de 240 posições campo a campo — cada método {@code texto}/{@code numero}/
 * {@code valorMonetario}/{@code data}/{@code brancos}/{@code zeros} escreve exatamente
 * {@code tamanho} caracteres (trunca texto longo demais, preenche à esquerda com zero pra
 * número, à direita com espaço pra texto — layout posicional fixo, sem separador).
 * <p>
 * {@link #build()} é a rede de segurança central deste módulo: se a soma dos tamanhos não
 * fechar em exatamente 240, lança exceção IMEDIATAMENTE (ali mesmo, na hora de montar o
 * registro) em vez de deixar passar uma linha malformada — transforma um bug de aritmética
 * de posição (contar campo errado) em erro alto e óbvio no `mvn compile`/teste, nunca um
 * arquivo CNAB corrompido silenciosamente enviado a um banco de verdade.
 */
public final class Cnab240LineBuilder {

    private static final DateTimeFormatter DATA_CNAB = DateTimeFormatter.ofPattern("ddMMyyyy");

    private final StringBuilder sb = new StringBuilder(240);

    public Cnab240LineBuilder texto(String valor, int tamanho) {
        String v = valor == null ? "" : valor;
        if (v.length() > tamanho) {
            v = v.substring(0, tamanho);
        }
        sb.append(String.format("%-" + tamanho + "s", v));
        return this;
    }

    /**
     * Diferente de {@link #texto}, NUNCA trunca — um campo numérico é código/identificador
     * (carteira, agência, nosso número, valor monetário), não texto de exibição. Truncar
     * silenciosamente da esquerda mudaria o SIGNIFICADO do dado (ex: carteira "17" virar
     * "7" é um código bancário diferente, não uma versão abreviada do mesmo código) —
     * achado real durante a validação ao vivo desta fase (carteira de 2 dígitos
     * configurada, campo do layout CAIXA/genérico só comporta 1 dígito). Lança exceção
     * imediatamente em vez de gerar um arquivo CNAB sutilmente errado.
     */
    public Cnab240LineBuilder numero(long valor, int tamanho) {
        String v = Long.toString(Math.max(valor, 0));
        if (v.length() > tamanho) {
            throw new IllegalArgumentException(
                    "Valor numérico " + valor + " não cabe em " + tamanho + " posições do campo CNAB240 — configuração incompatível com este layout, não um bug de arredondamento");
        }
        sb.append(String.format("%" + tamanho + "s", v).replace(' ', '0'));
        return this;
    }

    /** Valor monetário em centavos (implícito, sem separador decimal — 2 casas), padrão
     * {@code 9(n)V9(2)} do CNAB240. */
    public Cnab240LineBuilder valorMonetario(BigDecimal valor, int tamanho) {
        BigDecimal v = valor == null ? BigDecimal.ZERO : valor;
        long centavos = v.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
        return numero(centavos, tamanho);
    }

    public Cnab240LineBuilder data(LocalDate data, int tamanho) {
        return texto(data == null ? "" : data.format(DATA_CNAB), tamanho);
    }

    public Cnab240LineBuilder brancos(int tamanho) {
        return texto("", tamanho);
    }

    public Cnab240LineBuilder zeros(int tamanho) {
        return numero(0, tamanho);
    }

    public String build() {
        String linha = sb.toString();
        if (linha.length() != 240) {
            throw new IllegalStateException(
                    "Linha CNAB240 com " + linha.length() + " posições (esperado 240) — bug de soma de campos no builder, não no dado: " + linha);
        }
        return linha;
    }
}
