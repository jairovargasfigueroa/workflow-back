package com.jairo.workflowtramites.seed.config;

/**
 * Config de "comportamiento" por depto que se usa al generar datos sintéticos.
 * Los rangos min/max controlan la aleatoriedad de la redistribución de fechas,
 * y las tasas controlan la distribución de acciones.
 *
 * Inyectar sesgos realistas aquí es lo que permite que el modelo ML aprenda
 * patrones útiles (ej. "Urbanismo es cuello de botella" porque tiene tiempos altos).
 */
public record ConfigDepto(
        int bandejaMinutosMin,
        int bandejaMinutosMax,
        int trabajoMinutosMin,
        int trabajoMinutosMax,
        double tasaRechazo,
        double tasaDevolucion
) {
}
