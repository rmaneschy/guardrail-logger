package com.guardrail.logger.spring;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import com.guardrail.logger.config.GuardrailLoggerProperties;
import com.guardrail.logger.config.SensitiveField;
import com.guardrail.logger.core.DataType;
import com.guardrail.logger.engine.SanitizationEngine;
import com.guardrail.logger.formatter.*;
import com.guardrail.logger.logback.GuardrailMaskingEncoder;
import com.guardrail.logger.logback.GuardrailMaskingLayout;
import com.guardrail.logger.registry.FormatterRegistry;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Listener de aplicação Spring que configura automaticamente o mascaramento de logs.
 * 
 * <p>Este listener é acionado quando a aplicação Spring Boot está pronta
 * (ApplicationReadyEvent) e configura automaticamente os appenders do Logback
 * para aplicar mascaramento de dados sensíveis.</p>
 * 
 * <p>A configuração pode ser feita via properties ou programaticamente:</p>
 * 
 * <pre>{@code
 * // Via application.yml
 * guardrail:
 *   logger:
 *     enabled: true
 *     sensitive-fields:
 *       - name: documento
 *         data-type: CPF
 * 
 * // Programaticamente
 * GuardrailLoggerApplicationListener.configure(properties);
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class GuardrailLoggerApplicationListener {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(GuardrailLoggerApplicationListener.class);
    
    private static volatile boolean configured = false;
    private static GuardrailLoggerProperties currentProperties;

    /**
     * Configura o mascaramento de logs com as propriedades fornecidas.
     * 
     * <p>Este método pode ser chamado manualmente ou será invocado
     * automaticamente pelo Spring Boot através da auto-configuração.</p>
     *
     * @param properties propriedades de configuração
     */
    public static synchronized void configure(GuardrailLoggerProperties properties) {
        if (properties == null) {
            log.warn("GuardrailLoggerProperties is null, using default configuration");
            properties = createDefaultProperties();
        }

        currentProperties = properties;

        if (!properties.isEnabled()) {
            log.info("Guardrail Logger is disabled");
            return;
        }

        // Registra formatadores padrão
        registerDefaultFormatters();

        // Inicializa a engine de sanitização
        SanitizationEngine.getInstance().initialize(properties);

        // Configura os appenders do Logback
        configureLogbackAppenders(properties);

        configured = true;
        log.info("Guardrail Logger configured successfully with {} sensitive fields", 
            properties.getSensitiveFields().size());
    }

    /**
     * Configura o mascaramento com configuração padrão.
     */
    public static void configureWithDefaults() {
        configure(createDefaultProperties());
    }

    /**
     * Cria propriedades de configuração padrão.
     */
    private static GuardrailLoggerProperties createDefaultProperties() {
        GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
        
        // Adiciona campos sensíveis comuns
        properties.addSensitiveField(SensitiveField.builder()
            .name("documento").dataType(DataType.CPF).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("cpf").dataType(DataType.CPF).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("cnpj").dataType(DataType.CNPJ).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("email").dataType(DataType.EMAIL).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("telefone").dataType(DataType.PHONE).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("phone").dataType(DataType.PHONE).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("nome").dataType(DataType.NAME).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("name").dataType(DataType.NAME).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("renda").dataType(DataType.MONETARY).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("senha").dataType(DataType.PASSWORD).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("password").dataType(DataType.PASSWORD).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("cartao").dataType(DataType.CREDIT_CARD).build());
        properties.addSensitiveField(SensitiveField.builder()
            .name("creditCard").dataType(DataType.CREDIT_CARD).build());

        return properties;
    }

    /**
     * Registra os formatadores padrão no registro global.
     */
    private static void registerDefaultFormatters() {
        FormatterRegistry registry = FormatterRegistry.getInstance();
        
        registry.register(DataType.CPF, new CpfFormatter());
        registry.register(DataType.CNPJ, new CnpjFormatter());
        registry.register(DataType.EMAIL, new EmailFormatter());
        registry.register(DataType.CREDIT_CARD, new CreditCardFormatter());
        registry.register(DataType.PHONE, new PhoneFormatter());
        registry.register(DataType.NAME, new NameFormatter());
        registry.register(DataType.MONETARY, new MonetaryFormatter());
    }

    /**
     * Configura os appenders do Logback para usar mascaramento.
     */
    private static void configureLogbackAppenders(GuardrailLoggerProperties properties) {
        try {
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

            List<Appender<ILoggingEvent>> appendersToModify = new ArrayList<>();
            Iterator<Appender<ILoggingEvent>> appenderIterator = rootLogger.iteratorForAppenders();
            
            while (appenderIterator.hasNext()) {
                appendersToModify.add(appenderIterator.next());
            }

            for (Appender<ILoggingEvent> appender : appendersToModify) {
                configureAppender(appender, loggerContext, properties);
            }

        } catch (Exception e) {
            log.error("Failed to configure Logback appenders for masking", e);
        }
    }

    /**
     * Configura um appender específico para usar mascaramento.
     */
    private static void configureAppender(Appender<ILoggingEvent> appender, 
            LoggerContext loggerContext, GuardrailLoggerProperties properties) {
        
        if (appender instanceof ConsoleAppender) {
            configureConsoleAppender((ConsoleAppender<ILoggingEvent>) appender, loggerContext);
        }
        // Outros tipos de appenders podem ser configurados aqui
    }

    /**
     * Configura um ConsoleAppender para usar o encoder de mascaramento.
     */
    private static void configureConsoleAppender(ConsoleAppender<ILoggingEvent> appender, 
            LoggerContext loggerContext) {
        
        Encoder<ILoggingEvent> currentEncoder = appender.getEncoder();
        
        if (currentEncoder instanceof GuardrailMaskingEncoder) {
            // Já está configurado
            return;
        }

        String pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";
        
        if (currentEncoder instanceof PatternLayoutEncoder) {
            pattern = ((PatternLayoutEncoder) currentEncoder).getPattern();
        }

        // Cria novo encoder com mascaramento
        GuardrailMaskingEncoder maskingEncoder = new GuardrailMaskingEncoder();
        maskingEncoder.setContext(loggerContext);
        maskingEncoder.setPattern(pattern);
        maskingEncoder.start();

        // Para e substitui o encoder
        if (currentEncoder != null) {
            currentEncoder.stop();
        }
        
        appender.setEncoder(maskingEncoder);
        
        log.debug("Configured masking encoder for appender: {}", appender.getName());
    }

    /**
     * Verifica se o mascaramento está configurado.
     *
     * @return true se configurado
     */
    public static boolean isConfigured() {
        return configured;
    }

    /**
     * Retorna as propriedades de configuração atuais.
     *
     * @return propriedades atuais ou null se não configurado
     */
    public static GuardrailLoggerProperties getCurrentProperties() {
        return currentProperties;
    }

    /**
     * Reseta a configuração para estado inicial.
     */
    public static synchronized void reset() {
        configured = false;
        currentProperties = null;
        SanitizationEngine.getInstance().reset();
        FormatterRegistry.getInstance().clear();
    }
}
