package com.guardrail.logger.core;

/**
 * Interface para formatadores de dados sensíveis.
 * 
 * <p>Formatadores são responsáveis por aplicar uma formatação específica aos dados
 * antes ou durante o processo de ofuscação, permitindo mascaramento parcial com
 * preservação de formato.</p>
 * 
 * <p>Exemplo: CPF "12345678909" pode ser formatado como "***456789**" mantendo
 * parte do dado visível para identificação.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
@FunctionalInterface
public interface Formatter {

    /**
     * Formata e ofusca o valor fornecido.
     *
     * @param value o valor original a ser formatado e ofuscado
     * @return o valor formatado e ofuscado
     */
    String format(String value);

    /**
     * Retorna o tipo de dado que este formatador suporta.
     *
     * @return o tipo de dado suportado
     */
    default DataType getDataType() {
        return DataType.GENERIC;
    }

    /**
     * Retorna o nome identificador do formatador.
     *
     * @return nome do formatador
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Verifica se o valor informado é válido para este formatador.
     *
     * @param value o valor a ser validado
     * @return true se o valor é válido para formatação
     */
    default boolean isValid(String value) {
        return value != null && !value.isBlank();
    }
}
