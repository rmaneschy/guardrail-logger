package com.guardrail.logger.formatter;

import com.guardrail.logger.core.Formatter;

public class DocumentFormatter implements Formatter {
    @Override
    public String format(final String value) {
        if (value == null || value.isBlank()) return "***";

        // Remove apenas caracteres de pontuação comuns, preservando letras e números
        final var cleanValue = value.replaceAll("[.\\-/]", "");
        int len = cleanValue.length();

        final var sb = new StringBuilder(18); // Capacidade inicial para evitar resize

        if (len == 11) { // Lógica de CPF
            // Resultado esperado: 234.***.***-20
            sb.append(cleanValue, 0, 3)
                    .append(".***.***-")
                    .append(cleanValue, 9, 11);
            return sb.toString();
        }

        if (len == 14) { // Lógica de CNPJ (Suporta Alfanumérico)
            // Exemplo: 12.***.***/***/90
            sb.append(cleanValue, 0, 2)
                    .append(".***.***/***/")
                    .append(cleanValue, 12, 14);
            return sb.toString();
        }

        return "***";
    }

    @Override
    public String getName() {
        return "documentFormatter";
    }

}
