package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.model.EventoAuditoriaArchivo;
import com.jairo.workflowtramites.model.enums.TipoEventoArchivo;
import com.jairo.workflowtramites.repository.EventoAuditoriaArchivoRepository;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditoriaArchivoService {

    private final EventoAuditoriaArchivoRepository repository;

    public void registrar(TipoEventoArchivo tipo, String archivoId, String solicitudId, String detalle) {
        EventoAuditoriaArchivo evento = EventoAuditoriaArchivo.builder()
                .archivoId(archivoId)
                .solicitudId(solicitudId)
                .tipo(tipo)
                .fecha(LocalDateTime.now())
                .detalle(detalle)
                .build();

        AuthenticatedUser usuario = usuarioActual();
        if (usuario != null) {
            evento.setUsuarioId(usuario.getId());
            evento.setUsuarioNombre(usuario.getNombre());
            evento.setUsuarioRol(usuario.getRol() != null ? usuario.getRol().name() : null);
            evento.setUsuarioDepartamentoId(usuario.getDepartamentoId());
        }

        repository.save(evento);
    }

    public void registrarDenegado(TipoEventoArchivo tipoIntentado, String archivoId, String motivo) {
        EventoAuditoriaArchivo evento = EventoAuditoriaArchivo.builder()
                .archivoId(archivoId)
                .tipo(TipoEventoArchivo.ACCESS_DENIED)
                .fecha(LocalDateTime.now())
                .detalle("Intento de " + tipoIntentado.name())
                .motivoDenegacion(motivo)
                .build();

        AuthenticatedUser usuario = usuarioActual();
        if (usuario != null) {
            evento.setUsuarioId(usuario.getId());
            evento.setUsuarioNombre(usuario.getNombre());
            evento.setUsuarioRol(usuario.getRol() != null ? usuario.getRol().name() : null);
            evento.setUsuarioDepartamentoId(usuario.getDepartamentoId());
        }

        repository.save(evento);
    }

    public Page<EventoAuditoriaArchivo> buscar(String archivoId, String solicitudId, String usuarioId,
                                                TipoEventoArchivo tipo, LocalDateTime desde, LocalDateTime hasta,
                                                int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        return repository.buscarConFiltros(archivoId, solicitudId, usuarioId, tipo, desde, hasta, pageRequest);
    }

    private AuthenticatedUser usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof AuthenticatedUser u) return u;
        return null;
    }
}
