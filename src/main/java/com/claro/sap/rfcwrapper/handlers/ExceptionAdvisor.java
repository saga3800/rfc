package com.claro.sap.rfcwrapper.handlers;

import com.claro.sap.rfcwrapper.exception.AuthenticationException;
import com.claro.sap.rfcwrapper.vo.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Mapea las excepciones no controladas a respuestas Http
 */
@ControllerAdvice
public class ExceptionAdvisor {

    /**
     * Trata las excepciones no controladas que se refieren a un problema de autenticación
     *
     * @param ex      excepción
     * @param request petición
     * @return respuesta http
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GenericResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        GenericResponse body = new GenericResponse();
        body.setMessage(ex.getMessage());
        body.setOrigen("/sap/invoke");
        body.setSuccess(false);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}
