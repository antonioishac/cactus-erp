package br.com.cactuserp.pdv.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Decodifica código de barras EAN-13 com peso/valor embutido (etiqueta de
 * balança — Toledo/Filizola/etc), usando a máscara configurada em
 * {@code PDV_CONFIGURACAO_BALANCA.TIPO_CONFIGURACAO}.
 *
 * <p>É lógica pura (sem I/O) — decodifica uma String de 13 dígitos já
 * lida pelo leitor de código de barras do PDV. Não confundir com leitura
 * serial ao vivo de uma balança de checkout (ver {@code BalancaSerialService}).
 *
 * <h3>Padrão</h3>
 * Código de barras "peso variável" brasileiro: prefixo fixo (normalmente
 * {@code 2}) + código do produto + peso (gramas) ou valor (centavos) +
 * dígito verificador — sempre 13 dígitos ao todo (padrão EAN-13).
 *
 * <p>As 5 máscaras documentadas em {@code PDV.md} usam {@code DV} como
 * <b>rótulo de 2 letras para UMA única posição</b> (dígito verificador),
 * não duas posições separadas — por isso {@link #normalizarMascara}
 * colapsa o sufixo {@code "DV"} num único caractere antes de alinhar
 * com os 13 dígitos do código de barras.
 *
 * <p>⚠️ <b>Assunção não validada contra etiqueta real</b>: peso é tratado
 * como gramas inteiras (sem casas decimais no código de barras — 3 casas
 * decimais implícitas ao converter pra kg). É a convenção mais comum, mas
 * pode variar por balança/configuração regional — validar contra uma
 * etiqueta impressa de verdade antes de usar em produção.
 */
@Component
public class BalancaCodigoDecoder {

    /** Resultado da decodificação — peso OU valor vem preenchido, nunca os dois. */
    public static class LeituraBalanca {
        private final String codigoProduto;
        private final BigDecimal pesoEmKg;       // null se a máscara for de valor (P)
        private final BigDecimal valorEmReais;   // null se a máscara for de peso (T)
        private final boolean digitoVerificadorValido;

        public LeituraBalanca(String codigoProduto, BigDecimal pesoEmKg, BigDecimal valorEmReais,
                              boolean digitoVerificadorValido) {
            this.codigoProduto = codigoProduto;
            this.pesoEmKg = pesoEmKg;
            this.valorEmReais = valorEmReais;
            this.digitoVerificadorValido = digitoVerificadorValido;
        }

        public String getCodigoProduto() { return codigoProduto; }
        public BigDecimal getPesoEmKg() { return pesoEmKg; }
        public BigDecimal getValorEmReais() { return valorEmReais; }
        public boolean isDigitoVerificadorValido() { return digitoVerificadorValido; }
        public boolean ehPorPeso() { return pesoEmKg != null; }
    }

    /**
     * Decodifica um código de barras de 13 dígitos usando a máscara informada.
     *
     * @param codigoBarras 13 dígitos numéricos (ex: "2001234001500{d}")
     * @param mascara      ex: {@code "2CCCCCTTTTTTDV"} (ver PDV_CONFIGURACAO_BALANCA)
     * @return leitura decodificada
     * @throws IllegalArgumentException se código ou máscara forem inválidos/incompatíveis
     */
    public LeituraBalanca decodificar(String codigoBarras, String mascara) {
        if (codigoBarras == null || !codigoBarras.matches("\\d{13}")) {
            throw new IllegalArgumentException("Código de barras deve ter exatamente 13 dígitos numéricos.");
        }
        if (mascara == null || mascara.isBlank()) {
            throw new IllegalArgumentException("Máscara (TIPO_CONFIGURACAO) não informada.");
        }

        String m = normalizarMascara(mascara);
        if (m.length() != 13) {
            throw new IllegalArgumentException(
                    "Máscara normalizada deveria ter 13 posições, tem " + m.length() + ": " + mascara);
        }

        StringBuilder codigo = new StringBuilder();
        StringBuilder valorBloco = new StringBuilder();
        char tipoBloco = 0; // 'T' (peso) ou 'P' (valor)

        for (int i = 0; i < 13; i++) {
            char mc = m.charAt(i);
            char cc = codigoBarras.charAt(i);
            switch (mc) {
                case 'C' -> codigo.append(cc);
                case 'T', 'P' -> {
                    if (tipoBloco == 0) tipoBloco = mc;
                    valorBloco.append(cc);
                }
                case 'X' -> { /* dígito verificador — validado separadamente */ }
                default -> {
                    // Caractere literal da máscara (ex: prefixo '2' ou zeros de
                    // preenchimento) — deve bater exatamente com o código lido.
                    if (mc != cc) {
                        throw new IllegalArgumentException(
                                "Código de barras não corresponde à máscara na posição " + i
                                        + " (esperado '" + mc + "', veio '" + cc + "').");
                    }
                }
            }
        }

        boolean checkDigitOk = validarDigitoVerificador(codigoBarras);

        BigDecimal peso = null;
        BigDecimal valor = null;
        if (tipoBloco == 'T') {
            // Peso em gramas -> kg (3 casas decimais implícitas)
            peso = new BigDecimal(valorBloco.toString())
                    .divide(BigDecimal.valueOf(1000), 3, RoundingMode.UNNECESSARY);
        } else if (tipoBloco == 'P') {
            // Valor em centavos -> reais (2 casas decimais implícitas)
            valor = new BigDecimal(valorBloco.toString())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY);
        }

        return new LeituraBalanca(codigo.toString(), peso, valor, checkDigitOk);
    }

    /**
     * Colapsa o sufixo de 2 letras {@code "DV"} (rótulo de "Dígito
     * Verificador") num único caractere sentinela {@code 'X'}, alinhando a
     * máscara com os 13 dígitos reais do código de barras.
     */
    private String normalizarMascara(String mascara) {
        String m = mascara.trim().toUpperCase();
        if (m.endsWith("DV")) {
            return m.substring(0, m.length() - 2) + "X";
        }
        return m;
    }

    /**
     * Valida o dígito verificador padrão EAN-13 (módulo 10, pesos 1/3
     * alternados a partir da esquerda).
     */
    private boolean validarDigitoVerificador(String codigoBarras13) {
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            int digito = codigoBarras13.charAt(i) - '0';
            soma += (i % 2 == 0) ? digito : digito * 3;
        }
        int esperado = (10 - (soma % 10)) % 10;
        int informado = codigoBarras13.charAt(12) - '0';
        return esperado == informado;
    }
}
