package com.jairo.workflowtramites.service.motor;

import com.jairo.workflowtramites.dto.response.motor.DashboardPrioridadResponse;
import com.jairo.workflowtramites.dto.response.motor.MejorRutaResponse;
import com.jairo.workflowtramites.dto.response.motor.PaginaAnomaliasResponse;
import com.jairo.workflowtramites.dto.response.motor.RiesgoFlujoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

/**
 * Gateway al microservicio FastAPI del Motor Inteligente.
 *
 * <p>Cada llamada está envuelta en try/catch con fallback graceful: si el micro
 * está temporalmente caído o tarda demasiado, el sistema NO rompe — devuelve
 * un response con {@code disponible = false} y el front muestra
 * "motor temporalmente no disponible" en lugar de un error.
 *
 * <p>Este servicio es pura infraestructura: NO contiene lógica de negocio,
 * solo orquesta el HTTP al micro.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MotorGatewayService {

    private final RestClient motorRestClient;

    // --------- Capacidad 1: Mejor ruta por flujo ---------
    public MejorRutaResponse mejorRuta(String flujoId, String authHeader) {
        try {
            return motorRestClient.get()
                    .uri("/motor/flujo/{id}/mejor-ruta", flujoId)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .body(MejorRutaResponse.class);
        } catch (Exception e) {
            log.warn("[motor-gateway] mejor-ruta no disponible para flujo {}: {}", flujoId, e.getMessage());
            return MejorRutaResponse.noDisponible(flujoId);
        }
    }

    // --------- Capacidad 2: Riesgo (cuellos + demoras) ---------
    public RiesgoFlujoResponse riesgo(String flujoId, String authHeader) {
        try {
            return motorRestClient.get()
                    .uri("/motor/flujo/{id}/riesgo", flujoId)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .body(RiesgoFlujoResponse.class);
        } catch (Exception e) {
            log.warn("[motor-gateway] riesgo no disponible para flujo {}: {}", flujoId, e.getMessage());
            return RiesgoFlujoResponse.noDisponible(flujoId);
        }
    }

    // --------- Capacidad 3: Lista paginada de anomalías ---------
    public PaginaAnomaliasResponse anomalias(int page, int size, String severidad, String authHeader) {
        try {
            return motorRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/motor/anomalias")
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .queryParamIfPresent("severidad", Optional.ofNullable(severidad))
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .body(PaginaAnomaliasResponse.class);
        } catch (Exception e) {
            log.warn("[motor-gateway] anomalias no disponible (page={}, size={}, sev={}): {}",
                    page, size, severidad, e.getMessage());
            return PaginaAnomaliasResponse.vacia(page);
        }
    }

    // --------- Capacidad 3: Descartar anomalía como falso positivo ---------
    public void descartarAnomalia(String anomaliaId, String authHeader) {
        try {
            motorRestClient.post()
                    .uri("/motor/anomalia/{id}/descartar", anomaliaId)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            // Acción no crítica: si falla, solo logueamos. El admin puede reintentar.
            log.warn("[motor-gateway] descartar anomalía {} no disponible: {}", anomaliaId, e.getMessage());
        }
    }

    // --------- Capacidad 4: Prioridad agregada para el dashboard ---------
    public DashboardPrioridadResponse dashboardPrioridad(String authHeader) {
        try {
            return motorRestClient.get()
                    .uri("/motor/dashboard-prioridad")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .body(DashboardPrioridadResponse.class);
        } catch (Exception e) {
            log.warn("[motor-gateway] dashboard-prioridad no disponible: {}", e.getMessage());
            return DashboardPrioridadResponse.noDisponible();
        }
    }
}
