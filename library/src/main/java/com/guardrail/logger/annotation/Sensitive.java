package com.guardrail.logger.annotation;

import com.guardrail.logger.core.DataType;

import java.lang.annotation.*;

/**
 * Anotação para marcar campos que contêm dados sensíveis.
 * 
 * <p>Quando aplicada a um campo, indica que o valor deve ser ofuscado
 * em operações de toString(), serialização e logging.</p>
 * 
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * public class Cliente {
 *     @Sensitive(type = DataType.CPF)
 *     private String documento;
 *     
 *     @Sensitive(type = DataType.NAME)
 *     private String nome;
 *     
 *     @Sensitive(type = DataType.MONETARY, visibleStart = 1)
 *     private BigDecimal renda;
 * }
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sensitive {

    /**
     * Tipo de dado sensível.
     * 
     * @return tipo de dado
     */
    DataType type() default DataType.GENERIC;

    /**
     * Nome do formatador customizado a ser utilizado.
     * Se não especificado, usa o formatador padrão do tipo de dado.
     * 
     * @return nome do formatador
     */
    String formatter() default "";

    /**
     * Número de caracteres visíveis no início do valor.
     * 
     * @return caracteres visíveis no início
     */
    int visibleStart() default 0;

    /**
     * Número de caracteres visíveis no final do valor.
     * 
     * @return caracteres visíveis no final
     */
    int visibleEnd() default 0;

    /**
     * Caractere utilizado para mascaramento.
     * 
     * @return caractere de máscara
     */
    char maskChar() default '*';

    /**
     * Máscara fixa a ser utilizada quando não há formatador.
     * 
     * @return máscara fixa
     */
    String mask() default "***";
}
