package com.guardrail.logger.annotation;

import java.lang.annotation.*;

/**
 * Anotação para indicar que uma classe deve ter toString() com ofuscação.
 * 
 * <p>Quando aplicada a uma classe, indica que o método toString() gerado
 * ou implementado deve ofuscar campos marcados com @Sensitive.</p>
 * 
 * <p>Esta anotação pode ser processada por:</p>
 * <ul>
 *   <li>Lombok através de extensão customizada</li>
 *   <li>Processador de anotações em tempo de compilação</li>
 *   <li>Utilitário SensitiveToStringBuilder em tempo de execução</li>
 * </ul>
 * 
 * <p>Exemplo de uso:</p>
 * <pre>{@code
 * @SensitiveToString
 * public class Cliente {
 *     @Sensitive(type = DataType.CPF)
 *     private String documento;
 *     
 *     private String nome;
 *     
 *     @Override
 *     public String toString() {
 *         return SensitiveToStringBuilder.build(this);
 *     }
 * }
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveToString {

    /**
     * Se deve incluir campos herdados de superclasses.
     * 
     * @return true para incluir campos herdados
     */
    boolean includeInherited() default true;

    /**
     * Se deve incluir campos nulos na saída.
     * 
     * @return true para incluir campos nulos
     */
    boolean includeNulls() default false;

    /**
     * Campos a serem excluídos do toString().
     * 
     * @return nomes dos campos a excluir
     */
    String[] exclude() default {};

    /**
     * Se deve usar formato JSON na saída.
     * 
     * @return true para formato JSON
     */
    boolean jsonFormat() default false;
}
