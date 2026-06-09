package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.request.TramiteRequest;
import com.jairo.workflowtramites.dto.response.DocumentoProducidoSlotResponse;
import com.jairo.workflowtramites.dto.response.FormularioTemplateResponse;
import com.jairo.workflowtramites.dto.response.TramiteDisponibleResponse;
import com.jairo.workflowtramites.dto.response.TramiteResponse;
import com.jairo.workflowtramites.service.SolicitudTramiteService;
import com.jairo.workflowtramites.service.TramiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
public class TramiteController {

    private final TramiteService tramiteService;
    private final SolicitudTramiteService solicitudTramiteService;

    @GetMapping
    public ResponseEntity<List<TramiteResponse>> listar() {
        return ResponseEntity.ok(tramiteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TramiteResponse> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(tramiteService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<TramiteResponse> crear(@RequestBody @Valid TramiteRequest request) {
        return ResponseEntity.status(201).body(tramiteService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TramiteResponse> actualizar(
            @PathVariable String id,
            @RequestBody @Valid TramiteRequest request) {
        return ResponseEntity.ok(tramiteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        tramiteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<TramiteDisponibleResponse>> listarDisponibles() {
        return ResponseEntity.ok(tramiteService.listarDisponibles());
    }

    @GetMapping("/{id}/formulario-solicitante")
    public ResponseEntity<FormularioTemplateResponse> obtenerFormularioSolicitante(@PathVariable String id) {
        return ResponseEntity.ok(tramiteService.obtenerFormularioSolicitante(id));
    }

    /**
     * Documentos que el solicitante debe subir al iniciar el tramite (el "kit").
     * Salen del nodo inicial del flujo activo. El front los muestra como slots al crear la solicitud.
     */
    @GetMapping("/{id}/documentos-kit")
    public ResponseEntity<List<DocumentoProducidoSlotResponse>> obtenerDocumentosKit(@PathVariable String id) {
        return ResponseEntity.ok(solicitudTramiteService.obtenerDocumentosKit(id));
    }
}
