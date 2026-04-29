package com.jairo.workflowtramites.util;

/**
 * Utilidad para generar el procesoKey de un FlujoTrabajo a partir de su nombre.
 *
 * El procesoKey es el identificador del proceso BPMN en Camunda. Debe ser:
 *  - Estable (no cambia al renombrar el flujo).
 *  - Válido como id BPMN (sin espacios, sin acentos, sin caracteres especiales).
 *  - Único por flujo.
 *
 * Centralizar la generación aquí evita divergencias entre el service y el seeder.
 */
public final class ProcesoKeyUtil {

    private ProcesoKeyUtil() {}

    /** Genera el procesoKey a partir del nombre del flujo. */
    public static String generar(String nombre) {
        return "flujo-" + normalizar(nombre);
    }

    /** Convierte un texto a slug: minúsculas, sin acentos, separado por guiones. */
    public static String normalizar(String texto) {
        if (texto == null) return "";
        return texto.toLowerCase()
                .replaceAll("[áàäâ]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöô]", "o")
                .replaceAll("[úùüû]", "u")
                .replaceAll("ñ", "n")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
