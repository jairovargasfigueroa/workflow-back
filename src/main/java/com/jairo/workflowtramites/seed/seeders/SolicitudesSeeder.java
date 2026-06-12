package com.jairo.workflowtramites.seed.seeders;

import com.jairo.workflowtramites.dto.request.SolicitudTramiteRequest;
import com.jairo.workflowtramites.dto.response.SolicitudTramiteResponse;
import com.jairo.workflowtramites.model.FormularioTemplate;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.model.Usuario;
import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import com.jairo.workflowtramites.model.embeds.RespuestaCampo;
import com.jairo.workflowtramites.model.enums.Rol;
import com.jairo.workflowtramites.repository.FormularioTemplateRepository;
import com.jairo.workflowtramites.repository.TramiteRepository;
import com.jairo.workflowtramites.repository.UsuarioRepository;
import com.jairo.workflowtramites.seed.config.SeedConfig;
import com.jairo.workflowtramites.seed.util.FakerRespuestas;
import com.jairo.workflowtramites.service.SolicitudTramiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Genera N solicitudes de prueba con historial completo:
 *
 *   1. Carga trámites activos, solicitantes, y funcionarios agrupados por depto.
 *   2. Para cada solicitud:
 *        - elige trámite + solicitante al azar,
 *        - genera respuestas del formulario inicial con Faker,
 *        - llama solicitudService.crear(...) y obtiene la solicitud viva,
 *        - decide destino (APROBADO / RECHAZADO / EN_PROCESO*)
 *        - delega el avance a AvanzadorFlujo.
 *   3. Al final, delega a RedistribuidorFechas para reescribir timestamps
 *      distribuidos en los últimos N días (seed.distribuir-ultimos-dias).
 *
 * Cantidad objetivo: seed.cantidad-solicitudes (por defecto 2000 en SeedConfig).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SolicitudesSeeder {

    private final SeedConfig seedConfig;
    private final TramiteRepository tramiteRepo;
    private final UsuarioRepository usuarioRepo;
    private final FormularioTemplateRepository formularioRepo;
    private final SolicitudTramiteService solicitudService;
    private final AvanzadorFlujo avanzador;
    private final RedistribuidorFechas redistribuidor;
    private final FakerRespuestas faker;
    private final Random random = new Random();

    public void sembrar() {
        int total = seedConfig.getCantidadSolicitudes();
        log.info("[SolicitudesSeeder] Iniciando generación de {} solicitudes", total);

        List<Tramite> tramites = tramiteRepo.findByActivoTrue().stream()
                .filter(t -> t.getFlujoTrabajoId() != null)
                .toList();
        if (tramites.isEmpty()) {
            log.warn("[SolicitudesSeeder] No hay trámites activos con flujo. Aborta.");
            return;
        }

        List<Usuario> usuarios = usuarioRepo.findAll();
        List<Usuario> solicitantes = usuarios.stream()
                .filter(u -> u.getRol() == Rol.SOLICITANTE && u.isActivo())
                .toList();
        if (solicitantes.isEmpty()) {
            log.warn("[SolicitudesSeeder] No hay solicitantes activos. Aborta.");
            return;
        }

        Map<String, List<Usuario>> funcionariosPorDepto = usuarios.stream()
                .filter(u -> u.getRol() == Rol.FUNCIONARIO
                        && u.isActivo()
                        && u.getDepartamentoId() != null)
                .collect(Collectors.groupingBy(Usuario::getDepartamentoId));

        Map<String, List<CampoFormulario>> camposPorTramite = new HashMap<>();
        for (Tramite t : tramites) {
            if (t.getFormularioSolicitanteId() != null) {
                formularioRepo.findById(t.getFormularioSolicitanteId())
                        .map(FormularioTemplate::getCampos)
                        .ifPresent(campos -> camposPorTramite.put(t.getId(), campos));
            }
        }

        long inicio = System.currentTimeMillis();
        int creadas = 0;
        int errores = 0;
        int[] conteoPorDestino = new int[AvanzadorFlujo.Destino.values().length];

        for (int i = 1; i <= total; i++) {
            Tramite tramite = tramites.get(random.nextInt(tramites.size()));
            Usuario solicitante = solicitantes.get(random.nextInt(solicitantes.size()));

            List<CampoFormulario> campos = camposPorTramite.getOrDefault(tramite.getId(), List.of());
            List<RespuestaCampo> respuestas = faker.generarRespuestas(campos);

            SolicitudTramiteRequest req = new SolicitudTramiteRequest();
            req.setTramiteId(tramite.getId());
            req.setRespuestas(respuestas);

            try {
                SolicitudTramiteResponse creada = solicitudService.crear(req, solicitante.getId());
                AvanzadorFlujo.Destino destino = pickDestino();
                conteoPorDestino[destino.ordinal()]++;
                avanzador.avanzar(creada.getId(), destino, funcionariosPorDepto);
                creadas++;
            } catch (Exception e) {
                errores++;
                log.warn("[SolicitudesSeeder] #{} error: {}", i, e.getMessage());
            }

            if (i % 100 == 0 || i == total) {
                long segundos = (System.currentTimeMillis() - inicio) / 1000;
                log.info("[SolicitudesSeeder] Progreso: {}/{}  ({}s)", i, total, segundos);
            }
        }

        log.info("[SolicitudesSeeder] Creadas={} | Errores={} | Destinos: APROBADO={}, RECHAZADO={}, EN_PROC_SIN_TOMAR={}, EN_PROC_CON_TOMA={}",
                creadas, errores,
                conteoPorDestino[AvanzadorFlujo.Destino.APROBADO.ordinal()],
                conteoPorDestino[AvanzadorFlujo.Destino.RECHAZADO.ordinal()],
                conteoPorDestino[AvanzadorFlujo.Destino.EN_PROCESO_SIN_TOMAR.ordinal()],
                conteoPorDestino[AvanzadorFlujo.Destino.EN_PROCESO_CON_TOMA.ordinal()]);

        log.info("[SolicitudesSeeder] Reescribiendo fechas retroactivamente...");
        redistribuidor.redistribuir();
        log.info("[SolicitudesSeeder] Finalizado.");
    }

    private AvanzadorFlujo.Destino pickDestino() {
        double r = random.nextDouble();
        if (r < 0.60) return AvanzadorFlujo.Destino.APROBADO;
        if (r < 0.80) return AvanzadorFlujo.Destino.RECHAZADO;
        if (r < 0.95) return AvanzadorFlujo.Destino.EN_PROCESO_SIN_TOMAR;
        return AvanzadorFlujo.Destino.EN_PROCESO_CON_TOMA;
    }
}
