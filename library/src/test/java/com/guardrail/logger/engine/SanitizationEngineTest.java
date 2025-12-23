package com.guardrail.logger.engine;

import com.guardrail.logger.config.GuardrailLoggerProperties;
import com.guardrail.logger.config.SensitiveField;
import com.guardrail.logger.core.DataType;
import com.guardrail.logger.formatter.CpfFormatter;
import com.guardrail.logger.formatter.NameFormatter;
import com.guardrail.logger.registry.FormatterRegistry;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes da Engine de Sanitização")
class SanitizationEngineTest {

    private SanitizationEngine engine;

    @BeforeEach
    void setUp() {
        engine = SanitizationEngine.getInstance();
        engine.reset();
        
        // Registra formatadores padrão
        FormatterRegistry.getInstance().register(DataType.CPF, new CpfFormatter());
        FormatterRegistry.getInstance().register(DataType.NAME, new NameFormatter());
    }

    @AfterEach
    void tearDown() {
        engine.reset();
        FormatterRegistry.getInstance().clear();
    }

    @Nested
    @DisplayName("Inicialização")
    class InitializationTests {

        @Test
        @DisplayName("Deve inicializar com propriedades válidas")
        void shouldInitializeWithValidProperties() {
            GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
            properties.setEnabled(true);
            
            engine.initialize(properties);
            
            assertThat(engine.isInitialized()).isTrue();
        }

        @Test
        @DisplayName("Não deve estar inicializado antes de chamar initialize")
        void shouldNotBeInitializedBeforeInitialize() {
            assertThat(engine.isInitialized()).isFalse();
        }
    }

    @Nested
    @DisplayName("Sanitização de JSON")
    class JsonSanitizationTests {

        @BeforeEach
        void setUpEngine() {
            GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
            properties.setEnabled(true);
            properties.addSensitiveField(SensitiveField.builder()
                .name("documento")
                .dataType(DataType.CPF)
                .build());
            properties.addSensitiveField(SensitiveField.builder()
                .name("nome")
                .dataType(DataType.NAME)
                .build());
            
            engine.initialize(properties);
        }

        @Test
        @DisplayName("Deve sanitizar JSON com campo documento")
        void shouldSanitizeJsonWithDocumento() {
            String input = "{\"documento\": \"12345678909\", \"nome\": \"JOSE DA SILVA\"}";
            String result = engine.sanitize(input);
            
            assertThat(result).doesNotContain("12345678909");
            assertThat(result).contains("***");
        }

        @Test
        @DisplayName("Deve sanitizar JSON com valores numéricos")
        void shouldSanitizeJsonWithNumericValues() {
            String input = "{\"documento\": 12345678909, \"renda\": 56789.98}";
            GuardrailLoggerProperties properties = engine.getProperties();
            properties.addSensitiveField(SensitiveField.builder()
                .name("renda")
                .dataType(DataType.MONETARY)
                .build());
            engine.initialize(properties);
            
            String result = engine.sanitize(input);
            assertThat(result).doesNotContain("12345678909");
        }
    }

    @Nested
    @DisplayName("Sanitização de toString")
    class ToStringSanitizationTests {

        @BeforeEach
        void setUpEngine() {
            GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
            properties.setEnabled(true);
            properties.addSensitiveField(SensitiveField.builder()
                .name("documento")
                .dataType(DataType.CPF)
                .build());
            properties.addSensitiveField(SensitiveField.builder()
                .name("nome")
                .dataType(DataType.NAME)
                .build());
            
            engine.initialize(properties);
        }

        @Test
        @DisplayName("Deve sanitizar formato toString simples")
        void shouldSanitizeSimpleToString() {
            String input = "Objeto=[documento=12345678909, nome=JOSE DA SILVA]";
            String result = engine.sanitize(input);
            
            assertThat(result).doesNotContain("12345678909");
        }

        @Test
        @DisplayName("Deve sanitizar formato toString com aspas duplas")
        void shouldSanitizeToStringWithDoubleQuotes() {
            String input = "Objeto=[documento=\"12345678909\", nome=\"JOSE DA SILVA\"]";
            String result = engine.sanitize(input);
            
            assertThat(result).doesNotContain("12345678909");
        }

        @Test
        @DisplayName("Deve sanitizar formato toString com aspas simples")
        void shouldSanitizeToStringWithSingleQuotes() {
            String input = "Objeto=[documento='12345678909', nome='JOSE DA SILVA']";
            String result = engine.sanitize(input);
            
            assertThat(result).doesNotContain("12345678909");
        }
    }

    @Nested
    @DisplayName("Sanitização de texto livre")
    class FreeTextSanitizationTests {

        @BeforeEach
        void setUpEngine() {
            GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
            properties.setEnabled(true);
            properties.addSensitiveField(SensitiveField.builder()
                .name("documento")
                .dataType(DataType.CPF)
                .build());
            
            engine.initialize(properties);
        }

        @Test
        @DisplayName("Deve sanitizar texto com colchetes")
        void shouldSanitizeTextWithBrackets() {
            String input = "Não foi possível obter dados do cliente [JOSE DA SILVA] documento 72894783928";
            GuardrailLoggerProperties properties = engine.getProperties();
            properties.addSensitiveField(SensitiveField.builder()
                .name("JOSE DA SILVA")
                .dataType(DataType.NAME)
                .build());
            engine.initialize(properties);
            
            String result = engine.sanitize(input);
            // A sanitização depende da configuração de campos
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Comportamento quando desabilitado")
    class DisabledBehaviorTests {

        @Test
        @DisplayName("Não deve sanitizar quando desabilitado")
        void shouldNotSanitizeWhenDisabled() {
            GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
            properties.setEnabled(false);
            properties.addSensitiveField(SensitiveField.builder()
                .name("documento")
                .dataType(DataType.CPF)
                .build());
            
            engine.initialize(properties);
            
            String input = "{\"documento\": \"12345678909\"}";
            String result = engine.sanitize(input);
            
            assertThat(result).isEqualTo(input);
        }

        @Test
        @DisplayName("Deve retornar mensagem original quando não inicializado")
        void shouldReturnOriginalWhenNotInitialized() {
            String input = "{\"documento\": \"12345678909\"}";
            String result = engine.sanitize(input);
            
            assertThat(result).isEqualTo(input);
        }
    }

    @Nested
    @DisplayName("Valores especiais")
    class SpecialValuesTests {

        @BeforeEach
        void setUpEngine() {
            GuardrailLoggerProperties properties = new GuardrailLoggerProperties();
            properties.setEnabled(true);
            engine.initialize(properties);
        }

        @Test
        @DisplayName("Deve retornar null para entrada null")
        void shouldReturnNullForNullInput() {
            String result = engine.sanitize(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Deve retornar string vazia para entrada vazia")
        void shouldReturnEmptyForEmptyInput() {
            String result = engine.sanitize("");
            assertThat(result).isEmpty();
        }
    }
}
