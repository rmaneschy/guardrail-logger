package com.guardrail.logger.core;

import com.guardrail.logger.engine.SanitizationPatterns;

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
    CPF("cpf", SanitizationPatterns.CPF),

    /**
     * Cadastro Nacional de Pessoa Jurídica (CNPJ) - 14 dígitos.
     */
    CNPJ("cnpj", SanitizationPatterns.CNPJ),

    /**
     * Registro Geral (RG) - documento de identidade.
     */
    RG("rg", SanitizationPatterns.RG),

    /**
     * Endereço de e-mail.
     */
    EMAIL("email", SanitizationPatterns.EMAIL),

    /**
     * Número de telefone.
     */
    PHONE("phone", SanitizationPatterns.PHONE),

    /**
     * Número de cartão de crédito.
     */
    CREDIT_CARD("creditCard", SanitizationPatterns.CREDIT_CARD),

    /**
     * Endereço IP (IPv4).
     */
    IP_ADDRESS("ipAddress", SanitizationPatterns.IP_ADDRESS),

    /**
     * Senha ou token de autenticação.
     */
    PASSWORD("password", SanitizationPatterns.GENERIC),

    /**
     * Nome completo de pessoa.
     */
    NAME("name", SanitizationPatterns.NAME),

    /**
     * Endereço físico.
     */
    ADDRESS("address", SanitizationPatterns.GENERIC),

    /**
     * Valor monetário ou renda.
     */
    MONETARY("monetary", SanitizationPatterns.MONETARY),

    /**
     * Número de conta bancária.
     */
    BANK_ACCOUNT("account", SanitizationPatterns.BANK_ACCOUNT),

    /**
     * Agência bancária.
     */
    BANK_AGENCY("bankAgency", SanitizationPatterns.BANK_AGENCY),

    /**
     * Tipo genérico para dados não categorizados.
     */
    GENERIC("generic", SanitizationPatterns.GENERIC);

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
