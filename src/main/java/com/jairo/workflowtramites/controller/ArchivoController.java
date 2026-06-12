package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.response.ArchivoDescargaResponse;
import com.jairo.workflowtramites.dto.response.ArchivoResponse;
import com.jairo.workflowtramites.dto.response.reportes.PaginaResponse;
import com.jairo.workflowtramites.mapper.ArchivoMapper;
import com.jairo.workflowtramites.model.Archivo;
import com.jairo.workflowtramites.model.enums.Rol;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import com.jairo.workflowtramites.service.ArchivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import java.time.LocalDateTime;
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
            @RequestParam(value = "departamentoOrigenId", required = false) String departamentoOrigenId,
            @RequestParam(value = "clientId", required = false) String clientId) {

        var creado = archivoService.subir(archivo, solicitudId, campoFormulario, departamentoOrigenId, clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ArchivoMapper.toResponse(creado));
    }

    @GetMapping("/solicitud/{solicitudId}")
    public List<ArchivoResponse> listarPorSolicitud(@PathVariable String solicitudId) {
        return archivoService.listarPorSolicitud(solicitudId)
                .stream()
                .map(ArchivoMapper::toResponse)
                .toList();
    }

    /**
     * Repositorio documental global — SOLO ADMIN ("el jefe hace consultas").
     * Vista de consulta: lista metadata con filtros, no edita nada.
     */
    @GetMapping("/repositorio")
    public PaginaResponse<ArchivoResponse> repositorio(
            @RequestParam(required = false) String tramiteId,
            @RequestParam(required = false) String clienteId,
            @RequestParam(required = false) String formato,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal AuthenticatedUser user) {

        autorizarAdmin(user);

        PaginaResponse<Archivo> pagina = archivoService.listarRepositorio(
                tramiteId, clienteId, formato, fechaDesde, fechaHasta, page, size);

        return PaginaResponse.<ArchivoResponse>builder()
                .contenido(pagina.getContenido().stream().map(ArchivoMapper::toResponse).toList())
                .pagina(pagina.getPagina())
                .tamano(pagina.getTamano())
                .total(pagina.getTotal())
                .totalPaginas(pagina.getTotalPaginas())
                .build();
    }

    @GetMapping("/{archivoId}/descargar")
    public ArchivoDescargaResponse descargar(@PathVariable String archivoId,
                                             @RequestParam(defaultValue = "true") boolean attachment) {
        return archivoService.generarUrlDescarga(archivoId, attachment);
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

    private void autorizarAdmin(AuthenticatedUser user) {
        if (user == null) {
            throw new AccessDeniedException("Autenticación requerida");
        }
        if (user.getRol() != Rol.ADMIN) {
            throw new AccessDeniedException("Solo administradores pueden consultar el repositorio documental");
        }
    }
}
