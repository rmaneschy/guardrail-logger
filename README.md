# Guardrail Logger

Biblioteca Java para proteção de dados sensíveis em logs, em conformidade com a LGPD (Lei Geral de Proteção de Dados).

## Visão Geral

O **Guardrail Logger** é uma biblioteca que oferece mascaramento automático de dados sensíveis em logs de aplicações Spring Boot. A solução foi projetada para ter o menor impacto possível na performance da aplicação, utilizando padrões pré-compilados e uma arquitetura extensível.

### Principais Características

A biblioteca oferece mascaramento automático de dados sensíveis através de um Appender customizado que é inserido programaticamente via `ApplicationListener<ApplicationReadyEvent>`. Isso garante compatibilidade tanto com projetos que utilizam `logback-spring.xml` quanto com aqueles que usam apenas `logback.xml`.

O sistema é extensível, permitindo a criação de formatadores e ofuscadores customizados além dos previamente implementados. A configuração pode ser feita via properties ou programaticamente, oferecendo flexibilidade para diferentes cenários de uso.

## Requisitos

| Componente | Versão |
|------------|--------|
| JDK | 21+ |
| Spring Boot | 3.x |
| Gradle | 8.x |
| Logback | 1.4+ |

## Instalação

### Gradle (Groovy)

```groovy
dependencies {
    implementation 'com.guardrail:guardrail-logger:1.0.0'
}
```

### Maven

```xml
<dependency>
    <groupId>com.guardrail</groupId>
    <artifactId>guardrail-logger</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuração

### Via application.yml

```yaml
guardrail:
  logger:
    enabled: true
    mask-char: '*'
    default-mask: '***'
    auto-detect: true
    sensitive-fields:
      - name: documento
        data-type: CPF
      - name: cpf
        data-type: CPF
      - name: email
        data-type: EMAIL
      - name: nome
        data-type: NAME
      - name: renda
        data-type: MONETARY
```

### Via Código (Programaticamente)

```java
import com.guardrail.logger.GuardrailLoggerBuilder;
import com.guardrail.logger.core.DataType;

@Configuration
public class GuardrailConfig {

    @PostConstruct
    public void configureGuardrail() {
        GuardrailLoggerBuilder.create()
            .enabled(true)
            .maskChar('*')
            .defaultMask("***")
            .addSensitiveField("documento", DataType.CPF)
            .addSensitiveField("email", DataType.EMAIL)
            .addSensitiveField("nome", DataType.NAME)
            .autoDetect(true)
            .build();
    }
}
```

## Uso

### Ofuscação Automática em Logs

Uma vez configurado, o Guardrail Logger intercepta automaticamente todas as mensagens de log e aplica mascaramento aos dados sensíveis detectados.

```java
// Entrada no log
log.info("Cliente cadastrado: documento=12345678909, nome=JOSE DA SILVA");

// Saída no log (ofuscada)
// Cliente cadastrado: documento=***456789**, nome=J*** D* S****
```

### Formatos de Log Suportados

O Guardrail Logger detecta e ofusca dados sensíveis em diversos formatos de log:

| Formato | Exemplo de Entrada | Exemplo de Saída |
|---------|-------------------|------------------|
| toString simples | `documento=12345678909` | `documento=***456789**` |
| toString com aspas | `documento="12345678909"` | `documento="***456789**"` |
| JSON | `{"documento": "12345678909"}` | `{"documento": "***456789**"}` |
| Texto livre | `cliente JOSE DA SILVA` | `cliente J*** D* S****` |

### Ofuscação Manual (Método Estático)

Para casos onde a ofuscação automática não é aplicável, utilize os métodos estáticos da classe `Obfuscator`:

```java
import com.guardrail.logger.util.Obfuscator;

// Ofuscação simples
log.info("Documento: " + Obfuscator.sanitize(cpf));

// Ofuscação com formatador específico
log.info("CPF: " + Obfuscator.format(cpf, DataType.CPF));

// Ofuscação parcial
log.info("Email: " + Obfuscator.partial(email, 2, 0));

// Métodos de conveniência
log.info("CPF: " + Obfuscator.cpf("12345678909"));
log.info("Email: " + Obfuscator.email("usuario@dominio.com"));
log.info("Nome: " + Obfuscator.name("JOSE DA SILVA"));
```

### Anotação @Sensitive

Utilize a anotação `@Sensitive` para marcar campos sensíveis em suas classes:

```java
import com.guardrail.logger.annotation.Sensitive;
import com.guardrail.logger.annotation.SensitiveToString;
import com.guardrail.logger.core.DataType;

@SensitiveToString
public class Cliente {
    
    @Sensitive(type = DataType.CPF)
    private String documento;
    
