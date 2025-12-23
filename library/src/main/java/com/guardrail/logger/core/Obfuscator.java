package com.guardrail.logger.core;

/**
 * Interface base para implementação de ofuscadores de dados sensíveis.
 * 
 * <p>Os ofuscadores são responsáveis por transformar dados sensíveis em versões
 * mascaradas, garantindo conformidade com a LGPD e outras regulamentações de
 * proteção de dados.</p>
 * 
 * <p>Implementações customizadas podem ser criadas para atender necessidades
 * específicas de cada aplicação.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
@FunctionalInterface
public interface Obfuscator {

    /**
     * Aplica a ofuscação ao valor fornecido.
     *
     * @param value o valor original a ser ofuscado
     * @return o valor ofuscado
     */
    String obfuscate(String value);

    /**
     * Retorna o caractere padrão utilizado para mascaramento.
     *
     * @return caractere de máscara (padrão: '*')
     */
    default char getMaskChar() {
        return '*';
    }

    /**
     * Verifica se o ofuscador suporta o tipo de dado informado.
     *
     * @param dataType o tipo de dado a ser verificado
     * @return true se o ofuscador suporta o tipo de dado
     */
    default boolean supports(DataType dataType) {
        return true;
    }
}
