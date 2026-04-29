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
        LocalDateTime ahora = LocalDateTime.now();

        List<SolicitudTramite> todas = solicitudRepo.findAll();
        int actualizadas = 0;

        for (SolicitudTramite s : todas) {
            LocalDateTime fechaCreacion = ahora
                    .minusDays(random.nextInt(dias))
                    .minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60));

            s.setFechaCreacion(fechaCreacion);

            LocalDateTime cursor = fechaCreacion;
            for (RespuestaDepartamento r : s.getRespuestasPorDepartamento()) {
                String nombreDepto = deptoIdANombre.get(r.getDepartamentoId());
                ConfigDepto cfg = configs.getOrDefault(nombreDepto, CONFIG_POR_DEFECTO);

                r.setFechaEntrada(cursor);

                if (r.getFechaAsignacion() != null) {
                    LocalDateTime asignacion = cursor.plusMinutes(
                            aleatorio(cfg.bandejaMinutosMin(), cfg.bandejaMinutosMax()));
                    if (asignacion.isAfter(ahora)) asignacion = ahora;
                    r.setFechaAsignacion(asignacion);
                    cursor = asignacion;
                }

                if (r.getFechaRespuesta() != null) {
                    LocalDateTime respuesta = cursor.plusMinutes(
                            aleatorio(cfg.trabajoMinutosMin(), cfg.trabajoMinutosMax()));
                    if (respuesta.isAfter(ahora)) respuesta = ahora;
                    r.setFechaRespuesta(respuesta);
                    cursor = respuesta;
                }
            }

            if (s.getFechaFinalizacion() != null) {
                s.setFechaFinalizacion(cursor);
            }

            solicitudRepo.save(s);
            actualizadas++;

            if (actualizadas % 500 == 0) {
                log.info("[RedistribuidorFechas] {} solicitudes reescritas", actualizadas);
            }
        }

        log.info("[RedistribuidorFechas] Total: {}", actualizadas);
    }

    private int aleatorio(int min, int max) {
        if (max <= min) return min;
        return min + random.nextInt(max - min);
    }
}
