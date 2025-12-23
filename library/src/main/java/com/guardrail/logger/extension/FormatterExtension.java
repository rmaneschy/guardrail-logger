package com.guardrail.logger.extension;

import com.guardrail.logger.core.Formatter;
import com.guardrail.logger.registry.FormatterRegistry;

/**
 * Interface para extensão de formatadores customizados.
 * 
 * <p>Implementações desta interface podem ser registradas automaticamente
 * pelo Spring Boot ou manualmente via código.</p>
 * 
 * <p>Exemplo de implementação:</p>
 * <pre>{@code
 * @Component
 * public class CustomCpfFormatter implements FormatterExtension {
 *     @Override
 *     public void register(FormatterRegistry registry) {
 *         registry.register("customCpf", value -> {
 *             // Lógica customizada de formatação
 *             return "***" + value.substring(3, 9) + "**";
 *         });
 *     }
 * }
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
@FunctionalInterface
public interface FormatterExtension {

    /**
     * Registra formatadores customizados no registro global.
     *
     * @param registry registro de formatadores
     */
    void register(FormatterRegistry registry);

    /**
     * Retorna a ordem de prioridade para registro.
     * Valores menores são registrados primeiro.
     *
     * @return ordem de prioridade (padrão: 100)
     */
    default int getOrder() {
        return 100;
    }
}
