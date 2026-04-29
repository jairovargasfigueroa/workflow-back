package com.jairo.workflowtramites;

import com.jairo.workflowtramites.dto.request.SolicitudTramiteRequest;
import com.jairo.workflowtramites.dto.response.SolicitudTramiteResponse;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.model.Usuario;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import com.jairo.workflowtramites.model.enums.Rol;
import com.jairo.workflowtramites.repository.SolicitudTramiteRepository;
import com.jairo.workflowtramites.repository.TramiteRepository;
import com.jairo.workflowtramites.repository.UsuarioRepository;
import com.jairo.workflowtramites.seed.seeders.AvanzadorFlujo;
import com.jairo.workflowtramites.service.SolicitudTramiteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlujoSolicitudIntegrationTest {

    @Autowired private SolicitudTramiteService solicitudService;
    @Autowired private SolicitudTramiteRepository solicitudRepository;
    @Autowired private TramiteRepository tramiteRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private AvanzadorFlujo avanzadorFlujo;

    private final List<String> solicitudesCreadas = new ArrayList<>();

    @AfterEach
    void limpiar() {
        solicitudesCreadas.forEach(solicitudRepository::deleteById);
        solicitudesCreadas.clear();
    }

    @Test
    void solicitudDebeQuedarEnProcesoAlCrearse() {
        Tramite tramite = obtenerTodosLosTramitesConFlujo().findFirst().orElseThrow();
        String solicitanteId = obtenerSolicitanteId();

        SolicitudTramiteRequest request = new SolicitudTramiteRequest();
        request.setTramiteId(tramite.getId());

        SolicitudTramiteResponse resultado = solicitudService.crear(request, solicitanteId);
        solicitudesCreadas.add(resultado.getId());

        assertEquals(EstadoTramite.EN_PROCESO, resultado.getEstado());
        assertFalse(resultado.getDepartamentosActuales().isEmpty());
        assertFalse(resultado.getRespuestasPorDepartamento().isEmpty());
    }

    @ParameterizedTest(name = "Aprobado — {0}")
    @MethodSource("tramiteIds")
    void cadaTramiteDebePoderTerminarEnAprobado(String tramiteId) {
        String solicitanteId = obtenerSolicitanteId();
        Map<String, List<Usuario>> funcsPorDepto = buildFuncsPorDepto();

        SolicitudTramiteRequest request = new SolicitudTramiteRequest();
        request.setTramiteId(tramiteId);

        SolicitudTramiteResponse creada = solicitudService.crear(request, solicitanteId);
        solicitudesCreadas.add(creada.getId());

        avanzadorFlujo.avanzar(creada.getId(), AvanzadorFlujo.Destino.APROBADO, funcsPorDepto);

        SolicitudTramite solicitudFinal = solicitudRepository.findById(creada.getId()).orElseThrow();
        assertEquals(EstadoTramite.APROBADO, solicitudFinal.getEstado(),
                "El trámite " + tramiteId + " no terminó en APROBADO");
    }

    @ParameterizedTest(name = "Rechazado — {0}")
    @MethodSource("tramiteIds")
    void cadaTramiteDebePoderTerminarEnRechazado(String tramiteId) {
        String solicitanteId = obtenerSolicitanteId();
        Map<String, List<Usuario>> funcsPorDepto = buildFuncsPorDepto();

        SolicitudTramiteRequest request = new SolicitudTramiteRequest();
        request.setTramiteId(tramiteId);

        SolicitudTramiteResponse creada = solicitudService.crear(request, solicitanteId);
        solicitudesCreadas.add(creada.getId());

        avanzadorFlujo.avanzar(creada.getId(), AvanzadorFlujo.Destino.RECHAZADO, funcsPorDepto);

        SolicitudTramite solicitudFinal = solicitudRepository.findById(creada.getId()).orElseThrow();
        assertNotEquals(EstadoTramite.EN_PROCESO, solicitudFinal.getEstado(),
                "El trámite " + tramiteId + " quedó colgado en EN_PROCESO sin terminar");
    }

    // -------- source para @ParameterizedTest --------

    Stream<String> tramiteIds() {
        return obtenerTodosLosTramitesConFlujo().map(Tramite::getId);
    }

    // -------- helpers --------

    private Stream<Tramite> obtenerTodosLosTramitesConFlujo() {
        List<Tramite> tramites = tramiteRepository.findByActivoTrue().stream()
                .filter(t -> t.getFlujoTrabajoId() != null)
                .toList();
        if (tramites.isEmpty())
            throw new IllegalStateException("No hay trámites activos con flujo — ejecuta el seed primero");
        return tramites.stream();
    }

    private String obtenerSolicitanteId() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == Rol.SOLICITANTE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay solicitantes — ejecuta el seed primero"))
                .getId();
    }

    private Map<String, List<Usuario>> buildFuncsPorDepto() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == Rol.FUNCIONARIO && u.getDepartamentoId() != null)
                .collect(Collectors.groupingBy(Usuario::getDepartamentoId));
    }
}
