package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.response.motor.DashboardPrioridadResponse;
import com.jairo.workflowtramites.dto.response.motor.MejorRutaResponse;
import com.jairo.workflowtramites.dto.response.motor.PaginaAnomaliasResponse;
import com.jairo.workflowtramites.dto.response.motor.RiesgoFlujoResponse;
import com.jairo.workflowtramites.model.enums.Rol;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import com.jairo.workflowtramites.service.motor.MotorGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST del Motor Inteligente de Enrutamiento y Análisis de Riesgo.
 *
 * <p>Cumple las 4 capacidades pedidas por el docente (todas usando deep learning
 * en el micro Python):
 * <ul>
 *   <li>1. Predecir mejor ruta para cada política de negocio</li>
 *   <li>2. Predecir riesgo de demoras o cuellos de botella</li>
 *   <li>3. Detectar anomalías</li>
 *   <li>4. Identificar prioridad en la asignación de recursos y trámites</li>
 * </ul>
 *
 * <p>Spring solo orquesta: la lógica de ML vive en el micro FastAPI.
 * Si el micro está caído, el gateway devuelve respuestas con
 * {@code disponible = false} para que el front degrade elegantemente.
 */
@RestController
@RequestMapping("/api/motor")
@RequiredArgsConstructor
public class MotorController {

    private final MotorGatewayService motorGateway;

    // --------- Capacidad 1: Predecir mejor ruta por flujo ---------
    @GetMapping("/flujo/{flujoId}/mejor-ruta")
    public MejorRutaResponse mejorRuta(@PathVariable String flujoId,
                                       @RequestHeader("Authorization") String authHeader,
                                       @AuthenticationPrincipal AuthenticatedUser user) {
        autorizarAdminOFuncionario(user);
        return motorGateway.mejorRuta(flujoId, authHeader);
    }

    // --------- Capacidad 2: Predecir riesgo de demoras/cuellos por flujo ---------
    @GetMapping("/flujo/{flujoId}/riesgo")
    public RiesgoFlujoResponse riesgo(@PathVariable String flujoId,
                                      @RequestHeader("Authorization") String authHeader,
                                      @AuthenticationPrincipal AuthenticatedUser user) {
        autorizarAdminOFuncionario(user);
        return motorGateway.riesgo(flujoId, authHeader);
    }

    // --------- Capacidad 3: Listar anomalías detectadas ---------
    @GetMapping("/anomalias")
    public PaginaAnomaliasResponse anomalias(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(required = false) String severidad,
                                             @RequestHeader("Authorization") String authHeader,
                                             @AuthenticationPrincipal AuthenticatedUser user) {
        autorizarAdmin(user);
        return motorGateway.anomalias(page, size, severidad, authHeader);
    }

    // --------- Capacidad 3: Marcar anomalía como falso positivo ---------
    @PostMapping("/anomalia/{id}/descartar")
    public ResponseEntity<Void> descartarAnomalia(@PathVariable String id,
                                                  @RequestHeader("Authorization") String authHeader,
                                                  @AuthenticationPrincipal AuthenticatedUser user) {
        autorizarAdmin(user);
        motorGateway.descartarAnomalia(id, authHeader);
        return ResponseEntity.noContent().build();
    }

    // --------- Capacidad 4: Prioridad agregada para Dashboard del admin ---------
    @GetMapping("/dashboard-prioridad")
    public DashboardPrioridadResponse dashboardPrioridad(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizarAdmin(user);
        return motorGateway.dashboardPrioridad(authHeader);
    }

    // ================== Autorización ==================

    /**
     * Solo ADMIN: para vistas de gestión (anomalías + prioridad agregada).
     */
    private void autorizarAdmin(AuthenticatedUser user) {
        if (user == null) {
            throw new AccessDeniedException("Autenticación requerida");
        }
        if (user.getRol() != Rol.ADMIN) {
            throw new AccessDeniedException("Solo administradores pueden acceder a esta sección del motor");
        }
    }

    /**
     * ADMIN o FUNCIONARIO: para análisis por flujo (cualquiera puede consultar
     * predicciones de un flujo en el contexto de su trabajo).
     */
    private void autorizarAdminOFuncionario(AuthenticatedUser user) {
        if (user == null) {
            throw new AccessDeniedException("Autenticación requerida");
        }
        if (user.getRol() == Rol.SOLICITANTE) {
            throw new AccessDeniedException("Esta sección del motor no está disponible para solicitantes");
        }
        if (user.getRol() == Rol.FUNCIONARIO && user.getDepartamentoId() == null) {
            throw new AccessDeniedException("Funcionario sin departamento asignado");
        }
    }
}
