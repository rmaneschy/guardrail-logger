package com.guardrail.logger.util;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.formatter.*;
import com.guardrail.logger.registry.FormatterRegistry;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes do Utilitário Obfuscator")
class ObfuscatorUtilTest {

    @BeforeEach
    void setUp() {
        // Registra formatadores padrão
        FormatterRegistry registry = FormatterRegistry.getInstance();
        registry.register(DataType.CPF, new CpfFormatter());
        registry.register(DataType.CNPJ, new CnpjFormatter());
        registry.register(DataType.EMAIL, new EmailFormatter());
        registry.register(DataType.CREDIT_CARD, new CreditCardFormatter());
        registry.register(DataType.PHONE, new PhoneFormatter());
        registry.register(DataType.NAME, new NameFormatter());
        registry.register(DataType.MONETARY, new MonetaryFormatter());
    }

    @AfterEach
    void tearDown() {
        FormatterRegistry.getInstance().clear();
    }

    @Nested
    @DisplayName("Método sanitize")
    class SanitizeTests {

        @Test
        @DisplayName("Deve retornar máscara padrão para qualquer valor")
        void shouldReturnDefaultMask() {
            String result = Obfuscator.sanitize("qualquer valor");
            assertThat(result).isEqualTo("***");
        }

        @Test
        @DisplayName("Deve retornar máscara padrão para null")
        void shouldReturnDefaultMaskForNull() {
            String result = Obfuscator.sanitize(null);
            assertThat(result).isEqualTo("***");
        }

        @Test
        @DisplayName("Deve usar máscara customizada")
        void shouldUseCustomMask() {
            String result = Obfuscator.sanitize("valor", "[HIDDEN]");
            assertThat(result).isEqualTo("[HIDDEN]");
        }
    }

    @Nested
    @DisplayName("Método format com DataType")
    class FormatWithDataTypeTests {

        @Test
        @DisplayName("Deve formatar CPF corretamente")
        void shouldFormatCpf() {
            String result = Obfuscator.format("12345678909", DataType.CPF);
            assertThat(result).isEqualTo("***456789**");
        }

        @Test
        @DisplayName("Deve formatar CNPJ corretamente")
        void shouldFormatCnpj() {
            String result = Obfuscator.format("12345678000190", DataType.CNPJ);
            assertThat(result).isEqualTo("**345678****90");
        }

        @Test
        @DisplayName("Deve formatar email corretamente")
        void shouldFormatEmail() {
            String result = Obfuscator.format("usuario@dominio.com", DataType.EMAIL);
            assertThat(result).isEqualTo("us***@dom***.com");
        }

        @Test
        @DisplayName("Deve retornar máscara para null")
        void shouldReturnMaskForNull() {
            String result = Obfuscator.format(null, DataType.CPF);
            assertThat(result).isEqualTo("***");
        }
    }

    @Nested
    @DisplayName("Método partial")
    class PartialTests {

        @Test
        @DisplayName("Deve manter início e fim visíveis")
        void shouldKeepStartAndEndVisible() {
            String result = Obfuscator.partial("12345678909", 3, 2);
            assertThat(result).startsWith("123");
            assertThat(result).endsWith("09");
        }

        @Test
        @DisplayName("Deve usar caractere de máscara customizado")
        void shouldUseCustomMaskChar() {
            String result = Obfuscator.partial("12345678909", 3, 2, '#');
            assertThat(result).contains("######");
        }

        @Test
        @DisplayName("Deve retornar máscara para null")
        void shouldReturnMaskForNull() {
            String result = Obfuscator.partial(null, 3, 2);
            assertThat(result).isEqualTo("***");
        }
    }

    @Nested
    @DisplayName("Métodos de conveniência")
    class ConvenienceMethodsTests {

        @Test
        @DisplayName("Método cpf deve formatar CPF")
        void cpfMethodShouldFormatCpf() {
            String result = Obfuscator.cpf("12345678909");
            assertThat(result).isEqualTo("***456789**");
        }

        @Test
        @DisplayName("Método cnpj deve formatar CNPJ")
        void cnpjMethodShouldFormatCnpj() {
            String result = Obfuscator.cnpj("12345678000190");
            assertThat(result).isEqualTo("**345678****90");
        }

        @Test
        @DisplayName("Método email deve formatar email")
        void emailMethodShouldFormatEmail() {
            String result = Obfuscator.email("usuario@dominio.com");
            assertThat(result).isEqualTo("us***@dom***.com");
        }

        @Test
        @DisplayName("Método creditCard deve formatar cartão")
        void creditCardMethodShouldFormatCard() {
            String result = Obfuscator.creditCard("4111111111111111");
            assertThat(result).isEqualTo("************1111");
        }

        @Test
        @DisplayName("Método name deve formatar nome")
        void nameMethodShouldFormatName() {
            String result = Obfuscator.name("JOSE DA SILVA");
            assertThat(result).isEqualTo("J*** D* S****");
        }

        @Test
        @DisplayName("Método password deve retornar máscara fixa")
        void passwordMethodShouldReturnFixedMask() {
            String result = Obfuscator.password("senhaSecreta123");
            assertThat(result).isEqualTo("********");
        }
    }

    @Nested
    @DisplayName("Método isObfuscated")
    class IsObfuscatedTests {

        @Test
        @DisplayName("Deve retornar true para valor ofuscado")
        void shouldReturnTrueForObfuscatedValue() {
            assertThat(Obfuscator.isObfuscated("*****678***")).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false para valor não ofuscado")
        void shouldReturnFalseForNonObfuscatedValue() {
            assertThat(Obfuscator.isObfuscated("12345678909")).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false para null")
        void shouldReturnFalseForNull() {
            assertThat(Obfuscator.isObfuscated(null)).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false para string vazia")
        void shouldReturnFalseForEmpty() {
            assertThat(Obfuscator.isObfuscated("")).isFalse();
        }
    }
}
