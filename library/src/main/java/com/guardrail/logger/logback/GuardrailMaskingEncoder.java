package com.guardrail.logger.logback;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.guardrail.logger.engine.SanitizationEngine;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Encoder customizado para Logback que aplica mascaramento de dados sensíveis.
 * 
 * <p>Este encoder estende o PatternLayoutEncoder padrão e intercepta
 * todas as mensagens de log para aplicar sanitização antes da codificação.</p>
 * 
 * <p>Pode ser utilizado diretamente em configurações logback.xml:</p>
 * 
 * <pre>{@code
 * <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
 *     <encoder class="com.guardrail.logger.logback.GuardrailMaskingEncoder">
 *         <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
 *     </encoder>
 * </appender>
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class GuardrailMaskingEncoder extends PatternLayoutEncoder {

    private boolean enabled = true;

    @Override
    public byte[] encode(ILoggingEvent event) {
        byte[] originalBytes = super.encode(event);

        if (!enabled) {
            return originalBytes;
        }

        SanitizationEngine engine = SanitizationEngine.getInstance();
        if (!engine.isInitialized()) {
            return originalBytes;
        }

        Charset charset = getCharset() != null ? getCharset() : StandardCharsets.UTF_8;
        String originalMessage = new String(originalBytes, charset);
        String sanitizedMessage = engine.sanitize(originalMessage);

        return sanitizedMessage.getBytes(charset);
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
