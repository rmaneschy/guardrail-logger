package com.guardrail.logger.obfuscator;

import com.guardrail.logger.core.Obfuscator;

/**
 * Ofuscador parcial que mantém parte do valor visível.
 * 
 * <p>Este ofuscador permite configurar quantos caracteres devem permanecer
 * visíveis no início e no final do valor, mascarando o restante.</p>
 * 
 * <p>Exemplo: "12345678909" com visibleStart=3 e visibleEnd=2 resulta em "123******09"</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class PartialObfuscator implements Obfuscator {

    private final int visibleCharsStart;
    private final int visibleCharsEnd;
    private final char maskChar;
    private final int minMaskLength;

    /**
     * Cria um ofuscador parcial com configurações padrão.
     * Mantém 3 caracteres no início e 2 no final.
     */
    public PartialObfuscator() {
        this(3, 2, '*', 3);
    }

    /**
     * Cria um ofuscador parcial com número de caracteres visíveis customizado.
     *
     * @param visibleCharsStart caracteres visíveis no início
     * @param visibleCharsEnd caracteres visíveis no final
     */
    public PartialObfuscator(int visibleCharsStart, int visibleCharsEnd) {
        this(visibleCharsStart, visibleCharsEnd, '*', 3);
    }

    /**
     * Cria um ofuscador parcial totalmente customizado.
     *
     * @param visibleCharsStart caracteres visíveis no início
     * @param visibleCharsEnd caracteres visíveis no final
     * @param maskChar caractere de máscara
     * @param minMaskLength comprimento mínimo da máscara
     */
    public PartialObfuscator(int visibleCharsStart, int visibleCharsEnd, char maskChar, int minMaskLength) {
        this.visibleCharsStart = Math.max(0, visibleCharsStart);
        this.visibleCharsEnd = Math.max(0, visibleCharsEnd);
        this.maskChar = maskChar;
        this.minMaskLength = Math.max(1, minMaskLength);
    }

    @Override
    public String obfuscate(String value) {
        if (value == null || value.isEmpty()) {
            return String.valueOf(maskChar).repeat(minMaskLength);
        }

        int length = value.length();
        int totalVisible = visibleCharsStart + visibleCharsEnd;

        // Se o valor é menor ou igual ao total de caracteres visíveis, mascara tudo
        if (length <= totalVisible) {
            return String.valueOf(maskChar).repeat(Math.max(length, minMaskLength));
        }

        StringBuilder result = new StringBuilder();

        // Adiciona caracteres visíveis do início
        if (visibleCharsStart > 0) {
            result.append(value, 0, Math.min(visibleCharsStart, length));
        }

        // Adiciona máscara no meio
        int maskLength = length - totalVisible;
        result.append(String.valueOf(maskChar).repeat(Math.max(maskLength, minMaskLength)));

        // Adiciona caracteres visíveis do final
        if (visibleCharsEnd > 0 && length > visibleCharsStart) {
            int startIndex = Math.max(visibleCharsStart, length - visibleCharsEnd);
            result.append(value.substring(startIndex));
        }

        return result.toString();
    }

    @Override
    public char getMaskChar() {
        return maskChar;
    }

    public int getVisibleCharsStart() {
        return visibleCharsStart;
    }

    public int getVisibleCharsEnd() {
        return visibleCharsEnd;
    }

    public int getMinMaskLength() {
        return minMaskLength;
    }
}
