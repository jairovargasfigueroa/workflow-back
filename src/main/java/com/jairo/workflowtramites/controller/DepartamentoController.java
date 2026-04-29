package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.request.DepartamentoRequest;
import com.jairo.workflowtramites.dto.response.DepartamentoResponse;
import com.jairo.workflowtramites.service.DepartamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
@RequiredArgsConstructor
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    @GetMapping
    public ResponseEntity<List<DepartamentoResponse>> listar() {
        return ResponseEntity.ok(departamentoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(departamentoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponse> crear(@RequestBody @Valid DepartamentoRequest request) {
        return ResponseEntity.status(201).body(departamentoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> actualizar(
            @PathVariable String id,
            @RequestBody @Valid DepartamentoRequest request) {
        return ResponseEntity.ok(departamentoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        departamentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
