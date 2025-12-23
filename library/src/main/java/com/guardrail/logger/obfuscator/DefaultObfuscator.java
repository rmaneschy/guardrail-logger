package com.guardrail.logger.obfuscator;

import com.guardrail.logger.core.Obfuscator;

/**
 * Ofuscador padrão que substitui todo o valor por caracteres de máscara.
 * 
 * <p>Este ofuscador é utilizado quando não há um ofuscador específico configurado
 * para o tipo de dado. Ele simplesmente substitui todo o conteúdo pelo caractere
 * de máscara definido.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class DefaultObfuscator implements Obfuscator {

    private final char maskChar;
    private final String defaultMask;

    /**
     * Cria um ofuscador padrão com máscara "***".
     */
    public DefaultObfuscator() {
        this('*', "***");
    }

    /**
     * Cria um ofuscador padrão com caractere de máscara customizado.
     *
     * @param maskChar caractere de máscara
     */
    public DefaultObfuscator(char maskChar) {
        this(maskChar, String.valueOf(maskChar).repeat(3));
    }

    /**
     * Cria um ofuscador padrão com caractere e máscara customizados.
     *
     * @param maskChar caractere de máscara
     * @param defaultMask máscara padrão
     */
    public DefaultObfuscator(char maskChar, String defaultMask) {
        this.maskChar = maskChar;
        this.defaultMask = defaultMask;
    }

    @Override
    public String obfuscate(String value) {
        if (value == null || value.isEmpty()) {
            return defaultMask;
        }
        return defaultMask;
    }

    @Override
    public char getMaskChar() {
        return maskChar;
    }

    /**
     * Retorna a máscara padrão utilizada.
     *
     * @return máscara padrão
     */
    public String getDefaultMask() {
        return defaultMask;
    }
}
