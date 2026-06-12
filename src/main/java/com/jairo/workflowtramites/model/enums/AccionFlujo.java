package com.jairo.workflowtramites.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Catalogo CERRADO de acciones validas para las flechas de decision (salidas de exclusiveGateway)
 * y para los eventos de fin de un flujo. Reemplaza el "texto libre": el front las ofrece en un
 * dropdown (GET /api/catalogo/acciones-flujo) y el back valida que se usen estas, de modo que
 * mapearEstadoFinal, el seeder y los reportes SIEMPRE reconozcan los valores.
 *
 * El "valor" es lo que viaja como la variable de Camunda (${accion == 'valor'}).
 * El NOMBRE de la etiqueta lo elige el disenador; a DONDE va la flecha lo decide el diagrama (libre).
 */
public enum AccionFlujo {
    APROBADO("Aprobado", "aprobado", true),
    RECHAZADO("Rechazado", "rechazado", true),
    OBSERVADO("Observado", "observado", false),
    CORREGIDO("Corregido", "corregido", false),
    CANCELADO("Cancelado", "cancelado", true);

    private final String etiqueta;
    private final String valor;
    /** Si es true, puede ser el nombre de un evento de fin (define el estado final del tramite). */
    private final boolean esFinal;

    AccionFlujo(String etiqueta, String valor, boolean esFinal) {
        this.etiqueta = etiqueta;
        this.valor = valor;
        this.esFinal = esFinal;
    }

    public String getEtiqueta() { return etiqueta; }
    public String getValor() { return valor; }
    public boolean isEsFinal() { return esFinal; }

    /** Valores validos para CUALQUIER flecha de decision (salida de gateway). */
    public static Set<String> valoresValidos() {
        return Arrays.stream(values()).map(AccionFlujo::getValor).collect(Collectors.toSet());
    }

    /** Valores validos para un EVENTO DE FIN (los que definen un estado final del tramite). */
    public static Set<String> valoresFinales() {
        return Arrays.stream(values()).filter(AccionFlujo::isEsFinal)
                .map(AccionFlujo::getValor).collect(Collectors.toSet());
    }

    /** Etiqueta legible de un valor de accion (para mostrar en el historial). */
    public static String etiquetaDe(String valor) {
        if (valor == null) return null;
        if ("avanzar".equals(valor)) return "Continuar";   // flechas secuenciales (sin decision)
        return Arrays.stream(values())
                .filter(a -> a.valor.equals(valor))
                .map(AccionFlujo::getEtiqueta)
                .findFirst()
                .orElse(valor);   // fallback: el valor tal cual (ej: un name viejo)
    }
}
