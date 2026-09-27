package br.com.cactuserp.pdv.config.util;

import javafx.scene.control.TextFormatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class FormaterCurrency {

    private static final String LANGUAGE = "pt";
    private static final String NATIONALITY = "BR";
    private static final String FORMAT = "#,##0.00";

    private FormaterCurrency() {}

    public static String fieldFormated(BigDecimal value) {

        Locale nationalCurrency = new Locale.Builder().setLanguage(LANGUAGE).setRegion(NATIONALITY).build();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(nationalCurrency);

        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat formater = new DecimalFormat(FORMAT, symbols);

        return formater.format(value);
    }

    public static BigDecimal fieldUnFormated(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }

        String converterValue = value.replace(".", "").replace(",", ".");
        return new BigDecimal(converterValue);
    }

    public static UnaryOperator<TextFormatter.Change> filterDecimal() {
        return change -> {
            String textoNovo = change.getControlNewText();
            return textoNovo.matches("\\d*(,\\d*)?") ? change : null;
        };
    }


}
