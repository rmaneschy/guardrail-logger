package com.guardrail.logger.util;

import com.guardrail.logger.annotation.Sensitive;
import com.guardrail.logger.annotation.SensitiveToString;
import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;
import com.guardrail.logger.obfuscator.PartialObfuscator;
import com.guardrail.logger.registry.FormatterRegistry;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Builder utilitário para geração de toString() com ofuscação de dados sensíveis.
 * 
 * <p>Esta classe fornece métodos estáticos para gerar representações em string
 * de objetos com campos sensíveis automaticamente ofuscados.</p>
 * 
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * public class Cliente {
 *     @Sensitive(type = DataType.CPF)
 *     private String documento;
 *     private String nome;
 *     
 *     @Override
 *     public String toString() {
 *         return SensitiveToStringBuilder.build(this);
 *     }
 * }
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public final class SensitiveToStringBuilder {

    private SensitiveToStringBuilder() {
        // Utility class
    }

    /**
     * Gera uma representação em string do objeto com campos sensíveis ofuscados.
     *
     * @param obj objeto a ser convertido
     * @return representação em string com dados sensíveis ofuscados
     */
    public static String build(Object obj) {
        if (obj == null) {
            return "null";
        }

        Class<?> clazz = obj.getClass();
        SensitiveToString annotation = clazz.getAnnotation(SensitiveToString.class);
        
        boolean includeInherited = annotation != null && annotation.includeInherited();
        boolean includeNulls = annotation != null && annotation.includeNulls();
        boolean jsonFormat = annotation != null && annotation.jsonFormat();
        Set<String> excludeFields = annotation != null ? 
            new HashSet<>(Arrays.asList(annotation.exclude())) : Collections.emptySet();

        List<Field> fields = getAllFields(clazz, includeInherited);
        
        if (jsonFormat) {
            return buildJson(obj, fields, includeNulls, excludeFields);
        } else {
            return buildStandard(obj, clazz, fields, includeNulls, excludeFields);
        }
    }

    /**
     * Gera representação em formato padrão (ClassName[field=value, ...]).
     */
    private static String buildStandard(Object obj, Class<?> clazz, List<Field> fields, 
            boolean includeNulls, Set<String> excludeFields) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getSimpleName()).append("[");

        boolean first = true;
        for (Field field : fields) {
            if (excludeFields.contains(field.getName())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value == null && !includeNulls) {
                    continue;
                }

                if (!first) {
                    sb.append(", ");
                }
                first = false;

                sb.append(field.getName()).append("=");
                sb.append(formatValue(field, value));

            } catch (IllegalAccessException e) {
                // Ignora campos inacessíveis
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Gera representação em formato JSON.
     */
    private static String buildJson(Object obj, List<Field> fields, 
            boolean includeNulls, Set<String> excludeFields) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean first = true;
        for (Field field : fields) {
            if (excludeFields.contains(field.getName())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value == null && !includeNulls) {
                    continue;
                }

                if (!first) {
                    sb.append(", ");
                }
                first = false;

                sb.append("\"").append(field.getName()).append("\": ");
                
                String formattedValue = formatValue(field, value);
                if (value instanceof Number) {
                    sb.append(formattedValue);
                } else {
                    sb.append("\"").append(escapeJson(formattedValue)).append("\"");
                }

            } catch (IllegalAccessException e) {
                // Ignora campos inacessíveis
            }
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * Formata o valor de um campo, aplicando ofuscação se necessário.
     */
    private static String formatValue(Field field, Object value) {
        if (value == null) {
            return "null";
        }

        Sensitive sensitive = field.getAnnotation(Sensitive.class);
        if (sensitive == null) {
            return String.valueOf(value);
        }

        String stringValue = String.valueOf(value);
        return obfuscateValue(stringValue, sensitive);
    }

    /**
     * Aplica ofuscação ao valor baseado na anotação @Sensitive.
     */
    private static String obfuscateValue(String value, Sensitive sensitive) {
        if (value == null || value.isEmpty()) {
            return sensitive.mask();
        }

        // Tenta usar formatador customizado
        if (!sensitive.formatter().isEmpty()) {
            Optional<Formatter> formatter = FormatterRegistry.getInstance()
                .getByName(sensitive.formatter());
            if (formatter.isPresent()) {
                return formatter.get().format(value);
            }
        }

        // Tenta usar formatador por tipo de dado
        if (sensitive.type() != DataType.GENERIC) {
            Optional<Formatter> formatter = FormatterRegistry.getInstance()
                .getByDataType(sensitive.type());
            if (formatter.isPresent()) {
                return formatter.get().format(value);
            }
        }

        // Usa ofuscação parcial se configurada
        if (sensitive.visibleStart() > 0 || sensitive.visibleEnd() > 0) {
            PartialObfuscator obfuscator = new PartialObfuscator(
                sensitive.visibleStart(),
                sensitive.visibleEnd(),
                sensitive.maskChar(),
                3
            );
            return obfuscator.obfuscate(value);
        }

        // Usa máscara padrão
        return sensitive.mask();
    }

    /**
     * Obtém todos os campos de uma classe, opcionalmente incluindo herdados.
     */
    private static List<Field> getAllFields(Class<?> clazz, boolean includeInherited) {
        List<Field> fields = new ArrayList<>();
        
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            
            if (!includeInherited) {
                break;
            }
            current = current.getSuperclass();
        }
        
        return fields;
    }

    /**
     * Escapa caracteres especiais para JSON.
     */
    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    /**
     * Builder fluente para construção customizada de toString().
     */
    public static class Builder {
        private final Object obj;
        private final StringBuilder sb = new StringBuilder();
        private boolean first = true;
        private boolean jsonFormat = false;

        public Builder(Object obj) {
            this.obj = obj;
        }

        /**
         * Define se deve usar formato JSON.
         */
        public Builder jsonFormat(boolean jsonFormat) {
            this.jsonFormat = jsonFormat;
            return this;
        }

        /**
         * Adiciona um campo com valor já formatado.
         */
        public Builder append(String fieldName, Object value) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            if (jsonFormat) {
                sb.append("\"").append(fieldName).append("\": ");
                if (value instanceof Number) {
                    sb.append(value);
                } else {
                    sb.append("\"").append(escapeJson(String.valueOf(value))).append("\"");
                }
            } else {
                sb.append(fieldName).append("=").append(value);
            }
            return this;
        }

        /**
         * Adiciona um campo sensível com ofuscação.
         */
        public Builder appendSensitive(String fieldName, Object value, DataType dataType) {
            String obfuscated = obfuscateSensitive(value, dataType);
            return append(fieldName, obfuscated);
        }

        /**
         * Adiciona um campo sensível com máscara fixa.
         */
        public Builder appendMasked(String fieldName, Object value, String mask) {
            return append(fieldName, value != null ? mask : "null");
        }

        /**
         * Constrói a string final.
         */
        public String build() {
            String content = sb.toString();
            if (jsonFormat) {
                return "{" + content + "}";
            } else {
                return obj.getClass().getSimpleName() + "[" + content + "]";
            }
        }

        private String obfuscateSensitive(Object value, DataType dataType) {
            if (value == null) {
                return "***";
            }
            
            Optional<Formatter> formatter = FormatterRegistry.getInstance().getByDataType(dataType);
            if (formatter.isPresent()) {
                return formatter.get().format(String.valueOf(value));
            }
            return "***";
        }
    }

    /**
     * Cria um novo builder para o objeto especificado.
     *
     * @param obj objeto base
     * @return novo builder
     */
    public static Builder builder(Object obj) {
        return new Builder(obj);
    }
}
