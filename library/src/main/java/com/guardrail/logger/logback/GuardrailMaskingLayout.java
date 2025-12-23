package com.guardrail.logger.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.guardrail.logger.engine.SanitizationEngine;

/**
 * Layout customizado para Logback que aplica mascaramento de dados sensíveis.
 * 
 * <p>Este layout estende o PatternLayout padrão do Logback e intercepta
 * todas as mensagens de log para aplicar sanitização antes da saída.</p>
 * 
 * <p>Pode ser utilizado diretamente em configurações logback.xml ou
 * logback-spring.xml:</p>
 * 
 * <pre>{@code
 * <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
 *     <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
 *         <layout class="com.guardrail.logger.logback.GuardrailMaskingLayout">
 *             <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
 *         </layout>
 *     </encoder>
 * </appender>
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class GuardrailMaskingLayout extends PatternLayout {

    private boolean enabled = true;

    @Override
    public String doLayout(ILoggingEvent event) {
        String originalMessage = super.doLayout(event);

        if (!enabled) {
            return originalMessage;
        }

        SanitizationEngine engine = SanitizationEngine.getInstance();
        if (!engine.isInitialized()) {
            return originalMessage;
        }

        return engine.sanitize(originalMessage);
    }

    /**
     * Habilita ou desabilita o mascaramento.
     *
     * @param enabled true para habilitar
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Verifica se o mascaramento está habilitado.
     *
     * @return true se habilitado
     */
    public boolean isEnabled() {
        return enabled;
    }
}
