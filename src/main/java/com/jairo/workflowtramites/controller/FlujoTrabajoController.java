package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.request.CambiarEstadoFlujoRequest;
import com.jairo.workflowtramites.dto.request.CopiarVersionRequest;
import com.jairo.workflowtramites.dto.request.DeployRequest;
import com.jairo.workflowtramites.dto.request.FlujoTrabajoRequest;
import com.jairo.workflowtramites.dto.request.GuardarBorradorRequest;
import com.jairo.workflowtramites.dto.request.PublicarFlujoRequest;
import com.jairo.workflowtramites.dto.response.FlujoTrabajoResponse;
import com.jairo.workflowtramites.dto.response.VersionFlujoDetalleResponse;
import com.jairo.workflowtramites.dto.response.VersionFlujoResumen;
import com.jairo.workflowtramites.dto.response.XmlResponse;
import com.jairo.workflowtramites.exception.ValidacionBpmnException;
import com.jairo.workflowtramites.service.FlujoTrabajoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flujos-trabajo")
@RequiredArgsConstructor
public class FlujoTrabajoController {

    private final FlujoTrabajoService flujoTrabajoService;

    @GetMapping
    public ResponseEntity<List<FlujoTrabajoResponse>> listar() {
        return ResponseEntity.ok(flujoTrabajoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlujoTrabajoResponse> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(flujoTrabajoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<FlujoTrabajoResponse> crear(@RequestBody @Valid FlujoTrabajoRequest request) {
        return ResponseEntity.status(201).body(flujoTrabajoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlujoTrabajoResponse> actualizar(
            @PathVariable String id,
            @RequestBody @Valid FlujoTrabajoRequest request) {
        return ResponseEntity.ok(flujoTrabajoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        flujoTrabajoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/borrador")
    public ResponseEntity<FlujoTrabajoResponse> guardarBorrador(
            @PathVariable String id,
            @RequestBody @Valid GuardarBorradorRequest request) {
        return ResponseEntity.ok(flujoTrabajoService.guardarBorrador(id, request.getXml()));
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<?> publicar(
            @PathVariable String id,
            @RequestBody PublicarFlujoRequest request) {
        try {
            return ResponseEntity.ok(flujoTrabajoService.publicar(id));
        } catch (ValidacionBpmnException e) {
            return ResponseEntity.unprocessableEntity().body(Map.of("errores", e.getErrores()));
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<FlujoTrabajoResponse> cambiarEstado(
            @PathVariable String id,
            @RequestBody @Valid CambiarEstadoFlujoRequest request) {
        return ResponseEntity.ok(flujoTrabajoService.cambiarEstado(id, request.getEstado()));
    }

    @DeleteMapping("/{id}/borrador")
    public ResponseEntity<FlujoTrabajoResponse> descartarBorrador(@PathVariable String id) {
        return ResponseEntity.ok(flujoTrabajoService.descartarBorrador(id));
    }

    @PostMapping("/{id}/copiar-version-como-borrador")
    public ResponseEntity<FlujoTrabajoResponse> copiarVersionComoBorrador(
            @PathVariable String id,
            @RequestBody @Valid CopiarVersionRequest request) {
        return ResponseEntity.ok(
                flujoTrabajoService.copiarVersionComoBorrador(id, request.getNumeroVersion()));
    }

    @GetMapping("/{id}/versiones")
    public ResponseEntity<List<VersionFlujoResumen>> listarVersiones(@PathVariable String id) {
        return ResponseEntity.ok(flujoTrabajoService.listarVersiones(id));
    }

    @GetMapping("/{id}/versiones/{numero}")
    public ResponseEntity<VersionFlujoDetalleResponse> obtenerVersion(
            @PathVariable String id,
            @PathVariable int numero) {
        return ResponseEntity.ok(flujoTrabajoService.obtenerVersion(id, numero));
    }

    @PostMapping("/{id}/desplegar")
    public ResponseEntity<FlujoTrabajoResponse> desplegar(
            @PathVariable String id,
            @RequestBody @Valid DeployRequest request) {
        return ResponseEntity.ok(flujoTrabajoService.desplegar(id, request.getXml()));
    }

    @GetMapping("/{id}/xml")
    public ResponseEntity<XmlResponse> obtenerXml(@PathVariable String id) {
        return ResponseEntity.ok(XmlResponse.builder()
                .xml(flujoTrabajoService.obtenerXml(id))
                .build());
    }
}
