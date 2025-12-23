package com.guardrail.logger.registry;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Obfuscator;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro centralizado de ofuscadores.
 * 
 * <p>Esta classe mantém um registro de todos os ofuscadores disponíveis,
 * permitindo registro e recuperação por nome ou tipo de dado.</p>
 * 
 * <p>O registro é thread-safe e pode ser utilizado em ambientes concorrentes.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public final class ObfuscatorRegistry {

    private static final ObfuscatorRegistry INSTANCE = new ObfuscatorRegistry();

    private final Map<String, Obfuscator> obfuscatorsByName = new ConcurrentHashMap<>();
    private final Map<DataType, Obfuscator> obfuscatorsByType = new ConcurrentHashMap<>();
    private Obfuscator defaultObfuscator;

    private ObfuscatorRegistry() {
        // Singleton
    }

    /**
     * Retorna a instância singleton do registro.
     *
     * @return instância do ObfuscatorRegistry
     */
    public static ObfuscatorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Registra um ofuscador pelo nome.
     *
     * @param name nome do ofuscador
     * @param obfuscator instância do ofuscador
     */
    public void register(String name, Obfuscator obfuscator) {
        obfuscatorsByName.put(name.toLowerCase(), obfuscator);
    }

    /**
     * Registra um ofuscador pelo tipo de dado.
     *
     * @param dataType tipo de dado
     * @param obfuscator instância do ofuscador
     */
    public void register(DataType dataType, Obfuscator obfuscator) {
        obfuscatorsByType.put(dataType, obfuscator);
    }

    /**
     * Define o ofuscador padrão.
     *
     * @param obfuscator ofuscador padrão
     */
    public void setDefaultObfuscator(Obfuscator obfuscator) {
        this.defaultObfuscator = obfuscator;
    }

    /**
     * Retorna o ofuscador padrão.
     *
     * @return ofuscador padrão
     */
    public Obfuscator getDefaultObfuscator() {
        return defaultObfuscator;
    }

    /**
     * Busca um ofuscador pelo nome.
     *
     * @param name nome do ofuscador
     * @return Optional contendo o ofuscador, se encontrado
     */
    public Optional<Obfuscator> getByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(obfuscatorsByName.get(name.toLowerCase()));
    }

    /**
     * Busca um ofuscador pelo tipo de dado.
     *
     * @param dataType tipo de dado
     * @return Optional contendo o ofuscador, se encontrado
     */
    public Optional<Obfuscator> getByDataType(DataType dataType) {
        return Optional.ofNullable(obfuscatorsByType.get(dataType));
    }

    /**
     * Retorna o ofuscador mais apropriado para o tipo de dado.
     * Se não houver um ofuscador específico, retorna o padrão.
     *
     * @param dataType tipo de dado
     * @return ofuscador apropriado ou padrão
     */
    public Obfuscator getObfuscatorFor(DataType dataType) {
        return obfuscatorsByType.getOrDefault(dataType, defaultObfuscator);
    }

    /**
     * Verifica se existe um ofuscador registrado para o nome informado.
     *
     * @param name nome do ofuscador
     * @return true se existe um ofuscador registrado
     */
    public boolean hasObfuscator(String name) {
        return name != null && obfuscatorsByName.containsKey(name.toLowerCase());
    }

    /**
     * Verifica se existe um ofuscador registrado para o tipo de dado.
     *
     * @param dataType tipo de dado
     * @return true se existe um ofuscador registrado
     */
    public boolean hasObfuscator(DataType dataType) {
        return obfuscatorsByType.containsKey(dataType);
    }

    /**
     * Remove um ofuscador pelo nome.
     *
     * @param name nome do ofuscador
     */
    public void unregister(String name) {
        obfuscatorsByName.remove(name.toLowerCase());
    }

    /**
     * Remove um ofuscador pelo tipo de dado.
     *
     * @param dataType tipo de dado
     */
    public void unregister(DataType dataType) {
        obfuscatorsByType.remove(dataType);
    }

    /**
     * Remove todos os ofuscadores registrados.
     */
    public void clear() {
        obfuscatorsByName.clear();
        obfuscatorsByType.clear();
        defaultObfuscator = null;
    }

    /**
     * Retorna o número de ofuscadores registrados por nome.
     *
     * @return número de ofuscadores
     */
    public int size() {
        return obfuscatorsByName.size();
    }
}