    @Sensitive(type = DataType.NAME)
    private String nome;
    
    @Sensitive(type = DataType.MONETARY, visibleStart = 1)
    private BigDecimal renda;
    
    @Override
    public String toString() {
        return SensitiveToStringBuilder.build(this);
    }
}
```

### Builder Fluente para toString

```java
import com.guardrail.logger.util.SensitiveToStringBuilder;

@Override
public String toString() {
    return SensitiveToStringBuilder.builder(this)
        .append("id", id)
        .appendSensitive("documento", documento, DataType.CPF)
        .appendSensitive("nome", nome, DataType.NAME)
        .appendMasked("senha", senha, "********")
        .build();
}
```

## Tipos de Dados Suportados

| DataType | Descrição | Exemplo de Formatação |
|----------|-----------|----------------------|
| `CPF` | Cadastro de Pessoa Física | `***456789**` |
| `CNPJ` | Cadastro Nacional de Pessoa Jurídica | `**345678****90` |
| `EMAIL` | Endereço de e-mail | `us***@dom***.com` |
| `PHONE` | Número de telefone | `(11) *****-4321` |
| `CREDIT_CARD` | Número de cartão de crédito | `************1111` |
| `NAME` | Nome de pessoa | `J*** D* S****` |
| `MONETARY` | Valores monetários | `*****.**` |
| `PASSWORD` | Senhas e tokens | `********` |
| `GENERIC` | Tipo genérico | `***` |

## Extensibilidade

### Criando um Formatador Customizado

```java
import com.guardrail.logger.core.Formatter;
import com.guardrail.logger.core.DataType;

public class CustomCpfFormatter implements Formatter {
    
    @Override
    public String format(String value) {
        if (value == null || value.length() != 11) {
            return "***";
        }
        return value.substring(0, 3) + ".***.***-" + value.substring(9);
    }
    
    @Override
    public DataType getDataType() {
        return DataType.CPF;
    }
    
    @Override
    public String getName() {
        return "customCpfFormatter";
    }
    
    @Override
    public boolean isValid(String value) {
        return value != null && value.replaceAll("[^0-9]", "").length() == 11;
    }
}
```

### Registrando Formatadores via Extension

```java
import com.guardrail.logger.extension.FormatterExtension;
import org.springframework.stereotype.Component;

@Component
public class CustomFormatterExtension implements FormatterExtension {
    
    @Override
    public void register(FormatterRegistry registry) {
        registry.register("customCpf", new CustomCpfFormatter());
        registry.register(DataType.CPF, new CustomCpfFormatter());
    }
    
    @Override
    public int getOrder() {
        return 50; // Prioridade de registro
    }
}
```

### Criando um Ofuscador Customizado

```java
import com.guardrail.logger.core.Obfuscator;

public class HashObfuscator implements Obfuscator {
    
    @Override
    public String obfuscate(String value) {
        if (value == null) return "***";
        return DigestUtils.sha256Hex(value).substring(0, 8);
    }
    
    @Override
    public char getMaskChar() {
        return '*';
    }
}
```

## Integração com Graylog (GELF)

O Guardrail Logger é compatível com appenders GELF para envio de logs ao Graylog. A configuração é feita automaticamente através do `ApplicationListener`, garantindo que todos os logs sejam sanitizados antes do envio.

```yaml
guardrail:
  logger:
    enabled: true
    gelf:
      enabled: true
      host: graylog.example.com
      port: 12201
```

## Performance

A biblioteca foi projetada para minimizar o impacto na performance da aplicação. As principais otimizações incluem:

O uso de padrões regex pré-compilados evita a recompilação a cada mensagem de log. O cache de formatadores e ofuscadores reduz a criação de objetos. A detecção precoce de mensagens já ofuscadas evita processamento desnecessário. A estrutura singleton para registros e engine garante acesso rápido aos componentes.

## Comandos Úteis

### Rodar os testes com relatório de cobertura

```bash
./gradlew test jacocoTestReport codeCoverageReport --stacktrace
```

### Gerar uma versão publicando no maven local

```bash
./gradlew :library:publishToMavenLocal
```

### Gerar uma versão publicando no Artifactory/Nexus

```bash
./gradlew :library:publish
```

## Contribuição

Contribuições são bem-vindas! Por favor, leia o arquivo CONTRIBUTING.md para detalhes sobre o processo de submissão de pull requests.

## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## Referências

A implementação desta biblioteca foi baseada em pesquisas sobre melhores práticas de mascaramento de dados sensíveis em logs, incluindo referências da LGPD (Lei 13.709/2018) e padrões de segurança como PCI-DSS para dados de cartão de crédito.
