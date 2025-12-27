package com.guardrail.logger.engine;

import com.guardrail.logger.config.GuardrailLoggerProperties;
import com.guardrail.logger.config.SensitiveField;
import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;
import com.guardrail.logger.obfuscator.DefaultObfuscator;
import com.guardrail.logger.obfuscator.PartialObfuscator;
import com.guardrail.logger.registry.FormatterRegistry;
import com.guardrail.logger.registry.ObfuscatorRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Engine central de sanitização de dados sensíveis.
 * 
 * <p>Esta classe é responsável por coordenar a detecção e ofuscação de dados
 * sensíveis em mensagens de log, suportando múltiplos formatos de entrada
 * como JSON, toString de objetos e texto livre.</p>
 * 
 * <p>A engine utiliza uma combinação de padrões regex pré-compilados e
 * detecção baseada em atributos para identificar dados sensíveis.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class SanitizationEngine {

    private static final SanitizationEngine INSTANCE = new SanitizationEngine();

    private final Map<String, List<Pattern>> fieldPatterns = new ConcurrentHashMap<>();
    private final Map<DataType, Pattern> dataTypePatterns = new ConcurrentHashMap<>();
    private final Set<String> sensitiveFieldNames = ConcurrentHashMap.newKeySet();
    
    private GuardrailLoggerProperties properties;
    private volatile boolean initialized = false;

    private SanitizationEngine() {
        // Singleton
    }

    /**
     * Retorna a instância singleton da engine.
     *
     * @return instância do SanitizationEngine
     */
    public static SanitizationEngine getInstance() {
        return INSTANCE;
    }

    /**
     * Configura a engine com as propriedades de configuração.
     *
     * @param properties propriedades de configuração
     */
    public synchronized void configure(GuardrailLoggerProperties properties) {
        this.properties = properties;
        this.resetInternalState();

        properties.getSensitiveFields().forEach(this::compilePatternsForField);

        if (properties.isAutoDetect()) {
            properties.getAutoDetectTypes().forEach(type -> 
                dataTypePatterns.put(type, Pattern.compile(type.getDefaultPattern())));
        }

        ObfuscatorRegistry.getInstance().setDefaultObfuscator(
            new DefaultObfuscator(properties.getMaskChar(), properties.getDefaultMask())
        );

        this.initialized = true;
    }

    /**
     * Sanitiza uma mensagem de log, ofuscando dados sensíveis.
     *
     * @param message mensagem original
     * @return mensagem sanitizada
     */
    public String sanitize(String message) {
        if (isNotOperational(message)) {
            return message;
        }

        String result = maskFields(message);

        return properties.isAutoDetect() ? maskDataTypes(result) : result;
    }

    private boolean isNotOperational(String message) {
        return !initialized || !properties.isEnabled() || message == null || message.isEmpty();
    }

    private String maskFields(String message) {
        return properties.getSensitiveFields().stream()
                .reduce(message, (msg, field) -> {
                    List<Pattern> patterns = fieldPatterns.get(field.getName().toLowerCase());
                    return patterns == null ? msg : patterns.stream()
                            .reduce(msg, (m, p) -> applyRegex(m, p, field), (m1, m2) -> m1);
                }, (m1, m2) -> m1);
    }

    private String maskDataTypes(String message) {
        return dataTypePatterns.entrySet().stream()
                .reduce(message, (msg, entry) -> applyDataTypeRegex(msg, entry.getValue(), entry.getKey()), (m1, m2) -> m1);
    }

    private String applyRegex(String message, Pattern pattern, SensitiveField field) {
        Matcher matcher = pattern.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            int groupIndex = matcher.groupCount();
            String value = matcher.group(groupIndex);
            
            String replacement = Optional.ofNullable(value)
                    .filter(v -> !v.isEmpty())
                    .map(v -> {
                        String obfuscated = resolveValue(v, field);
                        String full = matcher.group();
                        int start = matcher.start(groupIndex) - matcher.start();
                        int end = matcher.end(groupIndex) - matcher.start();
                        return full.substring(0, start) + obfuscated + full.substring(end);
                    })
                    .orElseGet(matcher::group);

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        return matcher.appendTail(sb).toString();
    }

    private String applyDataTypeRegex(String message, Pattern pattern, DataType dataType) {
        Matcher matcher = pattern.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String value = matcher.group();
            String replacement = isAlreadyMasked(value) ? value : resolveByDataType(value, dataType);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        return matcher.appendTail(sb).toString();
    }

    private String resolveValue(String value, SensitiveField field) {
        return getFormatter(field)
                .map(f -> f.format(value))
                .orElseGet(() -> applyPartialOrFallback(value, field));
    }

    private Optional<Formatter> getFormatter(SensitiveField field) {
        if (field.getFormatterName() != null) {
            return FormatterRegistry.getInstance().getByName(field.getFormatterName());
        }
        if (field.getDataType() != DataType.GENERIC) {
            return FormatterRegistry.getInstance().getByDataType(field.getDataType());
        }
        return Optional.empty();
    }

    private String applyPartialOrFallback(String value, SensitiveField field) {
        if (field.getVisibleCharsStart() > 0 || field.getVisibleCharsEnd() > 0) {
            return new PartialObfuscator(
                field.getVisibleCharsStart(),
                field.getVisibleCharsEnd(),
                properties.getMaskChar(),
                3
            ).obfuscate(value);
        }
        return ObfuscatorRegistry.getInstance().getDefaultObfuscator().obfuscate(value);
    }

    private String resolveByDataType(String value, DataType dataType) {
        return FormatterRegistry.getInstance().getByDataType(dataType)
                .map(f -> f.format(value))
                .orElseGet(() -> Optional.ofNullable(ObfuscatorRegistry.getInstance().getObfuscatorFor(dataType))
                        .map(o -> o.obfuscate(value))
                        .orElse(ObfuscatorRegistry.getInstance().getDefaultObfuscator().obfuscate(value)));
    }

    private void compilePatternsForField(SensitiveField field) {
        String fieldName = field.getName();
        String normalizedName = fieldName.toLowerCase();
        sensitiveFieldNames.add(field.isCaseSensitive() ? fieldName : normalizedName);

        String flags = field.isCaseSensitive() ? "" : SanitizationPatterns.CASE_INSENSITIVE;
        String quotedName = Pattern.quote(fieldName);
        
        fieldPatterns.put(normalizedName, List.of(
            Pattern.compile(flags + String.format(SanitizationPatterns.JSON_QUOTED, quotedName)),
            Pattern.compile(flags + String.format(SanitizationPatterns.JSON_UNQUOTED, quotedName)),
            Pattern.compile(flags + String.format(SanitizationPatterns.TO_STRING_DOUBLE_QUOTED, quotedName)),
            Pattern.compile(flags + String.format(SanitizationPatterns.TO_STRING_SINGLE_QUOTED, quotedName)),
            Pattern.compile(flags + String.format(SanitizationPatterns.TO_STRING_UNQUOTED, quotedName)),
            Pattern.compile(flags + String.format(SanitizationPatterns.TEXT_COLON_QUOTED, quotedName)),
            Pattern.compile(flags + String.format(SanitizationPatterns.TEXT_COLON_UNQUOTED, quotedName)),
            Pattern.compile(flags + String.format(SanitizationPatterns.QUERY_PARAMETER, quotedName)),
            Pattern.compile(flags + String.format(SanitizationPatterns.PATH_PARAMETER, quotedName))
        ));
    }

    private boolean isAlreadyMasked(String value) {
        return Optional.ofNullable(value)
                .filter(v -> !v.isEmpty())
                .map(v -> v.chars().filter(c -> c == properties.getMaskChar()).count() > v.length() / 2)
                .orElse(true);
    }

    /**
     * Verifica se a engine está inicializada.
     *
     * @return true se inicializada
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Retorna as propriedades de configuração.
     *
     * @return propriedades
     */
    public GuardrailLoggerProperties getProperties() {
        return properties;
    }

    /**
     * Reseta a engine para estado não inicializado.
     */
    public synchronized void reset() {
        this.initialized = false;
        this.resetInternalState();
        this.properties = null;
    }

    private void resetInternalState() {
        this.fieldPatterns.clear();
        this.dataTypePatterns.clear();
        this.sensitiveFieldNames.clear();
    }
}
