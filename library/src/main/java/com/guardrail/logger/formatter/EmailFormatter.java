package com.guardrail.logger.formatter;

import com.guardrail.logger.core.DataType;
import com.guardrail.logger.core.Formatter;

/**
 * Formatador específico para endereços de e-mail.
 * 
 * <p>Este formatador aplica mascaramento parcial ao e-mail, mantendo
 * parte do usuário e domínio visíveis para identificação.</p>
 * 
 * <p>Exemplo: "usuario@dominio.com" → "us***@dom***.com"</p>
 * 
 * @author Guardrail Logger Team
 * @since 1.0.0
 */
public class EmailFormatter implements Formatter {

    private final int visibleCharsUser;
    private final int visibleCharsDomain;
    private final char maskChar;

    /**
     * Cria um formatador de e-mail com configurações padrão.
     */
    public EmailFormatter() {
        this(2, 3, '*');
    }

    /**
     * Cria um formatador de e-mail customizado.
     *
     * @param visibleCharsUser caracteres visíveis no usuário
     * @param visibleCharsDomain caracteres visíveis no domínio
     * @param maskChar caractere de máscara
     */
    public EmailFormatter(int visibleCharsUser, int visibleCharsDomain, char maskChar) {
        this.visibleCharsUser = Math.max(1, visibleCharsUser);
        this.visibleCharsDomain = Math.max(1, visibleCharsDomain);
        this.maskChar = maskChar;
    }

    @Override
    public String format(String value) {
        if (!isValid(value)) {
            return String.valueOf(maskChar).repeat(3);
        }

        int atIndex = value.indexOf('@');
        String user = value.substring(0, atIndex);
        String domain = value.substring(atIndex + 1);

        StringBuilder result = new StringBuilder();

        // Mascara o usuário
        if (user.length() <= visibleCharsUser) {
            result.append(String.valueOf(maskChar).repeat(user.length()));
        } else {
            result.append(user, 0, visibleCharsUser);
            result.append(String.valueOf(maskChar).repeat(3));
        }

        result.append("@");

        // Mascara o domínio, preservando a extensão
        int lastDotIndex = domain.lastIndexOf('.');
        if (lastDotIndex > 0) {
            String domainName = domain.substring(0, lastDotIndex);
            String extension = domain.substring(lastDotIndex);

            if (domainName.length() <= visibleCharsDomain) {
                result.append(String.valueOf(maskChar).repeat(domainName.length()));
            } else {
                result.append(domainName, 0, visibleCharsDomain);
                result.append(String.valueOf(maskChar).repeat(3));
            }
            result.append(extension);
        } else {
            result.append(String.valueOf(maskChar).repeat(3));
        }

        return result.toString();
    }

    @Override
    public DataType getDataType() {
        return DataType.EMAIL;
    }

    @Override
    public String getName() {
        return "emailFormatter";
    }

    @Override
    public boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        int atIndex = value.indexOf('@');
        return atIndex > 0 && atIndex < value.length() - 1 && value.indexOf('@', atIndex + 1) == -1;
    }
}
