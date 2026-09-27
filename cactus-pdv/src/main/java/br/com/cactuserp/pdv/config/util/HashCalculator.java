package br.com.cactuserp.pdv.config.util;

import br.com.cactuserp.pdv.entity.PdvVendaCabecalho;
import br.com.cactuserp.pdv.entity.PdvVendaDetalhe;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import br.com.cactuserp.pdv.entity.PdvTotalTipoPagamento;

/**
 * Calcula o hash de integridade (SHA-256) dos registros fiscais do PDV.
 *
 * <p>O hash é gravado em {@code HASH_REGISTRO} das tabelas auditadas e serve
 * como prova de integridade do registro para o fisco (PAF-NFC-e). Em uma
 * auditoria, o fiscal recalcula o hash com a mesma fórmula e compara com
 * o gravado — qualquer divergência indica adulteração.
 *
 * <p><b>A fórmula deste hash é parte do contrato fiscal.</b> Não altere a
 * ordem dos campos ou o separador sem atualizar a documentação técnica do
 * PAF e re-hashear todos os registros existentes.
 *
 * <h3>Fórmula do cabeçalho</h3>
 * <pre>
 *   SHA-256(
 *       id | dataVenda | horaVenda | idMovimento | idColaborador |
 *       valorTotalDocumento | statusVenda
 *   )
 * </pre>
 *
 * <h3>Fórmula do detalhe (item)</h3>
 * <pre>
 *   SHA-256(
 *       id | idCabecalho | item | gtin | quantidade |
 *       valorUnitario | valorTotal | cancelado
 *   )
 * </pre>
 *
 * <p>Separador: pipe ({@code |}). Valores nulos viram string vazia. Valores
 * decimais usam {@code toPlainString()} para evitar notação científica.
 */
public final class HashCalculator {

    /** Separador de campos na string a ser hasheada. */
    private static final String SEP = "|";

    private HashCalculator() {
        // utilitário; não instanciável
    }

    // ====== API pública =====================================================

    /**
     * Calcula o hash SHA-256 do cabeçalho de venda.
     *
     * @param cabecalho cabeçalho já populado com os campos relevantes
     * @return hash em hexadecimal (64 caracteres lowercase)
     */
    public static String hashDoCabecalho(PdvVendaCabecalho cabecalho) {
        if (cabecalho == null) {
            throw new IllegalArgumentException("Cabeçalho não pode ser nulo para cálculo de hash.");
        }
        String s = String.join(SEP,
                str(cabecalho.getId()),
                str(cabecalho.getDataVenda()),
                str(cabecalho.getHoraVenda()),
                str(cabecalho.getPdvMovimento() != null ? cabecalho.getPdvMovimento().getId() : null),
                str(cabecalho.getColaborador() != null ? cabecalho.getColaborador().getId() : null),
                bd(cabecalho.getValorTotalDocumento()),
                str(cabecalho.getStatusVenda())
        );
        return sha256(s);
    }

    /**
     * Calcula o hash SHA-256 do detalhe (item) de venda.
     *
     * @param detalhe detalhe já populado com os campos relevantes
     * @return hash em hexadecimal (64 caracteres lowercase)
     */
    public static String hashDoDetalhe(PdvVendaDetalhe detalhe) {
        if (detalhe == null) {
            throw new IllegalArgumentException("Detalhe não pode ser nulo para cálculo de hash.");
        }
        String s = String.join(SEP,
                str(detalhe.getId()),
                str(detalhe.getPdvVendaCabecalho() != null ? detalhe.getPdvVendaCabecalho().getId() : null),
                str(detalhe.getItem()),
                str(detalhe.getGtin()),
                bd(detalhe.getQuantidade()),
                bd(detalhe.getValorUnitario()),
                bd(detalhe.getValorTotal()),
                str(detalhe.getCancelado())
        );
        return sha256(s);
    }

    // ====== Helpers de formatação ==========================================

    /** Converte qualquer objeto para string segura (null vira "" vazio). */
    private static String str(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /** Formata BigDecimal evitando notação científica e com null-safety. */
    private static String bd(BigDecimal valor) {
        return valor == null ? "" : valor.stripTrailingZeros().toPlainString();
    }

    // ====== Algoritmo =======================================================

    /**
     * Calcula SHA-256 de uma string e retorna em hex lowercase (64 chars).
     */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(64);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 é obrigatório em qualquer JVM padrão — nunca deve cair aqui.
            throw new IllegalStateException("Algoritmo SHA-256 indisponível na JVM", e);
        }
    }

    /**
     * Calcula o hash SHA-256 de um pagamento.
     *
     * <h3>Fórmula do pagamento</h3>
     * <pre>
     *   SHA-256(
     *       id | idCabecalho | idTipoPagamento |
     *       dataVenda | horaVenda | valor |
     *       nsu | rede | cartaoDc | estorno | idDispositivo
     *   )
     * </pre>
     */
    public static String hashDoPagamento(PdvTotalTipoPagamento pagto) {
        if (pagto == null) {
            throw new IllegalArgumentException("Pagamento não pode ser nulo para cálculo de hash.");
        }
        String s = String.join(SEP,
                str(pagto.getId()),
                str(pagto.getIdPdvVendaCabecalho()),
                str(pagto.getPdvTipoPagamento() != null
                        ? pagto.getPdvTipoPagamento().getId() : null),
                str(pagto.getDataVenda()),
                str(pagto.getHoraVenda()),
                bd(pagto.getValor()),
                str(pagto.getNsu()),
                str(pagto.getRede()),
                str(pagto.getCartaoDc()),
                str(pagto.getEstorno()),
                str(pagto.getIdDispositivo())
        );
        return sha256(s);
    }
}