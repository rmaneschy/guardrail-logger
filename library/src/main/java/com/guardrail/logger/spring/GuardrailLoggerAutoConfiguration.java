package com.guardrail.logger.spring;

import com.guardrail.logger.config.GuardrailLoggerProperties;
import com.guardrail.logger.config.SensitiveField;
import com.guardrail.logger.core.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

/**
 * Auto-configuração Spring Boot para o Guardrail Logger.
 * 
 * <p>Esta classe é automaticamente detectada pelo Spring Boot e configura
 * o mascaramento de dados sensíveis baseado nas propriedades definidas
 * no arquivo de configuração da aplicação.</p>
 * 
 * <p>A auto-configuração pode ser desabilitada via propriedade:</p>
 * <pre>
 * guardrail.logger.enabled=false
 * </pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(ch.qos.logback.classic.Logger.class)
@ConditionalOnProperty(prefix = "guardrail.logger", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties
public class GuardrailLoggerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GuardrailLoggerAutoConfiguration.class);

    @Autowired
    private Environment environment;

    /**
     * Cria o bean de propriedades de configuração.
     */
    @Bean
    @ConfigurationProperties(prefix = "guardrail.logger")
    public GuardrailLoggerProperties guardrailLoggerProperties() {
        return new GuardrailLoggerProperties();
    }

    /**
     * Cria o listener de aplicação que configura o mascaramento.
     */
    @Bean
    public ApplicationListener<ApplicationReadyEvent> guardrailLoggerInitializer(
            GuardrailLoggerProperties properties) {
        
        return event -> {
            try {
                // Carrega campos sensíveis adicionais do environment
                loadSensitiveFieldsFromEnvironment(properties);
                
                // Configura o mascaramento
                GuardrailLoggerApplicationListener.configure(properties);
                
                log.info("Guardrail Logger initialized on ApplicationReadyEvent");
            } catch (Exception e) {
                log.error("Failed to configure Guardrail Logger", e);
            }
        };
    }

    /**
     * Carrega campos sensíveis adicionais do environment.
     */
    private void loadSensitiveFieldsFromEnvironment(GuardrailLoggerProperties properties) {
        // Carrega campos sensíveis do formato: guardrail.logger.fields.{name}={dataType}
        int index = 0;
        while (true) {
            String nameKey = String.format("guardrail.logger.sensitive-fields[%d].name", index);
            String typeKey = String.format("guardrail.logger.sensitive-fields[%d].data-type", index);
            
            String name = environment.getProperty(nameKey);
            if (name == null) break;
            
            String type = environment.getProperty(typeKey, "GENERIC");
            DataType dataType = DataType.fromKey(type);
            
            // Verifica se o campo já existe
            boolean exists = properties.getSensitiveFields().stream()
                .anyMatch(f -> f.getName().equalsIgnoreCase(name));
            
            if (!exists) {
                properties.addSensitiveField(SensitiveField.builder()
                    .name(name)
                    .dataType(dataType)
                    .build());
            }
            
            index++;
        }
    }

    /**
     * Bean para configuração programática.
     */
    @Bean
    public GuardrailLoggerConfigurer guardrailLoggerConfigurer(GuardrailLoggerProperties properties) {
        return new GuardrailLoggerConfigurer(properties);
    }

    /**
     * Classe auxiliar para configuração programática.
     */
    public static class GuardrailLoggerConfigurer {
        private final GuardrailLoggerProperties properties;

        public GuardrailLoggerConfigurer(GuardrailLoggerProperties properties) {
            this.properties = properties;
        }

        /**
         * Adiciona um campo sensível.
         */
        public GuardrailLoggerConfigurer addSensitiveField(String name, DataType dataType) {
            properties.addSensitiveField(SensitiveField.builder()
                .name(name)
                .dataType(dataType)
                .build());
            return this;
        }

        /**
         * Adiciona um campo sensível com configuração completa.
         */
        public GuardrailLoggerConfigurer addSensitiveField(SensitiveField field) {
            properties.addSensitiveField(field);
            return this;
        }

        /**
         * Define o caractere de máscara.
         */
        public GuardrailLoggerConfigurer withMaskChar(char maskChar) {
            properties.setMaskChar(maskChar);
            return this;
        }

        /**
         * Define a máscara padrão.
         */
        public GuardrailLoggerConfigurer withDefaultMask(String defaultMask) {
            properties.setDefaultMask(defaultMask);
            return this;
        }

        /**
         * Habilita ou desabilita a detecção automática.
         */
        public GuardrailLoggerConfigurer withAutoDetect(boolean autoDetect) {
            properties.setAutoDetect(autoDetect);
            return this;
        }

        /**
         * Aplica as configurações.
         */
        public void apply() {
            GuardrailLoggerApplicationListener.configure(properties);
        }

        /**
         * Retorna as propriedades configuradas.
         */
        public GuardrailLoggerProperties getProperties() {
            return properties;
        }
    }
}
