package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.response.reportes.DemoraResponse;
import com.jairo.workflowtramites.dto.response.reportes.DepartamentoReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.SolicitudReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.TramiteReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.UsuarioReporteResponse;
import com.jairo.workflowtramites.model.Departamento;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.model.Usuario;

import java.time.Duration;
import java.time.LocalDateTime;

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

    public static SolicitudReporteResponse toSolicitudReporteResponse(SolicitudTramite s) {
        Double horas = null;
        if (s.getFechaCreacion() != null) {
            LocalDateTime fin = s.getFechaFinalizacion() != null ? s.getFechaFinalizacion() : LocalDateTime.now();
            horas = Duration.between(s.getFechaCreacion(), fin).toMinutes() / 60.0;
        }
        return SolicitudReporteResponse.builder()
                .id(s.getId())
                .tramiteId(s.getTramiteId())
                .tramiteNombre(s.getTramiteNombre())
                .solicitanteId(s.getSolicitanteId())
                .solicitanteNombre(s.getSolicitanteNombre())
                .estado(s.getEstado())
                .departamentosActuales(s.getDepartamentosActuales())
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
