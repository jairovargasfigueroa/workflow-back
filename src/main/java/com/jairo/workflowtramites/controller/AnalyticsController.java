package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/flujos")
    public ResponseEntity<Object> obtenerFlujos() {
        return ResponseEntity.ok(analyticsService.obtenerFlujos());
    }

    @PostMapping("/optimizar")
    public ResponseEntity<Object> optimizar(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(analyticsService.optimizar(body.get("flujoId")));
    }

    @PostMapping("/modelo/entrenar")
    public ResponseEntity<Map<?, ?>> entrenarModelo() {
        return ResponseEntity.ok(analyticsService.entrenarModelo());
    }
}
