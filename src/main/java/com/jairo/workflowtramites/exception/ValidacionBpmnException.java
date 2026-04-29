package com.jairo.workflowtramites.exception;

import java.util.List;

public class ValidacionBpmnException extends RuntimeException {

    private final List<String> errores;

    public ValidacionBpmnException(List<String> errores) {
        super("El borrador tiene errores de validación");
        this.errores = errores;
    }

    public List<String> getErrores() {
        return errores;
    }
}
