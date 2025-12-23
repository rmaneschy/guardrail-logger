package com.guardrail.logger.obfuscator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de Ofuscadores")
class ObfuscatorTest {

    @Nested
    @DisplayName("DefaultObfuscator")
    class DefaultObfuscatorTests {

        @Test
        @DisplayName("Deve retornar máscara padrão para qualquer valor")
        void shouldReturnDefaultMask() {
            DefaultObfuscator obfuscator = new DefaultObfuscator();
            assertThat(obfuscator.obfuscate("qualquer valor")).isEqualTo("***");
        }

        @Test
        @DisplayName("Deve retornar máscara padrão para valor nulo")
        void shouldReturnDefaultMaskForNull() {
            DefaultObfuscator obfuscator = new DefaultObfuscator();
            assertThat(obfuscator.obfuscate(null)).isEqualTo("***");
        }

        @Test
        @DisplayName("Deve retornar máscara padrão para valor vazio")
        void shouldReturnDefaultMaskForEmpty() {
            DefaultObfuscator obfuscator = new DefaultObfuscator();
            assertThat(obfuscator.obfuscate("")).isEqualTo("***");
        }

        @Test
        @DisplayName("Deve usar caractere de máscara customizado")
        void shouldUseCustomMaskChar() {
            DefaultObfuscator obfuscator = new DefaultObfuscator('#');
            assertThat(obfuscator.getMaskChar()).isEqualTo('#');
        }

        @Test
        @DisplayName("Deve usar máscara customizada")
        void shouldUseCustomMask() {
            DefaultObfuscator obfuscator = new DefaultObfuscator('*', "[REDACTED]");
            assertThat(obfuscator.obfuscate("valor")).isEqualTo("[REDACTED]");
        }
    }

    @Nested
    @DisplayName("PartialObfuscator")
    class PartialObfuscatorTests {

        @Test
        @DisplayName("Deve manter caracteres do início e fim visíveis")
        void shouldKeepStartAndEndVisible() {
            PartialObfuscator obfuscator = new PartialObfuscator(3, 2);
            String result = obfuscator.obfuscate("12345678909");
            assertThat(result).startsWith("123");
            assertThat(result).endsWith("09");
            assertThat(result).contains("*");
        }

        @Test
        @DisplayName("Deve mascarar valor menor que total visível")
        void shouldMaskValueSmallerThanVisible() {
            PartialObfuscator obfuscator = new PartialObfuscator(3, 3);
            String result = obfuscator.obfuscate("12345");
            assertThat(result).isEqualTo("*****");
        }

        @Test
        @DisplayName("Deve usar configurações padrão")
        void shouldUseDefaultSettings() {
            PartialObfuscator obfuscator = new PartialObfuscator();
            assertThat(obfuscator.getVisibleCharsStart()).isEqualTo(3);
            assertThat(obfuscator.getVisibleCharsEnd()).isEqualTo(2);
        }

        @Test
        @DisplayName("Deve respeitar comprimento mínimo da máscara")
        void shouldRespectMinMaskLength() {
            PartialObfuscator obfuscator = new PartialObfuscator(2, 2, '*', 5);
            String result = obfuscator.obfuscate("123456");
            assertThat(result).contains("*****");
        }

        @Test
        @DisplayName("Deve usar caractere de máscara customizado")
        void shouldUseCustomMaskChar() {
            PartialObfuscator obfuscator = new PartialObfuscator(2, 2, '#', 3);
            String result = obfuscator.obfuscate("12345678");
            assertThat(result).contains("####");
        }

        @Test
        @DisplayName("Deve retornar máscara para valor nulo")
        void shouldReturnMaskForNull() {
            PartialObfuscator obfuscator = new PartialObfuscator();
            String result = obfuscator.obfuscate(null);
            assertThat(result).isEqualTo("***");
        }

        @Test
        @DisplayName("Deve manter apenas início visível quando fim é zero")
        void shouldKeepOnlyStartVisible() {
            PartialObfuscator obfuscator = new PartialObfuscator(3, 0);
            String result = obfuscator.obfuscate("12345678");
            assertThat(result).startsWith("123");
            assertThat(result).doesNotContain("8");
        }

        @Test
        @DisplayName("Deve manter apenas fim visível quando início é zero")
        void shouldKeepOnlyEndVisible() {
            PartialObfuscator obfuscator = new PartialObfuscator(0, 3);
            String result = obfuscator.obfuscate("12345678");
            assertThat(result).endsWith("678");
        }
    }
}
