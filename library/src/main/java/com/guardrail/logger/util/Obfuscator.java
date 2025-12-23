package com.guardrail.logger.util;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;
import com.guardrail.logger.engine.SanitizationEngine;
import com.guardrail.logger.obfuscator.DefaultObfuscator;
import com.guardrail.logger.obfuscator.PartialObfuscator;
import com.guardrail.logger.registry.FormatterRegistry;

import java.util.Optional;

/**
 * Classe utilitária para ofuscação estática de dados sensíveis.
 * 
 * <p>Esta classe fornece métodos estáticos para ofuscar dados sensíveis
 * diretamente no código, útil para casos onde a ofuscação automática
 * não é aplicável.</p>
 * 
 * <p>Exemplos de uso:</p>
 * <pre>{@code
 * // Ofuscação simples
 * log.info("Cliente: " + Obfuscator.sanitize(cpf));
 * 
 * // Ofuscação com formatador específico
 * log.info("Documento: " + Obfuscator.format(cpf, DataType.CPF));
 * 
 * // Ofuscação parcial
 * log.info("Email: " + Obfuscator.partial(email, 2, 0));
 * 
 * // Sanitização de mensagem completa
 * log.info(Obfuscator.sanitizeMessage("Cliente CPF 12345678909 cadastrado"));
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public final class Obfuscator {

    private static final DefaultObfuscator DEFAULT_OBFUSCATOR = new DefaultObfuscator();
    private static final String DEFAULT_MASK = "***";

    private Obfuscator() {
        // Utility class
    }

    /**
     * Aplica ofuscação simples ao valor, substituindo por máscara padrão.
     *
     * @param value valor a ser ofuscado
     * @return valor ofuscado ou máscara padrão
     */
    public static String sanitize(Object value) {
        if (value == null) {
            return DEFAULT_MASK;
        }
        return DEFAULT_OBFUSCATOR.obfuscate(String.valueOf(value));
    }

    /**
     * Aplica ofuscação simples com máscara customizada.
     *
     * @param value valor a ser ofuscado
     * @param mask máscara a ser utilizada
     * @return valor ofuscado ou máscara customizada
     */
    public static String sanitize(Object value, String mask) {
        if (value == null) {
            return mask;
        }
        return mask;
    }

    /**
     * Aplica formatação específica para o tipo de dado.
     *
     * @param value valor a ser formatado
     * @param dataType tipo de dado
     * @return valor formatado e ofuscado
     */
    public static String format(Object value, DataType dataType) {
        if (value == null) {
            return DEFAULT_MASK;
        }

        String stringValue = String.valueOf(value);
        
        Optional<Formatter> formatter = FormatterRegistry.getInstance().getByDataType(dataType);
        if (formatter.isPresent()) {
            return formatter.get().format(stringValue);
        }

        return DEFAULT_OBFUSCATOR.obfuscate(stringValue);
    }

    /**
     * Aplica formatação usando formatador por nome.
     *
     * @param value valor a ser formatado
     * @param formatterName nome do formatador
     * @return valor formatado e ofuscado
     */
    public static String format(Object value, String formatterName) {
        if (value == null) {
            return DEFAULT_MASK;
        }

        String stringValue = String.valueOf(value);
        
        Optional<Formatter> formatter = FormatterRegistry.getInstance().getByName(formatterName);
        if (formatter.isPresent()) {
            return formatter.get().format(stringValue);
        }

        return DEFAULT_OBFUSCATOR.obfuscate(stringValue);
    }

    /**
     * Aplica ofuscação parcial, mantendo parte do valor visível.
     *
     * @param value valor a ser ofuscado
     * @param visibleStart caracteres visíveis no início
     * @param visibleEnd caracteres visíveis no final
     * @return valor parcialmente ofuscado
     */
    public static String partial(Object value, int visibleStart, int visibleEnd) {
        if (value == null) {
            return DEFAULT_MASK;
        }

        PartialObfuscator obfuscator = new PartialObfuscator(visibleStart, visibleEnd);
        return obfuscator.obfuscate(String.valueOf(value));
    }

    /**
     * Aplica ofuscação parcial com caractere de máscara customizado.
     *
     * @param value valor a ser ofuscado
     * @param visibleStart caracteres visíveis no início
     * @param visibleEnd caracteres visíveis no final
     * @param maskChar caractere de máscara
     * @return valor parcialmente ofuscado
     */
    public static String partial(Object value, int visibleStart, int visibleEnd, char maskChar) {
        if (value == null) {
            return String.valueOf(maskChar).repeat(3);
        }

        PartialObfuscator obfuscator = new PartialObfuscator(visibleStart, visibleEnd, maskChar, 3);
        return obfuscator.obfuscate(String.valueOf(value));
    }

    /**
     * Sanitiza uma mensagem completa, detectando e ofuscando dados sensíveis.
     *
     * @param message mensagem a ser sanitizada
     * @return mensagem com dados sensíveis ofuscados
     */
    public static String sanitizeMessage(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        SanitizationEngine engine = SanitizationEngine.getInstance();
        if (engine.isInitialized()) {
            return engine.sanitize(message);
        }

        return message;
    }

    /**
     * Formata CPF com ofuscação padrão.
     *
     * @param cpf número do CPF
     * @return CPF formatado e ofuscado
     */
    public static String cpf(Object cpf) {
        return format(cpf, DataType.CPF);
    }

    /**
     * Formata CNPJ com ofuscação padrão.
     *
     * @param cnpj número do CNPJ
     * @return CNPJ formatado e ofuscado
     */
    public static String cnpj(Object cnpj) {
        return format(cnpj, DataType.CNPJ);
    }

    /**
     * Formata e-mail com ofuscação padrão.
     *
     * @param email endereço de e-mail
     * @return e-mail formatado e ofuscado
     */
    public static String email(Object email) {
        return format(email, DataType.EMAIL);
    }

    /**
     * Formata telefone com ofuscação padrão.
     *
     * @param phone número de telefone
     * @return telefone formatado e ofuscado
     */
    public static String phone(Object phone) {
        return format(phone, DataType.PHONE);
    }

    /**
     * Formata cartão de crédito com ofuscação padrão (últimos 4 dígitos visíveis).
     *
     * @param creditCard número do cartão
     * @return cartão formatado e ofuscado
     */
    public static String creditCard(Object creditCard) {
        return format(creditCard, DataType.CREDIT_CARD);
    }

    /**
     * Formata nome com ofuscação padrão (apenas iniciais visíveis).
     *
     * @param name nome completo
     * @return nome formatado e ofuscado
     */
    public static String name(Object name) {
        return format(name, DataType.NAME);
    }

    /**
     * Formata valor monetário com ofuscação.
     *
     * @param value valor monetário
     * @return valor formatado e ofuscado
     */
    public static String monetary(Object value) {
        return format(value, DataType.MONETARY);
    }

    /**
     * Ofusca completamente uma senha ou token.
     *
     * @param password senha ou token
     * @return máscara fixa
     */
    public static String password(Object password) {
        return "********";
    }

    /**
     * Verifica se um valor parece já estar ofuscado.
     *
     * @param value valor a verificar
     * @return true se parece estar ofuscado
     */
    public static boolean isObfuscated(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        long maskCount = value.chars().filter(c -> c == '*').count();
        return maskCount > value.length() / 2;
    }
}
