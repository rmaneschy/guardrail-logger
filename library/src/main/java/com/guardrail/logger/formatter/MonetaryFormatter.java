package com.guardrail.logger.formatter;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;

/**
 * Formatador específico para valores monetários.
 * 
 * <p>Este formatador aplica mascaramento a valores monetários, podendo
 * mostrar apenas a ordem de grandeza ou mascarar completamente.</p>
 * 
 * <p>Exemplo: "56789.98" → "*****.98" ou "5****.98"</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class MonetaryFormatter implements Formatter {

    private final boolean showMagnitude;
    private final boolean showDecimals;
    private final char maskChar;

    /**
     * Cria um formatador monetário com configurações padrão.
     */
    public MonetaryFormatter() {
        this(false, false, '*');
    }

    /**
     * Cria um formatador monetário customizado.
     *
     * @param showMagnitude se deve mostrar a ordem de grandeza (primeiro dígito)
     * @param showDecimals se deve mostrar os decimais
     * @param maskChar caractere de máscara
     */
    public MonetaryFormatter(boolean showMagnitude, boolean showDecimals, char maskChar) {
        this.showMagnitude = showMagnitude;
        this.showDecimals = showDecimals;
        this.maskChar = maskChar;
    }

    @Override
    public String format(String value) {
        if (!isValid(value)) {
            return String.valueOf(maskChar).repeat(3);
        }

        // Normaliza separadores decimais
        final var normalizedValue = value.replace(",", ".");

        String integerPart;
        String decimalPart = "";
        
        int dotIndex = normalizedValue.lastIndexOf('.');
        if (dotIndex > 0) {
            integerPart = normalizedValue.substring(0, dotIndex);
            decimalPart = normalizedValue.substring(dotIndex);
        } else {
            integerPart = normalizedValue;
        }

        // Remove caracteres não numéricos da parte inteira
        final var cleanInteger = integerPart.replaceAll("[^0-9]", "");

        final var result = new StringBuilder();

        if (showMagnitude && !cleanInteger.isEmpty()) {
            result.append(cleanInteger.charAt(0));
            result.append(String.valueOf(maskChar).repeat(cleanInteger.length() - 1));
        } else {
            result.append(String.valueOf(maskChar).repeat(Math.max(1, cleanInteger.length())));
        }

        if (!decimalPart.isEmpty()) {
            if (showDecimals) {
                result.append(decimalPart);
            } else {
                result.append(".");
                result.append(String.valueOf(maskChar).repeat(decimalPart.length() - 1));
            }
        }

        return result.toString();
    }

    @Override
    public DataType getDataType() {
        return DataType.MONETARY;
    }

    @Override
    public String getName() {
        return "monetaryFormatter";
    }

    @Override
    public boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        final var cleanValue = value.replaceAll("[^0-9.,]", "");
        return !cleanValue.isEmpty();
    }
}
