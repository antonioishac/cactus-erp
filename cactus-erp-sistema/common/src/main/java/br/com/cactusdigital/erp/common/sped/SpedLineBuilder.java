package br.com.cactusdigital.erp.common.sped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Monta uma linha de registro de qualquer arquivo digital SPED (ECD, EFD ICMS/IPI, EFD
 * Contribuições) — formato delimitado por {@code |} (pipe), diferente do posicional fixo
 * do CNAB 240 (ver {@code Cnab240LineBuilder} no financeiro-service). Toda linha SPED
 * nasce e termina com {@code |}, campos separados por {@code |} — confirmado por
 * múltiplas fontes secundárias pra ECD/EFD ICMS-IPI/EFD Contribuições (ver ANÁLISE:
 * MÓDULO CONTÁBIL E FISCAL no CLAUDE.md raiz), nunca validado contra o validador PVA
 * oficial da Receita nesta sessão.
 * <p>
 * Reutilizado por {@code contabilidade-service} (SPED Contábil/ECD, Fase 14) e
 * {@code fiscal-service} (SPED Fiscal — EFD ICMS/IPI e EFD Contribuições, Fase 15) — vive
 * no {@code common} de propósito, mesmo critério já usado pra
 * {@code common.fiscal.AcbrMonitorClient} (transporte compartilhado entre módulos que
 * falam com o mesmo tipo de destino externo).
 */
public final class SpedLineBuilder {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("ddMMyyyy");

    private final List<String> campos = new ArrayList<>();

    public SpedLineBuilder campo(String valor) {
        campos.add(valor == null ? "" : valor);
        return this;
    }

    public SpedLineBuilder campo(Object valor) {
        campos.add(valor == null ? "" : valor.toString());
        return this;
    }

    public SpedLineBuilder data(LocalDate data) {
        campos.add(data == null ? "" : data.format(FORMATO_DATA));
        return this;
    }

    /** SPED usa vírgula como separador decimal, sem separador de milhar. */
    public SpedLineBuilder valor(BigDecimal valor) {
        campos.add(valor == null ? "" : valor.setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ','));
        return this;
    }

    public String build() {
        return "|" + String.join("|", campos) + "|";
    }
}
