package com.claro.sap.rfcwrapper.exception;

/**
 * Excepción utilizada para notificar problemas con los datos utilizadados para autenticarse en algún recurso
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Constructor de la clase
     *
     * @param errorMessage mensaje de error
     */
    public AuthenticationException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructor de la clase
     *
     * @param errorMessage mensaje de error
     * @param err          excepción original
     */
    public AuthenticationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
