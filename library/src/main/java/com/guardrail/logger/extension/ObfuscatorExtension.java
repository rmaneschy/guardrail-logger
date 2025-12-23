package com.guardrail.logger.extension;

import com.guardrail.logger.registry.ObfuscatorRegistry;

/**
 * Interface para extensão de ofuscadores customizados.
 * 
 * <p>Implementações desta interface podem ser registradas automaticamente
 * pelo Spring Boot ou manualmente via código.</p>
 * 
 * <p>Exemplo de implementação:</p>
 * <pre>{@code
 * @Component
 * public class CustomObfuscatorExtension implements ObfuscatorExtension {
 *     @Override
 *     public void register(ObfuscatorRegistry registry) {
 *         registry.register("hash", value -> {
 *             // Retorna hash do valor ao invés de máscara
 *             return DigestUtils.sha256Hex(value).substring(0, 8);
 *         });
 *     }
 * }
 * }</pre>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
@FunctionalInterface
public interface ObfuscatorExtension {

    /**
     * Registra ofuscadores customizados no registro global.
     *
     * @param registry registro de ofuscadores
     */
    void register(ObfuscatorRegistry registry);

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
