package com.claro.sap.rfcwrapper.validation;

import com.claro.sap.rfcwrapper.constans.Origin;
import com.claro.sap.rfcwrapper.exception.AuthenticationException;
import com.claro.sap.rfcwrapper.rfc.RemoteFunctionTemplate;
import org.apache.logging.log4j.util.Strings;

/**
 * Clase que contiene métodos que validan la estructura de una petición a sap antes de ser enviada
 */
public final class PreConditions {
    private PreConditions() {
    }

    /**
     * Ejecuta las validaciones previas sobre los datos recibidos
     *
     * @param template datos
     */
    public static void execute(RemoteFunctionTemplate template) {
        if (!Origin.INTERNAL.equals(template.getOrigin())) {
            if (template.getCredentials() != null) {
                String user = template.getCredentials().get("user");
                String password = template.getCredentials().get("password");
                if (!Strings.isNotEmpty(user) || !Strings.isNotBlank(user) || !Strings.isNotEmpty(password) || !Strings.isNotBlank(password))
                    throw new AuthenticationException("Username and password are required");
            } else {
                throw new AuthenticationException("Username and password are required");
            }
        }
    }
}
