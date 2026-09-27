package br.com.cactuserp.pdv.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

/**
 * Monta sequências de comandos ESC/POS (padrão universal de impressoras
 * térmicas — Epson, Bematech, Elgin, Daruma etc herdam o mesmo conjunto
 * básico de comandos). Lógica pura, sem I/O — o envio de fato é feito por
 * {@link ImpressaoService}.
 *
 * <p>Diferente das integrações de hardware/protocolo proprietário deste
 * projeto (ACBr, balança serial, TEF), ESC/POS é um padrão extremamente
 * estável e documentado há décadas — os comandos abaixo (init, negrito,
 * corte, pulso de gaveta) são universais e têm alta confiança de
 * corretude. A única variável por impressora costuma ser o <b>codepage</b>
 * de acentuação; usa-se {@code ISO-8859-1} por ser o mais comum, mas pode
 * precisar de ajuste (comando de seleção de codepage) por modelo.
 */
public class EscPosBuilder {

    private static final Charset CODEPAGE = Charset.forName("ISO-8859-1");

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    /** ESC @ — reinicializa a impressora (limpa buffer, restaura formatação padrão). */
    public EscPosBuilder inicializar() {
        return escrever(0x1B, 0x40);
    }

    public EscPosBuilder texto(String linha) {
        escreverBytes((linha == null ? "" : linha).getBytes(CODEPAGE));
        return this;
    }

    public EscPosBuilder novaLinha() {
        return escrever(0x0A);
    }

    public EscPosBuilder linha(String texto) {
        return texto(texto).novaLinha();
    }

    /** ESC E n — negrito ligado (n=1) ou desligado (n=0). */
    public EscPosBuilder negrito(boolean ligado) {
        return escrever(0x1B, 0x45, ligado ? 1 : 0);
    }

    /** ESC a n — alinhamento: 0=esquerda, 1=centro, 2=direita. */
    public EscPosBuilder alinhamento(int n) {
        return escrever(0x1B, 0x61, n);
    }

    public EscPosBuilder separador() {
        return linha("------------------------------------------");
    }

    /** GS V 1 — corte parcial de papel (guilhotina). */
    public EscPosBuilder cortarPapel() {
        novaLinha().novaLinha().novaLinha();
        return escrever(0x1D, 0x56, 1);
    }

    /**
     * ESC p m t1 t2 — pulso pra abrir a gaveta de dinheiro conectada na
     * impressora (padrão de mercado: gaveta liga na porta RJ11/RJ12 da
     * impressora térmica, não direto na porta serial do PC).
     *
     * @param pino       0 ou 1 — qual pino do conector dispara (depende do
     *                   cabeamento; a maioria usa 0)
     * @param sinalInvertido ver {@code PDV_CONFIGURACAO.GAVETA_SINAL_INVERTIDO}
     */
    public EscPosBuilder abrirGaveta(int pino, boolean sinalInvertido) {
        int m = sinalInvertido ? (pino == 0 ? 1 : 0) : pino;
        return escrever(0x1B, 0x70, m, 25, 250);
    }

    public byte[] build() {
        return buffer.toByteArray();
    }

    private EscPosBuilder escrever(int... bytes) {
        for (int b : bytes) buffer.write(b);
        return this;
    }

    private void escreverBytes(byte[] bytes) {
        try {
            buffer.write(bytes);
        } catch (IOException e) {
            // ByteArrayOutputStream nunca lança IOException de verdade
            throw new UncheckedIOException(e);
        }
    }
}
