package com.guardrail.logger.config;

import com.guardrail.logger.core.DataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe de configuração principal para o Guardrail Logger.
 * 
 * <p>Esta classe pode ser utilizada com Spring Boot através do prefixo
 * {@code guardrail.logger} no arquivo de propriedades, ou configurada
 * programaticamente.</p>
 * 
 * <p>Exemplo de configuração via application.yml:</p>
 * <pre>
 * guardrail:
 *   logger:
 *     enabled: true
 *     mask-char: '*'
 *     default-mask: '***'
 *     sensitive-fields:
 *       - name: documento
 *         data-type: CPF
 *       - name: email
 *         data-type: EMAIL
 * </pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class GuardrailLoggerProperties {

    /**
     * Habilita ou desabilita a ofuscação de dados sensíveis.
     */
    private boolean enabled = true;

    /**
     * Caractere utilizado para mascaramento.
     */
    private char maskChar = '*';

    /**
     * Máscara padrão quando não há formatador específico.
     */
    private String defaultMask = "***";

    /**
     * Lista de campos sensíveis configurados.
     */
    private List<SensitiveField> sensitiveFields = new ArrayList<>();

    /**
     * Padrões regex customizados por nome de campo.
     */
    private Map<String, String> customPatterns = new HashMap<>();

    /**
     * Habilita detecção automática de tipos de dados sensíveis.
     */
    private boolean autoDetect = true;

    /**
     * Lista de tipos de dados para detecção automática.
     */
    private List<DataType> autoDetectTypes = new ArrayList<>();

    /**
     * Habilita ofuscação em logs de requisição/resposta HTTP.
     */
    private boolean obfuscateHttpLogs = true;

    /**
     * Habilita ofuscação em toString() de objetos anotados.
     */
    private boolean obfuscateToString = true;

    /**
     * Nível de log mínimo para aplicar ofuscação.
     */
    private String minLogLevel = "TRACE";

    /**
     * Lista de pacotes a serem ignorados na ofuscação.
     */
    private List<String> excludePackages = new ArrayList<>();

    /**
     * Configurações específicas para integração com GELF/Graylog.
     */
    private GelfConfig gelf = new GelfConfig();

    public GuardrailLoggerProperties() {
        initializeDefaultAutoDetectTypes();
    }

    private void initializeDefaultAutoDetectTypes() {
        autoDetectTypes.add(DataType.CPF);
        autoDetectTypes.add(DataType.CNPJ);
        autoDetectTypes.add(DataType.EMAIL);
        autoDetectTypes.add(DataType.CREDIT_CARD);
        autoDetectTypes.add(DataType.IP_ADDRESS);
    }

    // Getters and Setters

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public char getMaskChar() {
        return maskChar;
    }

    public void setMaskChar(char maskChar) {
        this.maskChar = maskChar;
    }

    public String getDefaultMask() {
        return defaultMask;
    }

    public void setDefaultMask(String defaultMask) {
        this.defaultMask = defaultMask;
    }

    public List<SensitiveField> getSensitiveFields() {
        return sensitiveFields;
    }

    public void setSensitiveFields(List<SensitiveField> sensitiveFields) {
        this.sensitiveFields = sensitiveFields;
    }

    public Map<String, String> getCustomPatterns() {
        return customPatterns;
    }

    public void setCustomPatterns(Map<String, String> customPatterns) {
        this.customPatterns = customPatterns;
    }

    public boolean isAutoDetect() {
        return autoDetect;
    }

    public void setAutoDetect(boolean autoDetect) {
        this.autoDetect = autoDetect;
    }

    public List<DataType> getAutoDetectTypes() {
        return autoDetectTypes;
    }

    public void setAutoDetectTypes(List<DataType> autoDetectTypes) {
        this.autoDetectTypes = autoDetectTypes;
    }

    public boolean isObfuscateHttpLogs() {
        return obfuscateHttpLogs;
    }

    public void setObfuscateHttpLogs(boolean obfuscateHttpLogs) {
        this.obfuscateHttpLogs = obfuscateHttpLogs;
    }

    public boolean isObfuscateToString() {
        return obfuscateToString;
    }

    public void setObfuscateToString(boolean obfuscateToString) {
        this.obfuscateToString = obfuscateToString;
    }

    public String getMinLogLevel() {
        return minLogLevel;
    }

    public void setMinLogLevel(String minLogLevel) {
        this.minLogLevel = minLogLevel;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
    }

    public GelfConfig getGelf() {
        return gelf;
    }

    public void setGelf(GelfConfig gelf) {
        this.gelf = gelf;
    }

    /**
     * Adiciona um campo sensível à configuração.
     *
     * @param field o campo sensível a ser adicionado
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerProperties addSensitiveField(SensitiveField field) {
        this.sensitiveFields.add(field);
        return this;
    }

    /**
     * Adiciona um padrão customizado para um campo.
     *
     * @param fieldName nome do campo
     * @param pattern padrão regex
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerProperties addCustomPattern(String fieldName, String pattern) {
        this.customPatterns.put(fieldName, pattern);
        return this;
    }

    /**
     * Configurações específicas para GELF/Graylog.
     */
    public static class GelfConfig {
        private boolean enabled = false;
        private String host = "localhost";
        private int port = 12201;
        private String protocol = "UDP";
        private boolean includeFullMdc = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public boolean isIncludeFullMdc() {
            return includeFullMdc;
        }

        public void setIncludeFullMdc(boolean includeFullMdc) {
            this.includeFullMdc = includeFullMdc;
        }
    }
}
