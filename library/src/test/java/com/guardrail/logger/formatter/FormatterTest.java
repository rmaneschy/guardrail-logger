package com.guardrail.logger.formatter;

import com.guardrail.logger.core.DataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de Formatadores")
class FormatterTest {

    @Nested
    @DisplayName("CpfFormatter")
    class CpfFormatterTests {

        private final CpfFormatter formatter = new CpfFormatter();

        @Test
        @DisplayName("Deve ofuscar CPF válido mantendo dígitos do meio")
        void shouldObfuscateValidCpf() {
            String result = formatter.format("12345678909");
            assertThat(result).isEqualTo("***456789**");
        }

        @Test
        @DisplayName("Deve ofuscar CPF formatado")
        void shouldObfuscateFormattedCpf() {
            String result = formatter.format("123.456.789-09");
            assertThat(result).isEqualTo("***456789**");
        }

        @Test
        @DisplayName("Deve retornar máscara para CPF inválido")
        void shouldReturnMaskForInvalidCpf() {
            String result = formatter.format("123");
            assertThat(result).isEqualTo("***");
        }

        @Test
        @DisplayName("Deve retornar máscara para valor nulo")
        void shouldReturnMaskForNull() {
            String result = formatter.format(null);
            assertThat(result).isEqualTo("***");
        }

        @Test
        @DisplayName("Deve retornar tipo de dado CPF")
        void shouldReturnCpfDataType() {
            assertThat(formatter.getDataType()).isEqualTo(DataType.CPF);
        }
    }

    @Nested
    @DisplayName("CnpjFormatter")
    class CnpjFormatterTests {

        private final CnpjFormatter formatter = new CnpjFormatter();

        @Test
        @DisplayName("Deve ofuscar CNPJ válido")
        void shouldObfuscateValidCnpj() {
            String result = formatter.format("12345678000190");
            assertThat(result).isEqualTo("**345678****90");
        }

        @Test
        @DisplayName("Deve ofuscar CNPJ formatado")
        void shouldObfuscateFormattedCnpj() {
            String result = formatter.format("12.345.678/0001-90");
            assertThat(result).isEqualTo("**345678****90");
        }

        @Test
        @DisplayName("Deve retornar tipo de dado CNPJ")
        void shouldReturnCnpjDataType() {
            assertThat(formatter.getDataType()).isEqualTo(DataType.CNPJ);
        }
    }

    @Nested
    @DisplayName("EmailFormatter")
    class EmailFormatterTests {

        private final EmailFormatter formatter = new EmailFormatter();

        @Test
        @DisplayName("Deve ofuscar email válido")
        void shouldObfuscateValidEmail() {
            String result = formatter.format("usuario@dominio.com");
            assertThat(result).isEqualTo("us***@dom***.com");
        }

        @Test
        @DisplayName("Deve ofuscar email com usuário curto")
        void shouldObfuscateShortUserEmail() {
            String result = formatter.format("ab@dominio.com");
            assertThat(result).contains("@");
            assertThat(result).contains("dom");
        }

        @Test
        @DisplayName("Deve retornar máscara para email inválido")
        void shouldReturnMaskForInvalidEmail() {
            String result = formatter.format("invalid-email");
            assertThat(result).isEqualTo("***");
        }

        @Test
        @DisplayName("Deve retornar tipo de dado EMAIL")
        void shouldReturnEmailDataType() {
            assertThat(formatter.getDataType()).isEqualTo(DataType.EMAIL);
        }
    }

    @Nested
    @DisplayName("CreditCardFormatter")
    class CreditCardFormatterTests {

        private final CreditCardFormatter formatter = new CreditCardFormatter();

        @Test
        @DisplayName("Deve ofuscar cartão de crédito mostrando últimos 4 dígitos")
        void shouldObfuscateCreditCard() {
            String result = formatter.format("4111111111111111");
            assertThat(result).isEqualTo("************1111");
        }

        @Test
        @DisplayName("Deve ofuscar cartão formatado")
        void shouldObfuscateFormattedCreditCard() {
            String result = formatter.format("4111-1111-1111-1111");
            assertThat(result).isEqualTo("************1111");
        }

        @Test
        @DisplayName("Deve retornar tipo de dado CREDIT_CARD")
        void shouldReturnCreditCardDataType() {
            assertThat(formatter.getDataType()).isEqualTo(DataType.CREDIT_CARD);
        }
    }

    @Nested
    @DisplayName("PhoneFormatter")
    class PhoneFormatterTests {

        private final PhoneFormatter formatter = new PhoneFormatter();

        @Test
        @DisplayName("Deve ofuscar telefone celular")
        void shouldObfuscateCellPhone() {
            String result = formatter.format("11987654321");
            assertThat(result).contains("(11)");
            assertThat(result).contains("4321");
        }

        @Test
        @DisplayName("Deve ofuscar telefone fixo")
        void shouldObfuscateLandline() {
            String result = formatter.format("1134567890");
            assertThat(result).contains("(11)");
        }

        @Test
        @DisplayName("Deve retornar tipo de dado PHONE")
        void shouldReturnPhoneDataType() {
            assertThat(formatter.getDataType()).isEqualTo(DataType.PHONE);
        }
    }

    @Nested
    @DisplayName("NameFormatter")
    class NameFormatterTests {

        private final NameFormatter formatter = new NameFormatter();

        @Test
        @DisplayName("Deve ofuscar nome mantendo iniciais")
        void shouldObfuscateNameKeepingInitials() {
            String result = formatter.format("JOSE DA SILVA");
            assertThat(result).isEqualTo("J*** D* S****");
        }

        @Test
        @DisplayName("Deve ofuscar nome simples")
        void shouldObfuscateSimpleName() {
            String result = formatter.format("Maria");
            assertThat(result).isEqualTo("M****");
        }

        @Test
        @DisplayName("Deve retornar tipo de dado NAME")
        void shouldReturnNameDataType() {
            assertThat(formatter.getDataType()).isEqualTo(DataType.NAME);
        }
    }

    @Nested
    @DisplayName("MonetaryFormatter")
    class MonetaryFormatterTests {

        private final MonetaryFormatter formatter = new MonetaryFormatter();

        @Test
        @DisplayName("Deve ofuscar valor monetário")
        void shouldObfuscateMonetaryValue() {
            String result = formatter.format("56789.98");
            assertThat(result).contains("*");
        }

        @Test
        @DisplayName("Deve ofuscar valor inteiro")
        void shouldObfuscateIntegerValue() {
            String result = formatter.format("1000");
            assertThat(result).isEqualTo("****");
        }

        @Test
        @DisplayName("Deve retornar tipo de dado MONETARY")
        void shouldReturnMonetaryDataType() {
            assertThat(formatter.getDataType()).isEqualTo(DataType.MONETARY);
        }
    }
}
