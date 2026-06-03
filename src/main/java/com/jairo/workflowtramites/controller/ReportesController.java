package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.request.reportes.ChatReporteRequest;
import com.jairo.workflowtramites.dto.response.reportes.DemoraResponse;
import com.jairo.workflowtramites.dto.response.reportes.DepartamentoReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.MetricaTiempoResponse;
import com.jairo.workflowtramites.dto.response.reportes.PaginaResponse;
import com.jairo.workflowtramites.dto.response.reportes.ProductividadDepartamentoResponse;
import com.jairo.workflowtramites.dto.response.reportes.SolicitudReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.TopNItemResponse;
import com.jairo.workflowtramites.dto.response.reportes.TramiteReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.UsuarioReporteResponse;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import com.jairo.workflowtramites.model.enums.Rol;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import com.jairo.workflowtramites.service.ChatGatewayService;
import com.jairo.workflowtramites.service.ReportesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final ReportesService reportesService;
    private final ChatGatewayService chatGatewayService;

    // ---------- 1. TRÁMITES ----------
    @GetMapping("/tramites")
    public ResponseEntity<List<TramiteReporteResponse>> listarTramites(
            @RequestParam(defaultValue = "true") boolean soloActivos,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return ResponseEntity.ok(reportesService.listarTramites(soloActivos));
    }

    // ---------- 2. DEPARTAMENTOS ----------
    @GetMapping("/departamentos")
    public ResponseEntity<List<DepartamentoReporteResponse>> listarDepartamentos(
            @RequestParam(defaultValue = "true") boolean soloActivos,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return ResponseEntity.ok(reportesService.listarDepartamentos(soloActivos, user));
    }

    // ---------- 3. USUARIOS ----------
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioReporteResponse>> listarUsuarios(
            @RequestParam(required = false) Rol rol,
            @RequestParam(required = false) String departamentoId,
            @RequestParam(required = false) Boolean activo,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return ResponseEntity.ok(reportesService.listarUsuarios(rol, departamentoId, activo, user));
    }

    // ---------- 4. SOLICITUDES ----------
    @GetMapping("/solicitudes")
    public ResponseEntity<PaginaResponse<SolicitudReporteResponse>> listarSolicitudes(
            @RequestParam(required = false) EstadoTramite estado,
            @RequestParam(required = false) String tramiteId,
            @RequestParam(required = false) String departamentoActualId,
            @RequestParam(required = false) String solicitanteId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return ResponseEntity.ok(reportesService.listarSolicitudes(
                estado, tramiteId, departamentoActualId, solicitanteId,
                fechaDesde, fechaHasta, page, size, user));
    }

    // ---------- 5. TOP N ----------
    @GetMapping("/top-n")
    public ResponseEntity<List<TopNItemResponse>> topN(
            @RequestParam String entidad,
            @RequestParam String criterio,
            @RequestParam(defaultValue = "10") int n,
            @RequestParam(defaultValue = "mes") String periodo,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return ResponseEntity.ok(reportesService.topN(entidad, criterio, n, periodo, user));
    }

    // ---------- 6. MÉTRICAS DE TIEMPO ----------
    @GetMapping("/metricas-tiempo")
    public ResponseEntity<List<MetricaTiempoResponse>> metricasTiempo(
            @RequestParam String agrupacion,
            @RequestParam(defaultValue = "mes") String periodo,
            @RequestParam(required = false) String entidadId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return ResponseEntity.ok(reportesService.metricasTiempo(agrupacion, periodo, entidadId, user));
    }

    // ---------- 7. PRODUCTIVIDAD DEPARTAMENTOS ----------
    @GetMapping("/productividad-departamentos")
    public ResponseEntity<List<ProductividadDepartamentoResponse>> productividadDepartamentos(
            @RequestParam(defaultValue = "mes") String periodo,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return ResponseEntity.ok(reportesService.productividadDepartamentos(periodo, user));
    }

    // ---------- 8. DEMORAS ----------
    @GetMapping("/demoras")
    public ResponseEntity<List<DemoraResponse>> demoras(
            @RequestParam(defaultValue = "48") double umbralHoras,
            @RequestParam(required = false) EstadoTramite estado,
            @RequestParam(defaultValue = "mes") String periodo,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return ResponseEntity.ok(reportesService.demoras(umbralHoras, estado, periodo, user));
    }

    // ---------- 9. GATEWAY CHAT SSE ----------
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(
            @Valid @RequestBody ChatReporteRequest request,
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal AuthenticatedUser user) {
        autorizar(user);
        return chatGatewayService.proxyChat(request, authHeader);
    }

    // ---------- 10. GATEWAY DESCARGA ----------
    @GetMapping("/descargar/{archivoId}")
    public ResponseEntity<byte[]> descargar(
            @PathVariable String archivoId,
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal AuthenticatedUser user) throws Exception {
        autorizar(user);
        HttpResponse<byte[]> microResponse = chatGatewayService.proxyDescarga(archivoId, authHeader);

        HttpHeaders headers = new HttpHeaders();
        microResponse.headers().firstValue("content-type")
                .ifPresent(v -> headers.add(HttpHeaders.CONTENT_TYPE, v));
        microResponse.headers().firstValue("content-disposition")
                .ifPresent(v -> headers.add(HttpHeaders.CONTENT_DISPOSITION, v));

        return ResponseEntity.status(microResponse.statusCode())
                .headers(headers)
                .body(microResponse.body());
    }

    // ---------- AUTORIZACIÓN ----------
    private void autorizar(AuthenticatedUser user) {
        if (user == null) {
            throw new AccessDeniedException("Autenticación requerida");
        }
        if (user.getRol() == Rol.SOLICITANTE) {
            throw new AccessDeniedException("Reportes no disponibles para tu rol");
        }
        if (user.getRol() == Rol.FUNCIONARIO && user.getDepartamentoId() == null) {
            throw new AccessDeniedException("Funcionario sin departamento asignado");
        }
    }
}
