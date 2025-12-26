package com.guardrail.logger.formatter;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;

/**
 * Formatador específico para números de telefone.
 * 
 * <p>Este formatador aplica mascaramento parcial ao telefone, mantendo
 * o DDD e os últimos dígitos visíveis.</p>
 * 
 * <p>Exemplo: "(11) 98765-4321" → "(11) *****-4321"</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class PhoneFormatter implements Formatter {

    private final int visibleDigitsEnd;
    private final boolean showDdd;
    private final char maskChar;

    /**
     * Cria um formatador de telefone com configurações padrão.
     */
    public PhoneFormatter() {
        this(4, true, '*');
    }

    /**
     * Cria um formatador de telefone customizado.
     *
     * @param visibleDigitsEnd dígitos visíveis no final
     * @param showDdd se deve mostrar o DDD
     * @param maskChar caractere de máscara
     */
    public PhoneFormatter(int visibleDigitsEnd, boolean showDdd, char maskChar) {
        this.visibleDigitsEnd = Math.max(0, visibleDigitsEnd);
        this.showDdd = showDdd;
        this.maskChar = maskChar;
    }

    @Override
    public String format(String value) {
        if (!isValid(value)) {
            return String.valueOf(maskChar).repeat(3);
        }

        // Remove formatação existente
        final var cleanValue = value.replaceAll("[^0-9]", "");

        if (cleanValue.length() < 10 || cleanValue.length() > 11) {
            return String.valueOf(maskChar).repeat(3);
        }

        final var result = new StringBuilder();

        if (showDdd) {
            result.append("(").append(cleanValue, 0, 2).append(") ");
            final var number = cleanValue.substring(2);
            int maskLength = number.length() - visibleDigitsEnd;
            result.append(String.valueOf(maskChar).repeat(Math.max(0, maskLength)));
            if (visibleDigitsEnd > 0 && number.length() > maskLength) {
                result.append("-");
                result.append(number.substring(Math.max(0, maskLength)));
            }
        } else {
            result.append("(**) ");
            final var number = cleanValue.substring(2);
            int maskLength = number.length() - visibleDigitsEnd;
            result.append(String.valueOf(maskChar).repeat(Math.max(0, maskLength)));
            if (visibleDigitsEnd > 0 && number.length() > maskLength) {
                result.append("-");
                result.append(number.substring(Math.max(0, maskLength)));
            }
        }

        return result.toString();
    }

    @Override
    public DataType getDataType() {
        return DataType.PHONE;
    }

    @Override
    public String getName() {
        return "phoneFormatter";
    }

    @Override
    public boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        final var cleanValue = value.replaceAll("[^0-9]", "");
        return cleanValue.length() >= 10 && cleanValue.length() <= 11;
    }
}
