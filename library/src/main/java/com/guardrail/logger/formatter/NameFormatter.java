package com.guardrail.logger.formatter;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;

/**
 * Formatador específico para nomes de pessoas.
 * 
 * <p>Este formatador aplica mascaramento parcial ao nome, mantendo
 * as iniciais ou parte do primeiro e último nome visíveis.</p>
 * 
 * <p>Exemplo: "JOSE DA SILVA" → "J*** D* S****"</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class NameFormatter implements Formatter {

    private final int visibleCharsPerWord;
    private final char maskChar;
    private final boolean preserveInitials;

    /**
     * Cria um formatador de nome com configurações padrão.
     */
    public NameFormatter() {
        this(1, '*', true);
    }

    /**
     * Cria um formatador de nome customizado.
     *
     * @param visibleCharsPerWord caracteres visíveis por palavra
     * @param maskChar caractere de máscara
     * @param preserveInitials se deve preservar apenas as iniciais
     */
    public NameFormatter(int visibleCharsPerWord, char maskChar, boolean preserveInitials) {
        this.visibleCharsPerWord = Math.max(0, visibleCharsPerWord);
        this.maskChar = maskChar;
        this.preserveInitials = preserveInitials;
    }

    @Override
    public String format(String value) {
        if (!isValid(value)) {
            return String.valueOf(maskChar).repeat(3);
        }

        String[] words = value.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }

            String word = words[i];
            if (word.isEmpty()) {
                continue;
            }

            if (preserveInitials) {
                // Mostra apenas a inicial
                result.append(word.charAt(0));
                if (word.length() > 1) {
                    result.append(String.valueOf(maskChar).repeat(word.length() - 1));
                }
            } else {
                // Mostra os primeiros N caracteres
                int visible = Math.min(visibleCharsPerWord, word.length());
                result.append(word, 0, visible);
                if (word.length() > visible) {
                    result.append(String.valueOf(maskChar).repeat(word.length() - visible));
                }
            }
        }

        return result.toString();
    }

    @Override
    public DataType getDataType() {
        return DataType.NAME;
    }

    @Override
    public String getName() {
        return "nameFormatter";
    }

    @Override
    public boolean isValid(String value) {
        return value != null && !value.isBlank();
    }
}
