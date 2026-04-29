package com.jairo.workflowtramites.seed.seeders;

import com.jairo.workflowtramites.dto.request.RespuestaDepartamentoRequest;
import com.jairo.workflowtramites.dto.response.AccionDisponibleResponse;
import com.jairo.workflowtramites.dto.response.TareaActivaResponse;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.Usuario;
import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import com.jairo.workflowtramites.model.embeds.RespuestaCampo;
import com.jairo.workflowtramites.model.embeds.RespuestaDepartamento;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import com.jairo.workflowtramites.repository.SolicitudTramiteRepository;
import com.jairo.workflowtramites.seed.util.FakerRespuestas;
import com.jairo.workflowtramites.service.SolicitudTramiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Avanza una solicitud recién creada por su flujo de trabajo, simulando que
 * funcionarios del depto activo toman y responden la tarea hasta llegar al
 * destino deseado (aprobado, rechazado, o pausada en progreso).
 *
 * No toca la base de datos directamente — usa SolicitudTramiteService para
 * que se ejerciten los mismos caminos que usa el front (claim + respond).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AvanzadorFlujo {

    private static final int MAX_PASOS = 25;

    private final SolicitudTramiteRepository solicitudRepo;
    private final SolicitudTramiteService solicitudService;
    private final FakerRespuestas faker;
    private final Random random = new Random();

    public enum Destino {
        APROBADO,
        RECHAZADO,
        EN_PROCESO_SIN_TOMAR,
        EN_PROCESO_CON_TOMA
    }

    /**
     * Avanza la solicitud hasta alcanzar el destino.
     * funcsPorDepto: map deptoId → funcionarios activos de ese depto (ya precargado).
     */
    public void avanzar(String solicitudId, Destino destino, Map<String, List<Usuario>> funcsPorDepto) {
        int pararEn = switch (destino) {
            case EN_PROCESO_SIN_TOMAR, EN_PROCESO_CON_TOMA -> 1 + random.nextInt(3);
            default -> Integer.MAX_VALUE;
        };

        int pasos = 0;

        for (int i = 0; i < MAX_PASOS; i++) {
            SolicitudTramite solicitud = solicitudRepo.findById(solicitudId).orElse(null);
            if (solicitud == null) return;
            if (solicitud.getEstado() != EstadoTramite.EN_PROCESO) return;

            RespuestaDepartamento pendiente = buscarPendiente(solicitud);
            if (pendiente == null) return;

            if (pasos >= pararEn) {
                if (destino == Destino.EN_PROCESO_CON_TOMA) {
                    tomarSinResponder(solicitudId, pendiente, funcsPorDepto);
                }
                return;
            }

            Usuario funcionario = pickFuncionario(pendiente.getDepartamentoId(), funcsPorDepto);
            if (funcionario == null) {
                log.debug("[AvanzadorFlujo] Sin funcionarios para depto {}, aborta {}",
                        pendiente.getDepartamentoId(), solicitudId);
                return;
            }

            try {
                solicitudService.tomarTarea(
                        solicitudId, pendiente.getElementId(),
                        funcionario.getId(), pendiente.getDepartamentoId());
            } catch (Exception e) {
                log.debug("[AvanzadorFlujo] tomarTarea falló en {}: {}", solicitudId, e.getMessage());
                return;
            }

            TareaActivaResponse tarea = obtenerTareaActiva(solicitudId, pendiente);
            String accion = pickAccion(tarea != null ? tarea.getAcciones() : List.of(), destino);
            List<CampoFormulario> campos = tarea != null ? tarea.getCampos() : List.of();
            List<RespuestaCampo> respuestas = faker.generarRespuestas(campos);

            RespuestaDepartamentoRequest req = new RespuestaDepartamentoRequest();
            req.setDepartamentoId(pendiente.getDepartamentoId());
            req.setElementId(pendiente.getElementId());
            req.setAccion(accion);
            req.setComentario(faker.generarComentario());
            req.setRespuestas(respuestas);

            try {
                solicitudService.responderDepartamento(solicitudId, req, funcionario.getId());
                pasos++;
            } catch (Exception e) {
                log.debug("[AvanzadorFlujo] responderDepartamento falló en {}: {}",
                        solicitudId, e.getMessage());
                return;
            }
        }

        log.debug("[AvanzadorFlujo] Solicitud {} alcanzó MAX_PASOS", solicitudId);
    }

    private RespuestaDepartamento buscarPendiente(SolicitudTramite solicitud) {
        return solicitud.getRespuestasPorDepartamento().stream()
                .filter(r -> r.getFechaRespuesta() == null)
                .findFirst()
                .orElse(null);
    }

    private Usuario pickFuncionario(String deptoId, Map<String, List<Usuario>> funcsPorDepto) {
        List<Usuario> funcs = funcsPorDepto.get(deptoId);
        if (funcs == null || funcs.isEmpty()) return null;
        return funcs.get(random.nextInt(funcs.size()));
    }

    private TareaActivaResponse obtenerTareaActiva(String solicitudId, RespuestaDepartamento pendiente) {
        try {
            List<TareaActivaResponse> tareas = solicitudService.obtenerTareasActivas(
                    solicitudId, pendiente.getDepartamentoId());
            return tareas.stream()
                    .filter(t -> pendiente.getElementId().equals(t.getElementId()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.debug("[AvanzadorFlujo] obtenerTareasActivas falló: {}", e.getMessage());
            return null;
        }
    }

    private void tomarSinResponder(String solicitudId, RespuestaDepartamento pendiente,
                                    Map<String, List<Usuario>> funcsPorDepto) {
        Usuario func = pickFuncionario(pendiente.getDepartamentoId(), funcsPorDepto);
        if (func == null) return;
        try {
            solicitudService.tomarTarea(
                    solicitudId, pendiente.getElementId(),
                    func.getId(), pendiente.getDepartamentoId());
        } catch (Exception e) {
            log.debug("[AvanzadorFlujo] tomar sin responder falló: {}", e.getMessage());
        }
    }

    /**
     * Elige una acción válida según el destino.
     *
     *  - Siempre evita "observado" (generaría loops que no estamos simulando).
     *  - Para RECHAZADO: si "rechazar/rechazado" está disponible, la elige.
     *  - En caso contrario prefiere acciones positivas (aprob*, corrig*, emitir).
     */
    private String pickAccion(List<AccionDisponibleResponse> acciones, Destino destino) {
        if (acciones == null || acciones.isEmpty()) return "avanzar";

        List<String> valores = new ArrayList<>();
        for (AccionDisponibleResponse a : acciones) {
            if (a.getValor() != null) valores.add(a.getValor());
        }
        if (valores.isEmpty()) return "avanzar";

        List<String> validas = new ArrayList<>();
        for (String v : valores) {
            if (!v.toLowerCase().contains("observ")) validas.add(v);
        }
        if (validas.isEmpty()) validas = valores;

        if (destino == Destino.RECHAZADO) {
            for (String v : validas) {
                if (v.toLowerCase().contains("rechaz")) return v;
            }
        }

        for (String v : validas) {
            String l = v.toLowerCase();
            if (l.contains("aprob") || l.contains("corrig") || l.contains("emitir")) return v;
        }

        return validas.get(0);
    }
}
