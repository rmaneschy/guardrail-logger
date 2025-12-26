package com.guardrail.logger.formatter;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;

/**
 * Formatador específico para números de cartão de crédito.
 * 
 * <p>Este formatador aplica mascaramento ao cartão de crédito, mantendo
 * apenas os últimos 4 dígitos visíveis conforme padrão PCI-DSS.</p>
 * 
 * <p>Exemplo: "4111111111111111" → "************1111"</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class CreditCardFormatter implements Formatter {

    private final int visibleDigitsEnd;
    private final char maskChar;
    private final boolean formatted;

    /**
     * Cria um formatador de cartão de crédito com configurações padrão.
     * Mantém os últimos 4 dígitos visíveis.
     */
    public CreditCardFormatter() {
        this(4, '*', false);
    }

    /**
     * Cria um formatador de cartão de crédito customizado.
     *
     * @param visibleDigitsEnd dígitos visíveis no final
     * @param maskChar caractere de máscara
     * @param formatted se deve manter formatação (XXXX-XXXX-XXXX-XXXX)
     */
    public CreditCardFormatter(int visibleDigitsEnd, char maskChar, boolean formatted) {
        this.visibleDigitsEnd = Math.max(0, Math.min(4, visibleDigitsEnd));
        this.maskChar = maskChar;
        this.formatted = formatted;
    }

    @Override
    public String format(String value) {
        if (!isValid(value)) {
            return String.valueOf(maskChar).repeat(4);
        }

        // Remove formatação existente
        String cleanValue = value.replaceAll("[^0-9]", "");

        if (cleanValue.length() < 13 || cleanValue.length() > 19) {
            return String.valueOf(maskChar).repeat(4);
        }

        int maskLength = cleanValue.length() - visibleDigitsEnd;
        String masked = String.valueOf(maskChar).repeat(maskLength);
        String visible = cleanValue.substring(maskLength);

        if (formatted && cleanValue.length() == 16) {
            // Formato: ****-****-****-1111
            final var result = new StringBuilder();
            result.append(maskChar).append(maskChar).append(maskChar).append(maskChar);
            result.append("-");
            result.append(maskChar).append(maskChar).append(maskChar).append(maskChar);
            result.append("-");
            result.append(maskChar).append(maskChar).append(maskChar).append(maskChar);
            result.append("-");
            result.append(visible);
            return result.toString();
        }

        return masked + visible;
    }

    @Override
    public DataType getDataType() {
        return DataType.CREDIT_CARD;
    }

    @Override
    public String getName() {
        return "creditCardFormatter";
    }

    @Override
    public boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        final var cleanValue = value.replaceAll("[^0-9]", "");
        return cleanValue.length() >= 13 && cleanValue.length() <= 19;
    }
}
