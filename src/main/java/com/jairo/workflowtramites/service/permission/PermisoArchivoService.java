package com.jairo.workflowtramites.service.permission;

import com.jairo.workflowtramites.model.Archivo;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.embeds.PermisoSet;
import com.jairo.workflowtramites.model.embeds.SujetoPermiso;
import com.jairo.workflowtramites.model.enums.AccionArchivo;
import com.jairo.workflowtramites.model.enums.Rol;
import com.jairo.workflowtramites.repository.SolicitudTramiteRepository;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermisoArchivoService {

    private final SolicitudTramiteRepository solicitudRepository;

    public boolean puede(AuthenticatedUser usuario, AccionArchivo accion, Archivo archivo) {
        if (usuario == null) return false;

        // La inmutabilidad aplica a TODOS, incluido el ADMIN: un documento cerrado
        // ("el contrato que nadie puede tocar") solo se puede ver/descargar, nunca
        // modificar ni eliminar. Por eso este chequeo va ANTES del bypass de admin.
        if (archivo.isInmutable() && accion != AccionArchivo.VER && accion != AccionArchivo.DESCARGAR) {
            return false;
        }

        if (usuario.getRol() == Rol.ADMIN) return true;

        if (usuario.getId() != null && usuario.getId().equals(archivo.getSubidoPor())) {
            if (accion == AccionArchivo.VER || accion == AccionArchivo.DESCARGAR) {
                return true;
            }
        }

        PermisoSet permisos = archivo.getPermisos();
        if (permisos == null) {
            return permisosLegacy(usuario, archivo);
        }

        List<SujetoPermiso> sujetos = switch (accion) {
            case VER, DESCARGAR -> permisos.getLectores();
            case SUBIR_VERSION -> permisos.getEditores();
            case ELIMINAR -> permisos.getEliminadores();
        };

        if (sujetos == null || sujetos.isEmpty()) {
            return permisosLegacy(usuario, archivo);
        }

        SolicitudTramite solicitud = obtenerSolicitud(archivo.getSolicitudId());

        return sujetos.stream().anyMatch(s -> cumple(usuario, s, archivo, solicitud));
    }

    private boolean cumple(AuthenticatedUser usuario, SujetoPermiso sujeto,
                            Archivo archivo, SolicitudTramite solicitud) {
        if (sujeto == null || sujeto.getTipo() == null) return false;

        return switch (sujeto.getTipo()) {
            case TODOS_AUTENTICADOS -> true;
            case ROL -> usuario.getRol() != null
                    && usuario.getRol().name().equals(sujeto.getSujetoId());
            case DEPARTAMENTO -> usuario.getDepartamentoId() != null
                    && usuario.getDepartamentoId().equals(sujeto.getSujetoId());
            case DUENO_TRAMITE -> solicitud != null
                    && usuario.getId() != null
                    && usuario.getId().equals(solicitud.getSolicitanteId());
            case DPTO_ORIGEN -> usuario.getDepartamentoId() != null
                    && archivo.getDepartamentoOrigenId() != null
                    && usuario.getDepartamentoId().equals(archivo.getDepartamentoOrigenId());
            case QUIEN_LO_SUBIO -> usuario.getId() != null
                    && usuario.getId().equals(archivo.getSubidoPor());
            case TODOS_SIGUIENTES_NODOS -> usuario.getRol() == Rol.FUNCIONARIO;
        };
    }

    private boolean permisosLegacy(AuthenticatedUser usuario, Archivo archivo) {
        if (usuario.getRol() == Rol.FUNCIONARIO) return true;
        if (usuario.getId() != null && usuario.getId().equals(archivo.getSubidoPor())) return true;
        if (usuario.getRol() == Rol.SOLICITANTE) {
            SolicitudTramite s = obtenerSolicitud(archivo.getSolicitudId());
            return s != null && usuario.getId() != null && usuario.getId().equals(s.getSolicitanteId());
        }
        return false;
    }

    private SolicitudTramite obtenerSolicitud(String solicitudId) {
        if (solicitudId == null) return null;
        Optional<SolicitudTramite> opt = solicitudRepository.findById(solicitudId);
        return opt.orElse(null);
    }
}
