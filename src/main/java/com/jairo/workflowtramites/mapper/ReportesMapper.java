package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.response.reportes.DemoraResponse;
import com.jairo.workflowtramites.dto.response.reportes.DepartamentoReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.DepartamentoResumenResponse;
import com.jairo.workflowtramites.dto.response.reportes.SolicitudReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.TramiteReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.UsuarioReporteResponse;
import com.jairo.workflowtramites.model.Departamento;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.model.Usuario;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReportesMapper {

    public static TramiteReporteResponse toTramiteReporteResponse(Tramite t, long totalSolicitudes) {
        return TramiteReporteResponse.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .activo(t.isActivo())
                .totalSolicitudes(totalSolicitudes)
                .build();
    }

    public static DepartamentoReporteResponse toDepartamentoReporteResponse(
            Departamento d, long totalFuncionarios, long totalSolicitudesActivas) {
        return DepartamentoReporteResponse.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .activo(d.isActivo())
                .totalFuncionarios(totalFuncionarios)
                .totalSolicitudesActivas(totalSolicitudesActivas)
                .build();
    }

    public static UsuarioReporteResponse toUsuarioReporteResponse(Usuario u, String departamentoNombre) {
        return UsuarioReporteResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .rol(u.getRol())
                .departamentoId(u.getDepartamentoId())
                .departamentoNombre(departamentoNombre)
                .activo(u.isActivo())
                .build();
    }

    public static SolicitudReporteResponse toSolicitudReporteResponse(
            SolicitudTramite s, Map<String, String> nombresDeptos) {
        Double horas = null;
        if (s.getFechaCreacion() != null) {
            LocalDateTime fin = s.getFechaFinalizacion() != null ? s.getFechaFinalizacion() : LocalDateTime.now();
            horas = Duration.between(s.getFechaCreacion(), fin).toMinutes() / 60.0;
        }

        // Resuelve los IDs de departamentosActuales a {id, nombre} usando el catalogo.
        // Si no se encuentra el nombre, cae al id (no rompe, queda visible que falta).
        List<DepartamentoResumenResponse> deptos = s.getDepartamentosActuales() == null
                ? List.of()
                : s.getDepartamentosActuales().stream()
                        .map(id -> DepartamentoResumenResponse.builder()
                                .id(id)
                                .nombre(nombresDeptos.getOrDefault(id, id))
                                .build())
                        .toList();

        return SolicitudReporteResponse.builder()
                .id(s.getId())
                .tramiteId(s.getTramiteId())
                .tramiteNombre(s.getTramiteNombre())
                .solicitanteId(s.getSolicitanteId())
                .solicitanteNombre(s.getSolicitanteNombre())
                .estado(s.getEstado())
                .departamentosActuales(deptos)
                .fechaCreacion(s.getFechaCreacion())
                .fechaFinalizacion(s.getFechaFinalizacion())
                .horasTranscurridas(horas)
                .build();
    }

    public static DemoraResponse toDemoraResponse(
            SolicitudTramite s, String departamentoActualId, String departamentoActualNombre, double horas) {
        return DemoraResponse.builder()
                .solicitudId(s.getId())
                .tramiteNombre(s.getTramiteNombre())
                .solicitanteNombre(s.getSolicitanteNombre())
                .departamentoActualId(departamentoActualId)
                .departamentoActualNombre(departamentoActualNombre)
                .estado(s.getEstado())
                .fechaCreacion(s.getFechaCreacion())
                .horasTranscurridas(horas)
                .build();
    }
}
