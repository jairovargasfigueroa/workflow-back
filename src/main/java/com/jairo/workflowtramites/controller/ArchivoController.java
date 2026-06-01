package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.response.ArchivoDescargaResponse;
import com.jairo.workflowtramites.dto.response.ArchivoResponse;
import com.jairo.workflowtramites.mapper.ArchivoMapper;
import com.jairo.workflowtramites.service.ArchivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/archivos")
@RequiredArgsConstructor
public class ArchivoController {

    private final ArchivoService archivoService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ArchivoResponse> subir(
            @RequestPart("archivo") MultipartFile archivo,
            @RequestParam("solicitudId") String solicitudId,
            @RequestParam(value = "campoFormulario", required = false) String campoFormulario,
            @RequestParam(value = "departamentoOrigenId", required = false) String departamentoOrigenId) {

        var creado = archivoService.subir(archivo, solicitudId, campoFormulario, departamentoOrigenId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ArchivoMapper.toResponse(creado));
    }

    @GetMapping("/solicitud/{solicitudId}")
    public List<ArchivoResponse> listarPorSolicitud(@PathVariable String solicitudId) {
        return archivoService.listarPorSolicitud(solicitudId)
                .stream()
                .map(ArchivoMapper::toResponse)
                .toList();
    }

    @GetMapping("/{archivoId}/descargar")
    public ArchivoDescargaResponse descargar(@PathVariable String archivoId) {
        return archivoService.generarUrlDescarga(archivoId);
    }

    @GetMapping("/{archivoId}/metadata")
    public ArchivoResponse metadata(@PathVariable String archivoId) {
        return ArchivoMapper.toResponse(archivoService.obtenerMetadata(archivoId));
    }

    @PutMapping(value = "/{archivoId}", consumes = "multipart/form-data")
    public ArchivoResponse nuevaVersion(
            @PathVariable String archivoId,
            @RequestPart("archivo") MultipartFile archivo) {
        return ArchivoMapper.toResponse(archivoService.subirNuevaVersion(archivoId, archivo));
    }

    @DeleteMapping("/{archivoId}")
    public ResponseEntity<Void> eliminar(@PathVariable String archivoId) {
        archivoService.eliminar(archivoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{archivoId}/versiones")
    public List<ArchivoResponse> listarVersiones(@PathVariable String archivoId) {
        return archivoService.listarVersiones(archivoId)
                .stream()
                .map(ArchivoMapper::toResponse)
                .toList();
    }

    @PostMapping("/{archivoId}/revertir/{numero}")
    public ArchivoResponse revertir(@PathVariable String archivoId, @PathVariable int numero) {
        return ArchivoMapper.toResponse(archivoService.revertirAVersion(archivoId, numero));
    }
}
