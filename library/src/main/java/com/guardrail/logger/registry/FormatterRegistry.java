package com.guardrail.logger.registry;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registro centralizado de formatadores.
 * 
 * <p>Esta classe mantém um registro de todos os formatadores disponíveis,
 * permitindo registro e recuperação por nome ou tipo de dado.</p>
 * 
 * <p>O registro é thread-safe e pode ser utilizado em ambientes concorrentes.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public final class FormatterRegistry {

    private static final FormatterRegistry INSTANCE = new FormatterRegistry();

    private final Map<String, Formatter> formattersByName = new ConcurrentHashMap<>();
    private final Map<DataType, Formatter> formattersByType = new ConcurrentHashMap<>();

    private FormatterRegistry() {
        // Singleton
    }

    /**
     * Retorna a instância singleton do registro.
     *
     * @return instância do FormatterRegistry
     */
    public static FormatterRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Registra um formatador pelo nome.
     *
     * @param name nome do formatador
     * @param formatter instância do formatador
     */
    public void register(String name, Formatter formatter) {
        formattersByName.put(name.toLowerCase(), formatter);
        if (formatter.getDataType() != DataType.GENERIC) {
            formattersByType.put(formatter.getDataType(), formatter);
        }
    }

    /**
     * Registra um formatador pelo tipo de dado.
     *
     * @param dataType tipo de dado
     * @param formatter instância do formatador
     */
    public void register(DataType dataType, Formatter formatter) {
        formattersByType.put(dataType, formatter);
        formattersByName.put(formatter.getName().toLowerCase(), formatter);
    }

    /**
     * Busca um formatador pelo nome.
     *
     * @param name nome do formatador
     * @return Optional contendo o formatador, se encontrado
     */
    public Optional<Formatter> getByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(formattersByName.get(name.toLowerCase()));
    }

    /**
     * Busca um formatador pelo tipo de dado.
     *
     * @param dataType tipo de dado
     * @return Optional contendo o formatador, se encontrado
     */
    public Optional<Formatter> getByDataType(DataType dataType) {
        return Optional.ofNullable(formattersByType.get(dataType));
    }

    /**
     * Verifica se existe um formatador registrado para o nome informado.
     *
     * @param name nome do formatador
     * @return true se existe um formatador registrado
     */
    public boolean hasFormatter(String name) {
        return name != null && formattersByName.containsKey(name.toLowerCase());
    }

    /**
     * Verifica se existe um formatador registrado para o tipo de dado.
     *
     * @param dataType tipo de dado
     * @return true se existe um formatador registrado
     */
    public boolean hasFormatter(DataType dataType) {
        return formattersByType.containsKey(dataType);
    }

    /**
     * Remove um formatador pelo nome.
     *
     * @param name nome do formatador
     */
    public void unregister(String name) {
        Formatter removed = formattersByName.remove(name.toLowerCase());
        if (removed != null && removed.getDataType() != DataType.GENERIC) {
            formattersByType.remove(removed.getDataType());
        }
    }

    /**
     * Remove todos os formatadores registrados.
     */
    public void clear() {
        formattersByName.clear();
        formattersByType.clear();
    }

    /**
     * Retorna o número de formatadores registrados.
     *
     * @return número de formatadores
     */
    public int size() {
        return formattersByName.size();
    }
}
