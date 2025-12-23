package com.guardrail.logger.config;

import com.guardrail.logger.core.DataType;

import java.util.Objects;

/**
 * Representa a configuração de um campo sensível a ser ofuscado.
 * 
 * <p>Esta classe permite configurar como cada campo sensível deve ser tratado,
 * incluindo o nome do campo, tipo de dado, padrão regex customizado e
 * formatador específico.</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class SensitiveField {

    private String name;
    private DataType dataType;
    private String customPattern;
    private String formatterName;
    private boolean caseSensitive;
    private int visibleCharsStart;
    private int visibleCharsEnd;

    public SensitiveField() {
        this.dataType = DataType.GENERIC;
        this.caseSensitive = false;
        this.visibleCharsStart = 0;
        this.visibleCharsEnd = 0;
    }

    public SensitiveField(String name) {
        this();
        this.name = name;
    }

    public SensitiveField(String name, DataType dataType) {
        this(name);
        this.dataType = dataType;
    }

    /**
     * Builder estático para criação fluente de SensitiveField.
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getCustomPattern() {
        return customPattern;
    }

    public void setCustomPattern(String customPattern) {
        this.customPattern = customPattern;
    }

    public String getFormatterName() {
        return formatterName;
    }

    public void setFormatterName(String formatterName) {
        this.formatterName = formatterName;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public int getVisibleCharsStart() {
        return visibleCharsStart;
    }

    public void setVisibleCharsStart(int visibleCharsStart) {
        this.visibleCharsStart = visibleCharsStart;
    }

    public int getVisibleCharsEnd() {
        return visibleCharsEnd;
    }

    public void setVisibleCharsEnd(int visibleCharsEnd) {
        this.visibleCharsEnd = visibleCharsEnd;
    }

    /**
     * Retorna o padrão regex efetivo para este campo.
     * Se um padrão customizado foi definido, ele é retornado.
     * Caso contrário, retorna o padrão padrão do DataType.
     *
     * @return o padrão regex efetivo
     */
    public String getEffectivePattern() {
        if (customPattern != null && !customPattern.isBlank()) {
            return customPattern;
        }
        return dataType != null ? dataType.getDefaultPattern() : DataType.GENERIC.getDefaultPattern();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensitiveField that = (SensitiveField) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "SensitiveField{" +
                "name='" + name + '\'' +
                ", dataType=" + dataType +
                ", caseSensitive=" + caseSensitive +
                '}';
    }

    /**
     * Builder para criação fluente de SensitiveField.
     */
    public static class Builder {
        private final SensitiveField field = new SensitiveField();

        public Builder name(String name) {
            field.setName(name);
            return this;
        }

        public Builder dataType(DataType dataType) {
            field.setDataType(dataType);
            return this;
        }

        public Builder customPattern(String pattern) {
            field.setCustomPattern(pattern);
            return this;
        }

        public Builder formatterName(String formatterName) {
            field.setFormatterName(formatterName);
            return this;
        }

        public Builder caseSensitive(boolean caseSensitive) {
            field.setCaseSensitive(caseSensitive);
            return this;
        }

        public Builder visibleCharsStart(int chars) {
            field.setVisibleCharsStart(chars);
            return this;
        }

        public Builder visibleCharsEnd(int chars) {
            field.setVisibleCharsEnd(chars);
            return this;
        }

        public SensitiveField build() {
            return field;
        }
    }
}
