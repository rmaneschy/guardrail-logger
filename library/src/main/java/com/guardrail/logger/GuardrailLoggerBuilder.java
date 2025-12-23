package com.guardrail.logger;

import com.guardrail.logger.config.GuardrailLoggerProperties;
import com.guardrail.logger.config.SensitiveField;
import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;
import com.guardrail.logger.core.Obfuscator;
import com.guardrail.logger.extension.FormatterExtension;
import com.guardrail.logger.extension.ObfuscatorExtension;
import com.guardrail.logger.registry.FormatterRegistry;
import com.guardrail.logger.registry.ObfuscatorRegistry;
import com.guardrail.logger.spring.GuardrailLoggerApplicationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder fluente para configuração programática do Guardrail Logger.
 * 
 * <p>Esta classe permite configurar o mascaramento de dados sensíveis
 * de forma programática, sem necessidade de arquivos de configuração.</p>
 * 
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * GuardrailLoggerBuilder.create()
 *     .enabled(true)
 *     .maskChar('*')
 *     .defaultMask("***")
 *     .addSensitiveField("documento", DataType.CPF)
 *     .addSensitiveField("email", DataType.EMAIL)
 *     .addFormatter("customCpf", value -> "***" + value.substring(3, 9) + "**")
 *     .autoDetect(true)
 *     .build();
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public final class GuardrailLoggerBuilder {

    private final GuardrailLoggerProperties properties;
    private final List<FormatterExtension> formatterExtensions;
    private final List<ObfuscatorExtension> obfuscatorExtensions;

    private GuardrailLoggerBuilder() {
        this.properties = new GuardrailLoggerProperties();
        this.formatterExtensions = new ArrayList<>();
        this.obfuscatorExtensions = new ArrayList<>();
    }

    /**
     * Cria uma nova instância do builder.
     *
     * @return nova instância do builder
     */
    public static GuardrailLoggerBuilder create() {
        return new GuardrailLoggerBuilder();
    }

    /**
     * Habilita ou desabilita o mascaramento.
     *
     * @param enabled true para habilitar
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder enabled(boolean enabled) {
        properties.setEnabled(enabled);
        return this;
    }

    /**
     * Define o caractere de máscara.
     *
     * @param maskChar caractere de máscara
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder maskChar(char maskChar) {
        properties.setMaskChar(maskChar);
        return this;
    }

    /**
     * Define a máscara padrão.
     *
     * @param defaultMask máscara padrão
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder defaultMask(String defaultMask) {
        properties.setDefaultMask(defaultMask);
        return this;
    }

    /**
     * Adiciona um campo sensível simples.
     *
     * @param name nome do campo
     * @param dataType tipo de dado
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addSensitiveField(String name, DataType dataType) {
        properties.addSensitiveField(SensitiveField.builder()
            .name(name)
            .dataType(dataType)
            .build());
        return this;
    }

    /**
     * Adiciona um campo sensível com configuração completa.
     *
     * @param field configuração do campo
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addSensitiveField(SensitiveField field) {
        properties.addSensitiveField(field);
        return this;
    }

    /**
     * Adiciona um campo sensível com ofuscação parcial.
     *
     * @param name nome do campo
     * @param dataType tipo de dado
     * @param visibleStart caracteres visíveis no início
     * @param visibleEnd caracteres visíveis no final
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addSensitiveField(String name, DataType dataType, 
            int visibleStart, int visibleEnd) {
        properties.addSensitiveField(SensitiveField.builder()
            .name(name)
            .dataType(dataType)
            .visibleCharsStart(visibleStart)
            .visibleCharsEnd(visibleEnd)
            .build());
        return this;
    }

    /**
     * Adiciona um padrão regex customizado para um campo.
     *
     * @param fieldName nome do campo
     * @param pattern padrão regex
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addCustomPattern(String fieldName, String pattern) {
        properties.addCustomPattern(fieldName, pattern);
        return this;
    }

    /**
     * Habilita ou desabilita a detecção automática de tipos de dados.
     *
     * @param autoDetect true para habilitar
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder autoDetect(boolean autoDetect) {
        properties.setAutoDetect(autoDetect);
        return this;
    }

    /**
     * Adiciona um tipo de dado para detecção automática.
     *
     * @param dataType tipo de dado
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addAutoDetectType(DataType dataType) {
        properties.getAutoDetectTypes().add(dataType);
        return this;
    }

    /**
     * Adiciona um formatador customizado.
     *
     * @param name nome do formatador
     * @param formatter instância do formatador
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addFormatter(String name, Formatter formatter) {
        formatterExtensions.add(registry -> registry.register(name, formatter));
        return this;
    }

    /**
     * Adiciona um formatador customizado para um tipo de dado.
     *
     * @param dataType tipo de dado
     * @param formatter instância do formatador
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addFormatter(DataType dataType, Formatter formatter) {
        formatterExtensions.add(registry -> registry.register(dataType, formatter));
        return this;
    }

    /**
     * Adiciona um ofuscador customizado.
     *
     * @param name nome do ofuscador
     * @param obfuscator instância do ofuscador
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addObfuscator(String name, Obfuscator obfuscator) {
        obfuscatorExtensions.add(registry -> registry.register(name, obfuscator));
        return this;
    }

    /**
     * Adiciona um ofuscador customizado para um tipo de dado.
     *
     * @param dataType tipo de dado
     * @param obfuscator instância do ofuscador
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addObfuscator(DataType dataType, Obfuscator obfuscator) {
        obfuscatorExtensions.add(registry -> registry.register(dataType, obfuscator));
        return this;
    }

    /**
     * Adiciona uma extensão de formatador.
     *
     * @param extension extensão de formatador
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addFormatterExtension(FormatterExtension extension) {
        formatterExtensions.add(extension);
        return this;
    }

    /**
     * Adiciona uma extensão de ofuscador.
     *
     * @param extension extensão de ofuscador
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder addObfuscatorExtension(ObfuscatorExtension extension) {
        obfuscatorExtensions.add(extension);
        return this;
    }

    /**
     * Adiciona um pacote a ser excluído da ofuscação.
     *
     * @param packageName nome do pacote
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder excludePackage(String packageName) {
        properties.getExcludePackages().add(packageName);
        return this;
    }

    /**
     * Configura integração com GELF/Graylog.
     *
     * @param host host do servidor Graylog
     * @param port porta do servidor
     * @return esta instância para encadeamento
     */
    public GuardrailLoggerBuilder withGelf(String host, int port) {
        properties.getGelf().setEnabled(true);
        properties.getGelf().setHost(host);
        properties.getGelf().setPort(port);
        return this;
    }

    /**
     * Constrói e aplica a configuração.
     *
     * @return propriedades configuradas
     */
    public GuardrailLoggerProperties build() {
        // Registra extensões de formatadores
        FormatterRegistry formatterRegistry = FormatterRegistry.getInstance();
        formatterExtensions.stream()
            .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
            .forEach(ext -> ext.register(formatterRegistry));

        // Registra extensões de ofuscadores
        ObfuscatorRegistry obfuscatorRegistry = ObfuscatorRegistry.getInstance();
        obfuscatorExtensions.stream()
            .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
            .forEach(ext -> ext.register(obfuscatorRegistry));

        // Aplica configuração
        GuardrailLoggerApplicationListener.configure(properties);

        return properties;
    }

    /**
     * Retorna as propriedades sem aplicar a configuração.
     *
     * @return propriedades configuradas
     */
    public GuardrailLoggerProperties getProperties() {
        return properties;
    }
}
