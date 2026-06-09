package com.jairo.workflowtramites.service.sla;

import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.model.embeds.RespuestaDepartamento;
import com.jairo.workflowtramites.model.enums.EstadoSla;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import com.jairo.workflowtramites.repository.SolicitudTramiteRepository;
import com.jairo.workflowtramites.repository.TramiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Job que actualiza el estado SLA de las solicitudes activas cada 5 minutos.
 *
 * - Recalcula {@code estadoSla} de la solicitud según el consumo del SLA del trámite.
 * - Recalcula {@code estadoSlaNodo} de cada RespuestaDepartamento pendiente
 *   según el consumo del SLA del nodo.
 *
 * Solicitudes finalizadas (APROBADO/RECHAZADO/CANCELADO) se ignoran.
 * Solicitudes/respuestas sin SLA configurado (fechaLimite/fechaLimiteNodo null) se ignoran.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SlaMonitorJob {

    private static final int UMBRAL_ALERTA_DEFAULT = 70;
    private static final Set<EstadoTramite> ESTADOS_ACTIVOS =
            Set.of(EstadoTramite.PENDIENTE, EstadoTramite.EN_PROCESO);

    private final SolicitudTramiteRepository solicitudRepository;
    private final TramiteRepository tramiteRepository;

    @Scheduled(fixedRate = 5 * 60 * 1000L, initialDelay = 60 * 1000L)
    public void actualizarEstadosSla() {
        List<SolicitudTramite> activas = solicitudRepository.findAll().stream()
                .filter(s -> ESTADOS_ACTIVOS.contains(s.getEstado()))
                .toList();

        if (activas.isEmpty()) return;

        // Cache de trámites para no consultar uno por uno (los umbrales viven en el Tramite)
        Map<String, Tramite> tramitesCache = new HashMap<>();

        LocalDateTime ahora = LocalDateTime.now();
        int actualizadas = 0;

        for (SolicitudTramite s : activas) {
            boolean cambio = false;

            // 1. SLA del trámite
            if (s.getFechaCreacion() != null && s.getFechaLimite() != null) {
                Tramite t = tramitesCache.computeIfAbsent(s.getTramiteId(),
                        id -> tramiteRepository.findById(id).orElse(null));

                int umbral = (t != null && t.getUmbralAlertaPorcentaje() != null)
                        ? t.getUmbralAlertaPorcentaje()
                        : UMBRAL_ALERTA_DEFAULT;

                Integer plazoMaximo = t != null ? t.getPlazoMaximoHoras() : null;

                EstadoSla nuevoEstado = calcularEstadoSla(
                        s.getFechaCreacion(), s.getFechaLimite(), plazoMaximo, ahora, umbral);

                if (nuevoEstado != s.getEstadoSla()) {
                    s.setEstadoSla(nuevoEstado);
                    cambio = true;
                }
            }

            // 2. SLA por nodo (RespuestaDepartamento pendientes)
            if (s.getRespuestasPorDepartamento() != null) {
                for (RespuestaDepartamento r : s.getRespuestasPorDepartamento()) {
                    if (r.getFechaRespuesta() != null) continue;       // ya respondida
                    if (r.getFechaEntrada() == null) continue;          // sin fecha de entrada
                    if (r.getFechaLimiteNodo() == null) continue;       // sin SLA por nodo

                    EstadoSla nuevoEstadoNodo = calcularEstadoSla(
                            r.getFechaEntrada(), r.getFechaLimiteNodo(), null, ahora, UMBRAL_ALERTA_DEFAULT);

                    if (nuevoEstadoNodo != r.getEstadoSlaNodo()) {
                        r.setEstadoSlaNodo(nuevoEstadoNodo);
                        cambio = true;
                    }
                }
            }

            if (cambio) {
                solicitudRepository.save(s);
                actualizadas++;
            }
        }

        if (actualizadas > 0) {
            log.info("[SlaMonitor] estados SLA actualizados en {} solicitud(es)", actualizadas);
        }
    }

    /**
     * Calcula el estado SLA a partir del consumo de tiempo entre fechaInicio y fechaLimite.
     *
     * - VERDE: consumido < umbralAlerta%
     * - AMARILLO: umbralAlerta% <= consumido < 100%
     * - ROJO: 100% <= consumido < plazoMaximo (o sin plazoMaximo)
     * - VENCIDO: ahora > plazoMaximo (si hay plazoMaximoHoras)
     */
    private EstadoSla calcularEstadoSla(LocalDateTime fechaInicio,
                                        LocalDateTime fechaLimite,
                                        Integer plazoMaximoHoras,
                                        LocalDateTime ahora,
                                        int umbralAlerta) {
        long totalMinutos = Duration.between(fechaInicio, fechaLimite).toMinutes();
        if (totalMinutos <= 0) return EstadoSla.VENCIDO;

        long consumidoMinutos = Duration.between(fechaInicio, ahora).toMinutes();
        if (consumidoMinutos < 0) return EstadoSla.VERDE;

        // Si hay plazo máximo, verificar vencimiento absoluto
        if (plazoMaximoHoras != null && plazoMaximoHoras > 0) {
            LocalDateTime fechaMaxima = fechaInicio.plusHours(plazoMaximoHoras);
            if (ahora.isAfter(fechaMaxima)) return EstadoSla.VENCIDO;
        }

        double porcentaje = (consumidoMinutos * 100.0) / totalMinutos;

        if (porcentaje >= 100.0) return EstadoSla.ROJO;
        if (porcentaje >= umbralAlerta) return EstadoSla.AMARILLO;
        return EstadoSla.VERDE;
    }
}
