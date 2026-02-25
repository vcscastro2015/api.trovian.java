package com.trovian.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class TextoUtils {

    private static final Pattern PADRAO_NAO =
            Pattern.compile("\\bnao\\b");

    public static boolean contemNao(String frase) {
        if (frase == null || frase.isBlank()) {
            return false;
        }
        String normalizada = Normalizer.normalize(frase, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        normalizada = normalizada.toLowerCase();
        return PADRAO_NAO.matcher(normalizada).find();
    }
}
