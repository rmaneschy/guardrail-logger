package com.guardrail.logger.formatter;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;

/**
 * Formatador específico para CPF (Cadastro de Pessoa Física).
 * 
 * <p>Este formatador aplica mascaramento parcial ao CPF, mantendo alguns
 * dígitos visíveis para identificação parcial enquanto protege a maior
 * parte do documento.</p>
 * 
 * <p>Exemplo: "12345678909" → "***456789**" ou "123.***.***-**"</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class CpfFormatter implements Formatter {

    private final int visibleDigitsStart;
    private final int visibleDigitsMiddle;
    private final char maskChar;
    private final boolean formatted;

    /**
     * Cria um formatador de CPF com configurações padrão.
     * Mantém 3 dígitos visíveis no meio.
     */
    public CpfFormatter() {
        this(0, 6, '*', false);
    }

    /**
     * Cria um formatador de CPF customizado.
     *
     * @param visibleDigitsStart dígitos visíveis no início
     * @param visibleDigitsMiddle dígitos visíveis no meio
     * @param maskChar caractere de máscara
     * @param formatted se deve manter formatação (XXX.XXX.XXX-XX)
     */
    public CpfFormatter(int visibleDigitsStart, int visibleDigitsMiddle, char maskChar, boolean formatted) {
        this.visibleDigitsStart = visibleDigitsStart;
        this.visibleDigitsMiddle = visibleDigitsMiddle;
        this.maskChar = maskChar;
        this.formatted = formatted;
    }

    @Override
    public String format(String value) {
        if (!isValid(value)) {
            return String.valueOf(maskChar).repeat(3);
        }

        // Remove formatação existente
        String cleanValue = value.replaceAll("[^0-9]", "");

        if (cleanValue.length() != 11) {
            return String.valueOf(maskChar).repeat(3);
        }

        StringBuilder result = new StringBuilder();

        if (formatted) {
            // Formato: XXX.XXX.XXX-XX
            result.append(maskChar).append(maskChar).append(maskChar);
            result.append(".");
            result.append(cleanValue, 3, 6);
            result.append(".");
            result.append(cleanValue, 6, 9);
            result.append("-");
            result.append(maskChar).append(maskChar);
        } else {
            // Formato simples: ***456789**
            result.append(String.valueOf(maskChar).repeat(3));
            result.append(cleanValue, 3, 9);
            result.append(String.valueOf(maskChar).repeat(2));
        }

        return result.toString();
    }

    @Override
    public DataType getDataType() {
        return DataType.CPF;
    }

    @Override
    public String getName() {
        return "cpfFormatter";
    }

    @Override
    public boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String cleanValue = value.replaceAll("[^0-9]", "");
        return cleanValue.length() == 11;
    }
}
