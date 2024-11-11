package com.connect.sap.rfcwrapper.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Estructura general utilizada por el servicio para entregar una respuesta
 */
@Data
public class GenericResponse implements Serializable {

    private static final long serialVersionUID = -7868182870673334153L;

    private boolean success;
    private String origen;
    private String message;
}
