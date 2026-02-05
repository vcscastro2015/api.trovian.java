package com.trovian.util;

public class TelefoneUtils {

    public static String salvarSemMascara(String telefone) {

        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        String numero = telefone.replaceAll("\\D", "");
        if (numero.startsWith("55") && numero.length() > 11) {
            numero = numero.substring(2);
        }
        return numero;
    }

    public static String aplicarMascara(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        String numero = telefone.replaceAll("\\D", "");
        if (numero.length() == 11) {
            return String.format("(%s)%s-%s",
                    numero.substring(0, 2),
                    numero.substring(2, 7),
                    numero.substring(7));
        }
        if (numero.length() == 10) {
            return String.format("(%s)%s-%s",
                    numero.substring(0, 2),
                    numero.substring(2, 6),
                    numero.substring(6));
        }
        return telefone;
    }

    public static String converterNumeroWpp(String telefoneWpp) {
        if (telefoneWpp == null || telefoneWpp.isBlank()) {
            return null;
        }
        String numero = telefoneWpp.replaceAll("\\D", "");
        if (numero.startsWith("55")) {
            numero = numero.substring(2);
        }
        if (numero.length() == 10) {
            String ddd = numero.substring(0, 2);
            String telefone = numero.substring(2);
            numero = ddd + "9" + telefone;
        }
        return numero;
    }
}
