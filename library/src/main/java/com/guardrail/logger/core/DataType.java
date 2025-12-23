package com.guardrail.logger.core;

/**
 * Enumeração dos tipos de dados sensíveis suportados pela biblioteca.
 * 
 * <p>Cada tipo de dado pode ter regras específicas de ofuscação e formatação,
 * permitindo tratamento adequado conforme a natureza do dado.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public enum DataType {

    /**
     * Cadastro de Pessoa Física (CPF) - 11 dígitos.
     */
    CPF("cpf", "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"),

    /**
     * Cadastro Nacional de Pessoa Jurídica (CNPJ) - 14 dígitos.
     */
    CNPJ("cnpj", "\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}"),

    /**
     * Registro Geral (RG) - documento de identidade.
     */
    RG("rg", "\\d{7,9}"),

    /**
     * Endereço de e-mail.
     */
    EMAIL("email", "[\\w.-]+@[\\w.-]+\\.\\w+"),

    /**
     * Número de telefone.
     */
    PHONE("phone", "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}"),

    /**
     * Número de cartão de crédito.
     */
    CREDIT_CARD("creditCard", "\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}"),

    /**
     * Endereço IP (IPv4).
     */
    IP_ADDRESS("ipAddress", "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}"),

    /**
     * Senha ou token de autenticação.
     */
    PASSWORD("password", ".*"),

    /**
     * Nome completo de pessoa.
     */
    NAME("name", "[A-Za-zÀ-ÿ\\s]+"),

    /**
     * Endereço físico.
     */
    ADDRESS("address", ".*"),

    /**
     * Valor monetário ou renda.
     */
    MONETARY("monetary", "\\d+[,.]?\\d*"),

    /**
     * Número de conta bancária.
     */
    BANK_ACCOUNT("bankAccount", "\\d{5,12}"),

    /**
     * Agência bancária.
     */
    BANK_AGENCY("bankAgency", "\\d{4,6}"),

    /**
     * Tipo genérico para dados não categorizados.
     */
    GENERIC("generic", ".*");

    private final String key;
    private final String defaultPattern;

    DataType(String key, String defaultPattern) {
        this.key = key;
        this.defaultPattern = defaultPattern;
    }

    /**
     * Retorna a chave identificadora do tipo de dado.
     *
     * @return chave do tipo de dado
     */
    public String getKey() {
        return key;
    }

    /**
     * Retorna o padrão regex padrão para identificação do tipo de dado.
     *
     * @return padrão regex
     */
    public String getDefaultPattern() {
        return defaultPattern;
    }

    /**
     * Busca um DataType pela sua chave.
     *
     * @param key a chave a ser buscada
     * @return o DataType correspondente ou GENERIC se não encontrado
     */
    public static DataType fromKey(String key) {
        if (key == null || key.isBlank()) {
            return GENERIC;
        }
        for (DataType type : values()) {
            if (type.key.equalsIgnoreCase(key)) {
                return type;
            }
        }
        return GENERIC;
    }
}
