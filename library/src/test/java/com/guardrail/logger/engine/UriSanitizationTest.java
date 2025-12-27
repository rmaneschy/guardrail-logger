package com.guardrail.logger.engine;

import com.guardrail.logger.config.GuardrailLoggerProperties;
import com.guardrail.logger.config.SensitiveField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UriSanitizationTest {

    private SanitizationEngine engine;

    @BeforeEach
    void setUp() {
        engine = SanitizationEngine.getInstance();
        engine.reset();
    }

    @Test
    @DisplayName("Deve ofuscar parâmetros de query em URLs (Simples)")
    void shouldObfuscateQueryParametersSimple() {
        GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
        properties.setEnabled(true);
        properties.addSensitiveField(SensitiveField.builder()
                .name("telefone")
                .visibleCharsStart(2)
                .visibleCharsEnd(3)
                .build());

        engine.configure(properties);

        String input = "GET http://demo.com/api?telefone=6378273937";
        String result = engine.sanitize(input);

        assertThat(result).contains("telefone=");
        assertThat(result).doesNotContain("6378273937");
    }

    @Test
    @DisplayName("Deve ofuscar parâmetros de query em URLs")
    void shouldObfuscateQueryParameters() {
        GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
        properties.setEnabled(true);
        properties.setAutoDetect(false);
        properties.addSensitiveField(SensitiveField.builder()
                .name("nome")
                .visibleCharsStart(0)
                .visibleCharsEnd(0)
                .build());
        properties.addSensitiveField(SensitiveField.builder()
                .name("telefone")
                .visibleCharsStart(2)
                .visibleCharsEnd(3)
                .build());
        
        engine.configure(properties);

        String input = "GET http://demo.com/api?nome=sdskdadghsa&telefone=6378273937";
        String result = engine.sanitize(input);

        assertThat(result).contains("nome=***");
        assertThat(result).contains("telefone=");
        assertThat(result).doesNotContain("6378273937");
        assertThat(result).doesNotContain("sdskdadghsa");
    }

    @Test
    @DisplayName("Deve ofuscar parâmetros de path em URLs")
    void shouldObfuscatePathParameters() {
        GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
        properties.setEnabled(true);
        properties.addSensitiveField(SensitiveField.builder()
                .name("usuario")
                .visibleCharsStart(3)
                .visibleCharsEnd(2)
                .build());
        
        engine.configure(properties);

        String input = "GET http://demo.com/api/usuario/26783764789?nome=sdskdadghsa";
        String result = engine.sanitize(input);

        // No exemplo: /usuario/26783764789 -> /usuario/***83764***
        // Aqui o campo "usuario" é o que vem DEPOIS de /usuario/
        
        assertThat(result).contains("/usuario/");
        assertThat(result).doesNotContain("26783764789");
    }
}
