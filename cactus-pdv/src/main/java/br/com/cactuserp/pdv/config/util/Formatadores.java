package br.com.cactuserp.pdv.config.util;

public final class Formatadores {

    private Formatadores() {}

    public static String cpfCnpj(String doc) {
        if (doc == null || doc.isBlank()) return "";
        String n = doc.replaceAll("\\D", "");
        if (n.length() == 11) {
            return n.substring(0, 3) + "." + n.substring(3, 6) + "." +
                    n.substring(6, 9) + "-" + n.substring(9);
        }
        if (n.length() == 14) {
            return n.substring(0, 2) + "." + n.substring(2, 5) + "." +
                    n.substring(5, 8) + "/" + n.substring(8, 12) + "-" + n.substring(12);
        }
        return doc;
    }

    public static String telefone(String fone) {
        if (fone == null || fone.isBlank()) return "";
        String n = fone.replaceAll("\\D", "");
        if (n.length() == 11) {
            return "(" + n.substring(0, 2) + ") " + n.substring(2, 7) + "-" + n.substring(7);
        }
        if (n.length() == 10) {
            return "(" + n.substring(0, 2) + ") " + n.substring(2, 6) + "-" + n.substring(6);
        }
        return fone;
    }
}