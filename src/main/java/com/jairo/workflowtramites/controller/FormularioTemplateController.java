package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.request.FormularioTemplateRequest;
import com.jairo.workflowtramites.dto.response.FormularioTemplateResponse;
import com.jairo.workflowtramites.service.FormularioTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formularios")
@RequiredArgsConstructor
public class FormularioTemplateController {

    private final FormularioTemplateService formularioTemplateService;

    @GetMapping
    public ResponseEntity<List<FormularioTemplateResponse>> listar() {
        return ResponseEntity.ok(formularioTemplateService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormularioTemplateResponse> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(formularioTemplateService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<FormularioTemplateResponse> crear(
            @RequestBody @Valid FormularioTemplateRequest request) {
        return ResponseEntity.status(201).body(formularioTemplateService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormularioTemplateResponse> actualizar(
            @PathVariable String id,
            @RequestBody @Valid FormularioTemplateRequest request) {
        return ResponseEntity.ok(formularioTemplateService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        formularioTemplateService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
