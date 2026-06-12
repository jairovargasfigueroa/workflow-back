package com.jairo.workflowtramites.seed.seeders;

import com.jairo.workflowtramites.model.Departamento;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.embeds.RespuestaDepartamento;
import com.jairo.workflowtramites.repository.DepartamentoRepository;
import com.jairo.workflowtramites.repository.SolicitudTramiteRepository;
import com.jairo.workflowtramites.seed.config.ConfigDepto;
import com.jairo.workflowtramites.seed.config.SeedConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Reescribe fechas de solicitudes y sus respuestas para distribuirlas en los
 * últimos N días con duraciones coherentes por depto (sacadas de SeedConfig).
 *
 * Se corre al final del SolicitudesSeeder, cuando ya están todas las solicitudes
 * generadas con timestamps de "ahora".
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedistribuidorFechas {

    private static final ConfigDepto CONFIG_POR_DEFECTO =
            new ConfigDepto(30, 24 * 60, 60, 12 * 60, 0.10, 0.05);

    private final SolicitudTramiteRepository solicitudRepo;
    private final DepartamentoRepository departamentoRepo;
    private final SeedConfig seedConfig;
    private final Random random = new Random();

    public void redistribuir() {
        Map<String, String> deptoIdANombre = new HashMap<>();
        for (Departamento d : departamentoRepo.findAll()) {
            deptoIdANombre.put(d.getId(), d.getNombre());
        }

        Map<String, ConfigDepto> configs = seedConfig.getConfigPorDepto();
        int dias = Math.max(1, seedConfig.getDistribuirUltimosDias());
        double porcentajeAnomalias = seedConfig.getPorcentajeAnomalias();
        int factorMin = seedConfig.getFactorAnomaliaLentaMin();
        int factorMax = seedConfig.getFactorAnomaliaLentaMax();
        LocalDateTime ahora = LocalDateTime.now();

        List<SolicitudTramite> todas = solicitudRepo.findAll();
        int actualizadas = 0, lentas = 0, rapidas = 0;

        for (SolicitudTramite s : todas) {
            // ¿Esta solicitud es ANÓMALA? (para que el motor detecte outliers). NO marcamos
            // ningún flag "esAnomala" en la entidad: el escenario está en los TIEMPOS, el micro
            // (deep learning no supervisado) los descubre solo.
            boolean esAnomala = random.nextDouble() < porcentajeAnomalias;
            boolean esLenta   = esAnomala && random.nextBoolean();  // cuello extremo en un nodo
            boolean esRapida  = esAnomala && !esLenta;               // procesada en tiempo mínimo

            // Las LENTAS arrancan más atrás para que la demora extrema "quepa" antes de ahora.
            LocalDateTime fechaCreacion = esLenta
                    ? ahora.minusDays(dias / 2 + random.nextInt(Math.max(1, dias / 2)))
                           .minusHours(random.nextInt(24))
                    : ahora.minusDays(random.nextInt(dias))
                           .minusHours(random.nextInt(24))
                           .minusMinutes(random.nextInt(60));

            s.setFechaCreacion(fechaCreacion);

            // En una solicitud lenta, UN nodo al azar concentra la demora extrema (el cuello).
            List<RespuestaDepartamento> respuestas = s.getRespuestasPorDepartamento();
            int nodoCuello = (esLenta && !respuestas.isEmpty()) ? random.nextInt(respuestas.size()) : -1;

            LocalDateTime cursor = fechaCreacion;
            int idx = 0;
            for (RespuestaDepartamento r : respuestas) {
                String nombreDepto = deptoIdANombre.get(r.getDepartamentoId());
                ConfigDepto cfg = configs.getOrDefault(nombreDepto, CONFIG_POR_DEFECTO);

                r.setFechaEntrada(cursor);

                if (r.getFechaAsignacion() != null) {
                    int bandeja = esRapida ? cfg.bandejaMinutosMin()
                            : aleatorio(cfg.bandejaMinutosMin(), cfg.bandejaMinutosMax());
                    LocalDateTime asignacion = cursor.plusMinutes(bandeja);
                    if (asignacion.isAfter(ahora)) asignacion = ahora;
                    r.setFechaAsignacion(asignacion);
                    cursor = asignacion;
                }

                if (r.getFechaRespuesta() != null) {
                    int trabajo = esRapida ? cfg.trabajoMinutosMin()
                            : aleatorio(cfg.trabajoMinutosMin(), cfg.trabajoMinutosMax());
                    // El nodo cuello de una solicitud lenta tarda factorMin..factorMax veces lo normal.
                    if (idx == nodoCuello) trabajo *= aleatorio(factorMin, factorMax + 1);
                    LocalDateTime respuesta = cursor.plusMinutes(trabajo);
                    if (respuesta.isAfter(ahora)) respuesta = ahora;
                    r.setFechaRespuesta(respuesta);
                    cursor = respuesta;
                }
                idx++;
            }

            if (s.getFechaFinalizacion() != null) {
                s.setFechaFinalizacion(cursor);
            }

            solicitudRepo.save(s);
            actualizadas++;
            if (esLenta) lentas++;
            if (esRapida) rapidas++;

            if (actualizadas % 500 == 0) {
                log.info("[RedistribuidorFechas] {} solicitudes reescritas", actualizadas);
            }
        }

        log.info("[RedistribuidorFechas] Total: {} (anomalías: {} lentas / cuello, {} rápidas)",
                actualizadas, lentas, rapidas);
    }

    private int aleatorio(int min, int max) {
        if (max <= min) return min;
        return min + random.nextInt(max - min);
    }
}
