package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.request.TramiteRequest;
import com.jairo.workflowtramites.dto.response.TramiteResponse;
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
}
