package com.guardrail.logger.engine;

/**
 * Centraliza os padrões de Expressões Regulares (Regex) utilizados para identificação de dados sensíveis.
 * 
 * <p>Esta classe organiza os padrões por formato de dado (JSON, toString, Texto Livre, URI)
 * para facilitar a manutenção e garantir a consistência em toda a biblioteca.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public final class SanitizationPatterns {

    private SanitizationPatterns() {
        // Classe utilitária
    }

    /** Prefixo para tornar a busca case-insensitive */
    public static final String CASE_INSENSITIVE = "(?i)";

    // --- JSON Patterns ---
    /** Formato JSON: "campo": "valor" */
    public static final String JSON_QUOTED = "\"%s\"\\s*:\\s*\"([^\"]+)\"";
    /** Formato JSON: "campo": valor (numérico, booleano ou null) */
    public static final String JSON_UNQUOTED = "\"%s\"\\s*:\\s*([^,}\\s]+)";

    // --- Object toString() Patterns ---
    /** Formato toString: campo="valor" */
    public static final String TO_STRING_DOUBLE_QUOTED = "%s\\s*=\\s*\"([^\"]+)\"";
    /** Formato toString: campo='valor' */
    public static final String TO_STRING_SINGLE_QUOTED = "%s\\s*=\\s*'([^']+)'";
    /** Formato toString: campo=valor */
    public static final String TO_STRING_UNQUOTED = "%s\\s*=\\s*([^,\\]\\}\\s&]+)";

    // --- Free Text Patterns ---
    /** Formato Texto: campo: "valor" */
    public static final String TEXT_COLON_QUOTED = "%s\\s*:\\s*\"([^\"]+)\"";
    /** Formato Texto: campo: valor */
    public static final String TEXT_COLON_UNQUOTED = "%s\\s*:\\s*([^,\\s&]+)";

    // --- URI / URL Patterns ---
    /** Query Parameter: ?campo=valor ou &campo=valor */
    public static final String QUERY_PARAMETER = "([?&]%s=)([^&\\s#]+)";
    /** Path Parameter: /campo/valor */
    public static final String PATH_PARAMETER = "(/%s/)([^/?#\\s]+)";

    // --- Default Data Type Patterns ---
    public static final String CPF = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";
    public static final String CNPJ = "[0-9A-Za-z]{14}|[0-9A-Za-z]{2}\\.[0-9A-Za-z]{3}\\.[0-9A-Za-z]{3}/[0-9A-Za-z]{4}-[0-9A-Za-z]{2}";
    public static final String RG = "\\d{7,9}";
    public static final String EMAIL = "[\\w.-]+@[\\w.-]+\\.\\w+";
    public static final String PHONE = "\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}";
    public static final String CREDIT_CARD = "\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}";
    public static final String IP_ADDRESS = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    public static final String GENERIC = ".*";
    public static final String NAME = "[A-Za-zÀ-ÿ\\s]+";
    public static final String MONETARY = "\\d+[,.]?\\d*";
    public static final String BANK_ACCOUNT = "\\d{5,12}";
    public static final String BANK_AGENCY = "\\d{4,6}";
}
