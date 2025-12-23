# Contribuindo para o Guardrail Logger

Obrigado pelo interesse em contribuir com o Guardrail Logger! Este documento fornece diretrizes para contribuição.

## Como Contribuir

### Reportando Bugs

Ao reportar um bug, por favor inclua as seguintes informações: versão da biblioteca, versão do JDK, versão do Spring Boot, descrição detalhada do problema, passos para reproduzir e logs relevantes (com dados sensíveis removidos).

### Sugerindo Melhorias

Sugestões de melhorias são bem-vindas. Descreva claramente a funcionalidade desejada, o caso de uso e, se possível, uma proposta de implementação.

### Pull Requests

Para submeter um pull request, siga estas etapas:

1. Faça um fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Implemente suas mudanças seguindo os padrões de código
4. Adicione testes para novas funcionalidades
5. Execute os testes (`./gradlew test`)
6. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
7. Push para a branch (`git push origin feature/nova-funcionalidade`)
8. Abra um Pull Request

## Padrões de Código

### Estilo de Código

O projeto segue as convenções de código Java padrão. Utilize formatação consistente, nomes descritivos para variáveis e métodos, e documente classes e métodos públicos com Javadoc.

### Testes

Todos os novos recursos devem incluir testes unitários. Utilize JUnit 5 e AssertJ para asserções. Os testes devem ser claros, concisos e cobrir casos de borda.

### Commits

Utilize mensagens de commit descritivas em português ou inglês. Prefira commits atômicos que representem uma única mudança lógica.

## Estrutura do Projeto

```
guardrail-logger/
├── library/
│   ├── src/
│   │   ├── main/java/com/guardrail/logger/
│   │   │   ├── annotation/     # Anotações
│   │   │   ├── config/         # Classes de configuração
│   │   │   ├── core/           # Interfaces e enums core
│   │   │   ├── engine/         # Engine de sanitização
│   │   │   ├── extension/      # Interfaces de extensão
│   │   │   ├── formatter/      # Formatadores padrão
│   │   │   ├── logback/        # Integração Logback
│   │   │   ├── obfuscator/     # Ofuscadores padrão
│   │   │   ├── registry/       # Registros de componentes
│   │   │   ├── spring/         # Auto-configuração Spring
│   │   │   └── util/           # Utilitários
│   │   └── test/               # Testes unitários
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## Criando Novos Formatadores

Para criar um novo formatador, implemente a interface `Formatter`:

```java
public class MeuFormatter implements Formatter {
    @Override
    public String format(String value) {
        // Implementação
    }
    
    @Override
    public DataType getDataType() {
        return DataType.GENERIC;
    }
    
    @Override
    public String getName() {
        return "meuFormatter";
    }
    
    @Override
    public boolean isValid(String value) {
        // Validação
    }
}
```

## Criando Novos Ofuscadores

Para criar um novo ofuscador, implemente a interface `Obfuscator`:

```java
public class MeuObfuscator implements Obfuscator {
    @Override
    public String obfuscate(String value) {
        // Implementação
    }
    
    @Override
    public char getMaskChar() {
        return '*';
    }
}
```

## Dúvidas

Em caso de dúvidas, abra uma issue no repositório ou entre em contato com a equipe mantenedora.
