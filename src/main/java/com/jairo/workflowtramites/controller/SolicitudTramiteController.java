package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.request.LiberarTareaRequest;
import com.jairo.workflowtramites.dto.request.RespuestaDepartamentoRequest;
import com.jairo.workflowtramites.dto.request.SolicitudTramiteRequest;
import com.jairo.workflowtramites.dto.request.TomarTareaRequest;
import com.jairo.workflowtramites.dto.response.SolicitudTramiteResumen;
import com.jairo.workflowtramites.dto.response.SolicitudTramiteResponse;
import com.jairo.workflowtramites.dto.response.TareaActivaResponse;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import com.jairo.workflowtramites.service.SolicitudTramiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudTramiteController {

    private final SolicitudTramiteService solicitudTramiteService;

    // -------- Endpoints abiertos / admin --------

    @GetMapping
    public ResponseEntity<List<SolicitudTramiteResumen>> listar() {
        return ResponseEntity.ok(solicitudTramiteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudTramiteResponse> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(solicitudTramiteService.obtenerPorId(id));
    }

    @GetMapping("/tramite/{tramiteId}")
    public ResponseEntity<List<SolicitudTramiteResumen>> listarPorTramite(
            @PathVariable String tramiteId) {
        return ResponseEntity.ok(solicitudTramiteService.listarPorTramite(tramiteId));
    }

    @GetMapping("/solicitante/{solicitanteId}")
    public ResponseEntity<List<SolicitudTramiteResumen>> listarPorSolicitante(
            @PathVariable String solicitanteId) {
        return ResponseEntity.ok(solicitudTramiteService.listarPorSolicitante(solicitanteId));
    }

    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<SolicitudTramiteResumen>> listarPorDepartamento(
            @PathVariable String departamentoId) {
        return ResponseEntity.ok(solicitudTramiteService.listarPorDepartamento(departamentoId));
    }

    @GetMapping("/departamento/{departamentoId}/estado/{estado}")
    public ResponseEntity<List<SolicitudTramiteResumen>> listarPorDepartamentoYEstado(
            @PathVariable String departamentoId,
            @PathVariable EstadoTramite estado) {
        return ResponseEntity.ok(solicitudTramiteService.listarPorDepartamentoYEstado(departamentoId, estado));
    }

    @GetMapping("/{id}/tarea-activa")
    public ResponseEntity<List<TareaActivaResponse>> obtenerTareasActivas(
            @PathVariable String id,
            @RequestParam String departamentoId) {
        return ResponseEntity.ok(solicitudTramiteService.obtenerTareasActivas(id, departamentoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudTramiteResponse> actualizar(
            @PathVariable String id,
            @RequestBody @Valid SolicitudTramiteRequest request) {
        return ResponseEntity.ok(solicitudTramiteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        solicitudTramiteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // -------- Endpoints personales (requieren usuario autenticado) --------

    @PostMapping
    public ResponseEntity<SolicitudTramiteResponse> crear(
            @RequestBody @Valid SolicitudTramiteRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        // AuthenticatedUser principal = requireUser(user);
        AuthenticatedUser principal = user;
        return ResponseEntity.status(201)
                .body(solicitudTramiteService.crear(request, principal.getId()));
    }

    @GetMapping("/mis-solicitudes")
    public ResponseEntity<List<SolicitudTramiteResumen>> misSolicitudes(
            @AuthenticationPrincipal AuthenticatedUser user) {
        // AuthenticatedUser principal = requireUser(user);
        AuthenticatedUser principal = user;
        return ResponseEntity.ok(solicitudTramiteService.listarPorSolicitante(principal.getId()));
    }

    @GetMapping("/mi-departamento/pendientes")
    public ResponseEntity<List<SolicitudTramiteResumen>> bandejaSinAsignar(
            @AuthenticationPrincipal AuthenticatedUser user) {
        // AuthenticatedUser principal = requireUser(user);
        AuthenticatedUser principal = user;
        return ResponseEntity.ok(
                solicitudTramiteService.listarPendientesSinAsignar(principal.getDepartamentoId()));
    }

    @GetMapping("/mi-departamento/mis-tareas")
    public ResponseEntity<List<SolicitudTramiteResumen>> misTareas(
            @AuthenticationPrincipal AuthenticatedUser user) {
        // AuthenticatedUser principal = requireUser(user);
        AuthenticatedUser principal = user;
        return ResponseEntity.ok(solicitudTramiteService.listarMisTareas(principal.getId()));
    }

    @GetMapping("/mi-departamento/historial")
    public ResponseEntity<List<SolicitudTramiteResumen>> historialDepartamento(
            @AuthenticationPrincipal AuthenticatedUser user) {
        // AuthenticatedUser principal = requireUser(user);
        AuthenticatedUser principal = user;
        return ResponseEntity.ok(
                solicitudTramiteService.listarHistorialDepartamento(principal.getDepartamentoId()));
    }

    @PostMapping("/{id}/tomar")
    public ResponseEntity<SolicitudTramiteResponse> tomarTarea(
            @PathVariable String id,
            @RequestBody @Valid TomarTareaRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        // AuthenticatedUser principal = requireUser(user);
        AuthenticatedUser principal = user;
        return ResponseEntity.ok(
                solicitudTramiteService.tomarTarea(
                        id, request.getElementId(), principal.getId(), principal.getDepartamentoId()));
    }

    @PostMapping("/{id}/liberar")
    public ResponseEntity<SolicitudTramiteResponse> liberarTarea(
            @PathVariable String id,
            @RequestBody @Valid LiberarTareaRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        // AuthenticatedUser principal = requireUser(user);
        AuthenticatedUser principal = user;
        return ResponseEntity.ok(
                solicitudTramiteService.liberarTarea(id, request.getElementId(), principal.getId()));
    }

    @PostMapping("/{id}/respuesta-departamento")
    public ResponseEntity<SolicitudTramiteResponse> responderDepartamento(
            @PathVariable String id,
            @RequestBody @Valid RespuestaDepartamentoRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        // AuthenticatedUser principal = requireUser(user);
        AuthenticatedUser principal = user;
        return ResponseEntity.ok(
                solicitudTramiteService.responderDepartamento(id, request, principal.getId()));
    }

    // -------- helpers --------

    private AuthenticatedUser requireUser(AuthenticatedUser user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Requiere autenticación");
        }
        return user;
    }
}
