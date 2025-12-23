package com.guardrail.logger.engine;

import com.guardrail.logger.config.GuardrailLoggerProperties;
import com.guardrail.logger.config.SensitiveField;
import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;
import com.guardrail.logger.core.Obfuscator;
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
     * Inicializa a engine com as propriedades de configuração.
     *
     * @param properties propriedades de configuração
     */
    public synchronized void initialize(GuardrailLoggerProperties properties) {
        this.properties = properties;
        this.fieldPatterns.clear();
        this.dataTypePatterns.clear();
        this.sensitiveFieldNames.clear();

        // Registra campos sensíveis configurados
        for (SensitiveField field : properties.getSensitiveFields()) {
            registerSensitiveField(field);
        }

        // Compila padrões para tipos de dados auto-detectados
        if (properties.isAutoDetect()) {
            for (DataType dataType : properties.getAutoDetectTypes()) {
                dataTypePatterns.put(dataType, Pattern.compile(dataType.getDefaultPattern()));
            }
        }

        // Registra ofuscador padrão
        ObfuscatorRegistry.getInstance().setDefaultObfuscator(
            new DefaultObfuscator(properties.getMaskChar(), properties.getDefaultMask())
        );

        this.initialized = true;
    }

    /**
     * Registra um campo sensível para detecção.
     *
     * @param field configuração do campo sensível
     */
    public void registerSensitiveField(SensitiveField field) {
        String fieldName = field.getName();
        sensitiveFieldNames.add(field.isCaseSensitive() ? fieldName : fieldName.toLowerCase());

        String flags = field.isCaseSensitive() ? "" : "(?i)";
        
        List<Pattern> patterns = new ArrayList<>();
        
        // JSON: "fieldName": "value" ou "fieldName": value
        patterns.add(Pattern.compile(flags + "\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\"([^\"]+)\""));
        patterns.add(Pattern.compile(flags + "\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*([^,}\\s]+)"));
        
        // toString: fieldName=value ou fieldName="value" ou fieldName='value'
        patterns.add(Pattern.compile(flags + Pattern.quote(fieldName) + "\\s*=\\s*\"([^\"]+)\""));
        patterns.add(Pattern.compile(flags + Pattern.quote(fieldName) + "\\s*=\\s*'([^']+)'"));
        patterns.add(Pattern.compile(flags + Pattern.quote(fieldName) + "\\s*=\\s*([^,\\]\\}\\s]+)"));

        fieldPatterns.put(fieldName.toLowerCase(), patterns);
    }

    /**
     * Sanitiza uma mensagem de log, ofuscando dados sensíveis.
     *
     * @param message mensagem original
     * @return mensagem sanitizada
     */
    public String sanitize(String message) {
        if (!initialized || !properties.isEnabled() || message == null || message.isEmpty()) {
            return message;
        }

        String result = message;

        // Aplica ofuscação por campo configurado
        result = sanitizeByFields(result);

        // Aplica detecção automática de tipos de dados
        if (properties.isAutoDetect()) {
            result = sanitizeByDataTypes(result);
        }

        return result;
    }

    /**
     * Sanitiza a mensagem baseado nos campos sensíveis configurados.
     */
    private String sanitizeByFields(String message) {
        String result = message;

        for (SensitiveField field : properties.getSensitiveFields()) {
            List<Pattern> patterns = fieldPatterns.get(field.getName().toLowerCase());
            if (patterns == null) continue;

            for (Pattern pattern : patterns) {
                result = applyPattern(result, pattern, field);
            }
        }

        return result;
    }

    /**
     * Aplica um padrão de ofuscação à mensagem.
     */
    private String applyPattern(String message, Pattern pattern, SensitiveField field) {
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String matchedValue = null;
            
            // Tenta encontrar o grupo capturado
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null && !group.isEmpty()) {
                    matchedValue = group;
                    break;
                }
            }
            
            if (matchedValue == null || matchedValue.isEmpty()) {
                continue;
            }

            String obfuscatedValue = obfuscateValue(matchedValue, field);
            String fullMatch = matcher.group();
            String replacement = fullMatch.replace(matchedValue, obfuscatedValue);
            
            // Escapa caracteres especiais no replacement
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * Sanitiza a mensagem baseado na detecção automática de tipos de dados.
     */
    private String sanitizeByDataTypes(String message) {
        String result = message;

        for (Map.Entry<DataType, Pattern> entry : dataTypePatterns.entrySet()) {
            DataType dataType = entry.getKey();
            Pattern pattern = entry.getValue();

            Matcher matcher = pattern.matcher(result);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String matchedValue = matcher.group();
                
                // Verifica se o valor já foi ofuscado
                if (isAlreadyObfuscated(matchedValue)) {
                    continue;
                }

                String obfuscatedValue = obfuscateByDataType(matchedValue, dataType);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(obfuscatedValue));
            }
            matcher.appendTail(sb);
            result = sb.toString();
        }

        return result;
    }

    /**
     * Ofusca um valor baseado na configuração do campo.
     */
    private String obfuscateValue(String value, SensitiveField field) {
        // Tenta usar formatador específico
        if (field.getFormatterName() != null) {
            Optional<Formatter> formatter = FormatterRegistry.getInstance()
                .getByName(field.getFormatterName());
            if (formatter.isPresent()) {
                return formatter.get().format(value);
            }
        }

        // Tenta usar formatador por tipo de dado
        if (field.getDataType() != DataType.GENERIC) {
            Optional<Formatter> formatter = FormatterRegistry.getInstance()
                .getByDataType(field.getDataType());
            if (formatter.isPresent()) {
                return formatter.get().format(value);
            }
        }

        // Usa ofuscação parcial se configurada
        if (field.getVisibleCharsStart() > 0 || field.getVisibleCharsEnd() > 0) {
            PartialObfuscator obfuscator = new PartialObfuscator(
                field.getVisibleCharsStart(),
                field.getVisibleCharsEnd(),
                properties.getMaskChar(),
                3
            );
            return obfuscator.obfuscate(value);
        }

        // Usa ofuscador padrão
        return ObfuscatorRegistry.getInstance().getDefaultObfuscator().obfuscate(value);
    }

    /**
     * Ofusca um valor baseado no tipo de dado detectado.
     */
    private String obfuscateByDataType(String value, DataType dataType) {
        // Tenta usar formatador por tipo de dado
        Optional<Formatter> formatter = FormatterRegistry.getInstance().getByDataType(dataType);
        if (formatter.isPresent()) {
            return formatter.get().format(value);
        }

        // Tenta usar ofuscador por tipo de dado
        Obfuscator obfuscator = ObfuscatorRegistry.getInstance().getObfuscatorFor(dataType);
        if (obfuscator != null) {
            return obfuscator.obfuscate(value);
        }

        // Usa ofuscador padrão
        return ObfuscatorRegistry.getInstance().getDefaultObfuscator().obfuscate(value);
    }

    /**
     * Verifica se o valor já foi ofuscado.
     */
    private boolean isAlreadyObfuscated(String value) {
        if (value == null || value.isEmpty()) return true;
        char maskChar = properties.getMaskChar();
        long maskCount = value.chars().filter(c -> c == maskChar).count();
        return maskCount > value.length() / 2;
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
        this.fieldPatterns.clear();
        this.dataTypePatterns.clear();
        this.sensitiveFieldNames.clear();
        this.properties = null;
    }
}
