package com.jairo.workflowtramites.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * Fija la zona horaria de la JVM al arrancar, en UN solo lugar (Filosofia "servidor en hora local").
 *
 * El back usa LocalDateTime.now() en todos lados (fechas, SLA, reportes, auditoria), y eso toma
 * la zona horaria de la JVM. En tu maquina la JVM usa tu zona local, pero los contenedores Docker
 * del servidor por defecto usan UTC -> sin esto, las fechas saldrian desfasadas (ej: +4h) en el
 * servidor. Forzandola en codigo (no en el entorno), local y EC2 usan SIEMPRE la misma zona,
 * sin depender del docker-compose, del TZ del servidor ni de la region de AWS.
 *
 * NOTA: esto asume que todos los usuarios estan en la misma zona (Bolivia). Si algun dia hay
 * usuarios en otro pais, habria que migrar a UTC + Instant (el front convierte por usuario).
 * Ajustar la zona en application.properties (app.timezone).
 */
@Slf4j
@Configuration
public class TimeZoneConfig {

    @Value("${app.timezone:America/La_Paz}")
    private String timezone;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
        log.info("Zona horaria de la aplicacion fijada en: {}", timezone);
    }
}
