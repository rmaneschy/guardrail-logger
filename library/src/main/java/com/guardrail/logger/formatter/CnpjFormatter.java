package com.guardrail.logger.formatter;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;

/**
 * Formatador específico para CNPJ (Cadastro Nacional de Pessoa Jurídica).
 * 
 * <p>Este formatador aplica mascaramento parcial ao CNPJ, mantendo alguns
 * dígitos visíveis para identificação parcial enquanto protege a maior
 * parte do documento.</p>
 * 
 * <p>Exemplo: "12345678000190" → "**345678****90" ou "**.345.678/****-90"</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class CnpjFormatter implements Formatter {

    private final char maskChar;
    private final boolean formatted;

    /**
     * Cria um formatador de CNPJ com configurações padrão.
     */
    public CnpjFormatter() {
        this('*', false);
    }

    /**
     * Cria um formatador de CNPJ customizado.
     *
     * @param maskChar caractere de máscara
     * @param formatted se deve manter formatação (XX.XXX.XXX/XXXX-XX)
     */
    public CnpjFormatter(char maskChar, boolean formatted) {
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

        if (cleanValue.length() != 14) {
            return String.valueOf(maskChar).repeat(3);
        }

        StringBuilder result = new StringBuilder();

        if (formatted) {
            // Formato: XX.XXX.XXX/XXXX-XX
            result.append(maskChar).append(maskChar);
            result.append(".");
            result.append(cleanValue, 2, 5);
            result.append(".");
            result.append(cleanValue, 5, 8);
            result.append("/");
            result.append(maskChar).append(maskChar).append(maskChar).append(maskChar);
            result.append("-");
            result.append(cleanValue, 12, 14);
        } else {
            // Formato simples: **345678****90
            result.append(String.valueOf(maskChar).repeat(2));
            result.append(cleanValue, 2, 8);
            result.append(String.valueOf(maskChar).repeat(4));
            result.append(cleanValue, 12, 14);
        }

        return result.toString();
    }

    @Override
    public DataType getDataType() {
        return DataType.CNPJ;
    }

    @Override
    public String getName() {
        return "cnpjFormatter";
    }

    @Override
    public boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String cleanValue = value.replaceAll("[^0-9]", "");
        return cleanValue.length() == 14;
    }
}
