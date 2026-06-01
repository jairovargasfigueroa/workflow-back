package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.request.RespuestaDepartamentoRequest;
import com.jairo.workflowtramites.dto.request.SolicitudTramiteRequest;
import com.jairo.workflowtramites.dto.response.AccionDisponibleResponse;
import com.jairo.workflowtramites.dto.response.NodoFlujoResponse;
import com.jairo.workflowtramites.dto.response.SolicitudTramiteResumen;
import com.jairo.workflowtramites.dto.response.SolicitudTramiteResponse;
import com.jairo.workflowtramites.dto.response.TareaActivaResponse;
import com.jairo.workflowtramites.dto.response.TransicionResponse;
import com.jairo.workflowtramites.mapper.SolicitudTramiteMapper;
import com.jairo.workflowtramites.model.FlujoTrabajo;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.model.embeds.RespuestaDepartamento;
import com.jairo.workflowtramites.model.enums.EstadoFlujo;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import com.jairo.workflowtramites.repository.SolicitudTramiteRepository;
import com.jairo.workflowtramites.service.notification.NotificationPayload;
import com.jairo.workflowtramites.service.notification.NotificationService;
import com.jairo.workflowtramites.dto.response.VersionFlujoResponse;
import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import com.jairo.workflowtramites.exception.RecursoNoEncontradoException;
import com.jairo.workflowtramites.service.workflow.TareaActiva;
import com.jairo.workflowtramites.service.workflow.WorkflowResult;
import com.jairo.workflowtramites.service.workflow.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudTramiteService {

    private final SolicitudTramiteRepository solicitudTramiteRepository;
    private final TramiteService tramiteService;
    private final FlujoTrabajoService flujoTrabajoService;
    private final WorkflowService workflowService;
    private final NotificationService notificationService;
    private final UsuarioService usuarioService;
    private final DepartamentoService departamentoService;
    private final FormularioTemplateService formularioTemplateService;
    private final MongoTemplate mongoTemplate;
    private final com.jairo.workflowtramites.repository.ArchivoRepository archivoRepository;
    private final com.jairo.workflowtramites.repository.VersionFlujoRepository versionFlujoRepository;

    public List<SolicitudTramiteResumen> listar() {
        return solicitudTramiteRepository.findAll()
                .stream()
                .map(SolicitudTramiteMapper::toResumen)
                .toList();
    }

    public SolicitudTramiteResponse obtenerPorId(String id) {
        SolicitudTramite solicitud = solicitudTramiteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada: " + id));
        return SolicitudTramiteMapper.toResponse(solicitud);
    }

    public List<SolicitudTramiteResumen> listarPorTramite(String tramiteId) {
        return solicitudTramiteRepository.findByTramiteId(tramiteId)
                .stream()
                .map(SolicitudTramiteMapper::toResumen)
                .toList();
    }

    public List<SolicitudTramiteResumen> listarPorSolicitante(String solicitanteId) {
        return solicitudTramiteRepository.findBySolicitanteId(solicitanteId)
                .stream()
                .map(SolicitudTramiteMapper::toResumen)
                .toList();
    }

    public List<SolicitudTramiteResumen> listarPorDepartamento(String departamentoId) {
        return solicitudTramiteRepository.findByDepartamentosActualesContaining(departamentoId)
                .stream()
                .map(SolicitudTramiteMapper::toResumen)
                .toList();
    }

    public List<SolicitudTramiteResumen> listarPorDepartamentoYEstado(String departamentoId, EstadoTramite estado) {
        return solicitudTramiteRepository.findByDepartamentosActualesContainingAndEstado(departamentoId, estado)
                .stream()
                .map(SolicitudTramiteMapper::toResumen)
                .toList();
    }

    public List<SolicitudTramiteResumen> listarPendientesSinAsignar(String departamentoId) {
        return solicitudTramiteRepository.findPendientesSinAsignar(departamentoId)
                .stream()
                .map(s -> SolicitudTramiteMapper.toResumenParaDepartamento(s, departamentoId))
                .toList();
    }

    public List<SolicitudTramiteResumen> listarMisTareas(String usuarioId) {
        return solicitudTramiteRepository.findMisTareasTomadas(usuarioId)
                .stream()
                .map(SolicitudTramiteMapper::toResumen)
                .toList();
    }

    public List<SolicitudTramiteResumen> listarHistorialDepartamento(String departamentoId) {
        return solicitudTramiteRepository.findPorHistorialDepartamento(departamentoId)
                .stream()
                .map(SolicitudTramiteMapper::toResumen)
                .toList();
    }

    public SolicitudTramiteResponse crear(SolicitudTramiteRequest request, String solicitanteId) {
        SolicitudTramite solicitud = SolicitudTramiteMapper.toModel(request, solicitanteId);

        Tramite tramite = tramiteService.obtenerEntidad(request.getTramiteId());
        solicitud.setTramiteNombre(tramite.getNombre());
        solicitud.setSolicitanteNombre(usuarioService.obtenerPorId(solicitanteId).getNombre());

        SolicitudTramite guardada = solicitudTramiteRepository.save(solicitud);

        if (tramite.getFlujoTrabajoId() != null) {
            FlujoTrabajo flujo = flujoTrabajoService.obtenerEntidad(tramite.getFlujoTrabajoId());

            if (flujo.getEstadoFlujo() != EstadoFlujo.ACTIVO)
                throw new RuntimeException(
                        "El flujo de trabajo no está activo y no acepta nuevas solicitudes");

            Map<String, Object> variables = new HashMap<>();
            variables.put("solicitudId", guardada.getId());

            WorkflowResult resultado = workflowService.iniciarProceso(flujo.getProcesoKey(), variables);

            guardada.setProcessInstanceId(resultado.getProcessInstanceId());
            guardada.setVersionFlujoId(flujo.getVersionActualId());
            guardada.setEstado(EstadoTramite.EN_PROCESO);
            guardada.setDepartamentosActuales(resultado.getTareasActuales().stream()
                    .map(t -> t.getDepartamentoId()).toList());

            Map<String, String> formulariosPorElementId = obtenerFormulariosPorElementId(flujo.getVersionActualId());

            for (var tarea : resultado.getTareasActuales()) {
                guardada.getRespuestasPorDepartamento().add(
                        RespuestaDepartamento.builder()
                                .departamentoId(tarea.getDepartamentoId())
                                .departamentoNombre(departamentoService.obtenerPorId(tarea.getDepartamentoId()).getNombre())
                                .elementId(tarea.getElementId())
                                .formularioId(formulariosPorElementId.get(tarea.getElementId()))
                                .fechaEntrada(LocalDateTime.now())
                                .build());
            }

            guardada = solicitudTramiteRepository.save(guardada);
        }

        String deptoInicial = guardada.getRespuestasPorDepartamento().isEmpty() ? ""
                : " Está en revisión por " + guardada.getRespuestasPorDepartamento().get(0).getDepartamentoNombre() + ".";

        notificationService.enviar(NotificationPayload.builder()
                .usuarioId(guardada.getSolicitanteId())
                .titulo("Solicitud recibida")
                .cuerpo("Tu solicitud de " + guardada.getTramiteNombre() + " fue registrada." + deptoInicial)
                .solicitudId(guardada.getId())
                .build());

        return SolicitudTramiteMapper.toResponse(guardada);
    }

    public SolicitudTramiteResponse actualizar(String id, SolicitudTramiteRequest request) {
        SolicitudTramite existente = solicitudTramiteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada: " + id));

        existente.setRespuestasSolicitante(request.getRespuestas());
        existente.setAdjuntos(request.getAdjuntos());

        return SolicitudTramiteMapper.toResponse(solicitudTramiteRepository.save(existente));
    }

    public SolicitudTramiteResponse responderDepartamento(String id, RespuestaDepartamentoRequest request, String funcionarioId) {
        SolicitudTramite solicitud = solicitudTramiteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada: " + id));

        LocalDateTime ahora = LocalDateTime.now();

        RespuestaDepartamento entrada = solicitud.getRespuestasPorDepartamento().stream()
                .filter(r -> request.getElementId().equals(r.getElementId())
                        && r.getFechaRespuesta() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No hay tarea pendiente con elementId: " + request.getElementId()));

        if (entrada.getFuncionarioAsignadoId() == null) {
            throw new IllegalStateException(
                    "La tarea debe tomarse antes de responder");
        }
        if (!entrada.getFuncionarioAsignadoId().equals(funcionarioId)) {
            throw new IllegalStateException(
                    "La tarea está asignada a otro funcionario");
        }

        validarDocumentosObligatoriosDelNodo(solicitud, entrada.getElementId());

        entrada.setFuncionarioId(entrada.getFuncionarioAsignadoId());
        entrada.setFuncionarioNombre(entrada.getFuncionarioAsignadoNombre());
        entrada.setAccion(request.getAccion());
        entrada.setComentario(request.getComentario());
        entrada.setFechaRespuesta(ahora);
        entrada.setRespuestas(request.getRespuestas());

        if (solicitud.getProcessInstanceId() != null) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("accion", request.getAccion());

            WorkflowResult resultado = workflowService.completarTarea(
                    solicitud.getProcessInstanceId(), request.getDepartamentoId(), variables);

            if (resultado.isProcesoTerminado()) {
                solicitud.setDepartamentosActuales(new ArrayList<>());
                solicitud.setFechaFinalizacion(ahora);
                solicitud.setEstado(mapearEstadoFinal(resultado.getEstadoFinal()));
            } else {
                List<String> elementIdsPendientes = solicitud.getRespuestasPorDepartamento().stream()
                        .filter(r -> r.getFechaRespuesta() == null)
                        .map(RespuestaDepartamento::getElementId)
                        .toList();

                List<TareaActiva> nuevas = resultado.getTareasActuales().stream()
                        .filter(t -> !elementIdsPendientes.contains(t.getElementId()))
                        .toList();

                solicitud.setDepartamentosActuales(resultado.getTareasActuales().stream()
                        .map(TareaActiva::getDepartamentoId).toList());
                solicitud.setEstado(EstadoTramite.EN_PROCESO);

                Map<String, String> formulariosPorElementId = obtenerFormulariosPorElementId(solicitud.getVersionFlujoId());

                for (TareaActiva tarea : nuevas) {
                    solicitud.getRespuestasPorDepartamento().add(
                            RespuestaDepartamento.builder()
                                    .departamentoId(tarea.getDepartamentoId())
                                    .departamentoNombre(departamentoService.obtenerPorId(tarea.getDepartamentoId()).getNombre())
                                    .elementId(tarea.getElementId())
                                    .formularioId(formulariosPorElementId.get(tarea.getElementId()))
                                    .fechaEntrada(ahora)
                                    .build());
                }
            }
        }

        SolicitudTramite actualizada = solicitudTramiteRepository.save(solicitud);

        notificationService.enviar(NotificationPayload.builder()
                .usuarioId(actualizada.getSolicitanteId())
                .titulo(resolverTituloNotificacion(actualizada.getEstado()))
                .cuerpo(resolverCuerpoNotificacion(actualizada, entrada.getDepartamentoNombre()))
                .solicitudId(actualizada.getId())
                .build());

        return SolicitudTramiteMapper.toResponse(actualizada);
    }

    public void eliminar(String id) {
        SolicitudTramite solicitud = solicitudTramiteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada: " + id));

        if (solicitud.getProcessInstanceId() != null) {
            workflowService.cancelarProceso(solicitud.getProcessInstanceId(), "Solicitud eliminada: " + id);
        }

        solicitudTramiteRepository.deleteById(id);
    }

    public SolicitudTramiteResponse tomarTarea(String solicitudId, String elementId,
                                                 String usuarioId, String departamentoId) {
        String usuarioNombre = usuarioService.obtenerPorId(usuarioId).getNombre();

        Query q = Query.query(
                Criteria.where("_id").is(solicitudId)
                        .and("respuestasPorDepartamento").elemMatch(
                                Criteria.where("elementId").is(elementId)
                                        .and("departamentoId").is(departamentoId)
                                        .and("fechaRespuesta").is(null)
                                        .and("funcionarioAsignadoId").is(null)));

        Update u = new Update()
                .set("respuestasPorDepartamento.$.funcionarioAsignadoId", usuarioId)
                .set("respuestasPorDepartamento.$.funcionarioAsignadoNombre", usuarioNombre)
                .set("respuestasPorDepartamento.$.fechaAsignacion", LocalDateTime.now());

        SolicitudTramite actualizada = mongoTemplate.findAndModify(
                q, u, FindAndModifyOptions.options().returnNew(true), SolicitudTramite.class);

        if (actualizada == null) {
            boolean existe = solicitudTramiteRepository.existsById(solicitudId);
            if (!existe) {
                throw new RecursoNoEncontradoException("Solicitud no encontrada: " + solicitudId);
            }
            throw new IllegalStateException(
                    "La tarea ya fue tomada, no pertenece a tu departamento o no está disponible");
        }

        return SolicitudTramiteMapper.toResponse(actualizada);
    }

    public SolicitudTramiteResponse liberarTarea(String solicitudId, String elementId, String usuarioId) {
        Query q = Query.query(
                Criteria.where("_id").is(solicitudId)
                        .and("respuestasPorDepartamento").elemMatch(
                                Criteria.where("elementId").is(elementId)
                                        .and("fechaRespuesta").is(null)
                                        .and("funcionarioAsignadoId").is(usuarioId)));

        Update u = new Update()
                .set("respuestasPorDepartamento.$.funcionarioAsignadoId", null)
                .set("respuestasPorDepartamento.$.funcionarioAsignadoNombre", null)
                .set("respuestasPorDepartamento.$.fechaAsignacion", null);

        SolicitudTramite actualizada = mongoTemplate.findAndModify(
                q, u, FindAndModifyOptions.options().returnNew(true), SolicitudTramite.class);

        if (actualizada == null) {
            boolean existe = solicitudTramiteRepository.existsById(solicitudId);
            if (!existe) {
                throw new RecursoNoEncontradoException("Solicitud no encontrada: " + solicitudId);
            }
            throw new IllegalStateException(
                    "No puedes liberar una tarea que no tienes asignada");
        }

        return SolicitudTramiteMapper.toResponse(actualizada);
    }

    public List<TareaActivaResponse> obtenerTareasActivas(String solicitudId, String departamentoId) {
        SolicitudTramite solicitud = solicitudTramiteRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada: " + solicitudId));

        List<RespuestaDepartamento> pendientes = solicitud.getRespuestasPorDepartamento().stream()
                .filter(r -> departamentoId.equals(r.getDepartamentoId()) && r.getFechaRespuesta() == null)
                .toList();

        if (pendientes.isEmpty()) return List.of();

        VersionFlujoResponse version = flujoTrabajoService.obtenerVersionPorId(solicitud.getVersionFlujoId());

        return pendientes.stream().map(entrada -> {
            NodoFlujoResponse nodo = version.getNodos().stream()
                    .filter(n -> entrada.getElementId().equals(n.getElementId()))
                    .findFirst()
                    .orElseThrow(() -> new RecursoNoEncontradoException("Nodo no encontrado: " + entrada.getElementId()));

            List<CampoFormulario> campos = nodo.getCamposFormulario();
            if (campos == null && nodo.getFormularioId() != null) {
                campos = formularioTemplateService.obtenerPorId(nodo.getFormularioId()).getCampos();
            }

            return TareaActivaResponse.builder()
                    .elementId(entrada.getElementId())
                    .departamentoId(departamentoId)
                    .departamentoNombre(entrada.getDepartamentoNombre())
                    .campos(campos)
                    .acciones(resolverAcciones(nodo, version))
                    .build();
        }).toList();
    }

    private List<AccionDisponibleResponse> resolverAcciones(NodoFlujoResponse nodo, VersionFlujoResponse version) {
        List<TransicionResponse> transiciones = nodo.getTransiciones();

        if (transiciones.size() == 1) {
            String targetId = transiciones.get(0).getTargetId();
            transiciones = version.getNodos().stream()
                    .filter(n -> targetId.equals(n.getElementId()) && "exclusiveGateway".equals(n.getTipo()))
                    .findFirst()
                    .map(NodoFlujoResponse::getTransiciones)
                    .orElse(transiciones);
        }

        return transiciones.stream()
                .filter(t -> t.getValor() != null)
                .map(t -> AccionDisponibleResponse.builder()
                        .etiqueta(t.getEtiqueta())
                        .valor(t.getValor())
                        .build())
                .toList();
    }

    private void validarDocumentosObligatoriosDelNodo(SolicitudTramite solicitud, String elementId) {
        if (solicitud.getVersionFlujoId() == null) return;

        com.jairo.workflowtramites.model.VersionFlujo version = versionFlujoRepository.findById(solicitud.getVersionFlujoId()).orElse(null);
        if (version == null || version.getNodos() == null) return;

        com.jairo.workflowtramites.model.embeds.NodoFlujo nodo = version.getNodos().stream()
                .filter(n -> elementId.equals(n.getElementId()))
                .findFirst()
                .orElse(null);

        if (nodo == null || nodo.getConfiguracionDocumental() == null) return;

        var documentosProducidos = nodo.getConfiguracionDocumental().getDocumentosProducidos();
        if (documentosProducidos == null || documentosProducidos.isEmpty()) return;

        List<String> faltantes = new ArrayList<>();
        for (var docConfig : documentosProducidos) {
            if (!docConfig.isObligatorio()) continue;
            String campo = docConfig.getCampoFormularioAsociado();
            if (campo == null) continue;

            boolean existe = !archivoRepository.findBySolicitudIdAndCampoFormularioOrigenAndEstado(
                    solicitud.getId(), campo, com.jairo.workflowtramites.model.Archivo.EstadoArchivo.ACTIVO
            ).isEmpty();

            if (!existe) {
                faltantes.add(docConfig.getNombre());
            }
        }

        if (!faltantes.isEmpty()) {
            throw new RuntimeException("Faltan documentos obligatorios para completar este paso: " + String.join(", ", faltantes));
        }

        for (var docConfig : documentosProducidos) {
            if (!docConfig.isInmutablePostCierre()) continue;
            String campo = docConfig.getCampoFormularioAsociado();
            if (campo == null) continue;

            archivoRepository.findBySolicitudIdAndCampoFormularioOrigenAndEstado(
                    solicitud.getId(), campo, com.jairo.workflowtramites.model.Archivo.EstadoArchivo.ACTIVO
            ).forEach(a -> {
                a.setInmutable(true);
                archivoRepository.save(a);
            });
        }
    }

    private Map<String, String> obtenerFormulariosPorElementId(String versionId) {
        return flujoTrabajoService.obtenerVersionPorId(versionId)
                .getNodos().stream()
                .filter(n -> n.getElementId() != null && n.getFormularioId() != null)
                .collect(Collectors.toMap(NodoFlujoResponse::getElementId, NodoFlujoResponse::getFormularioId, (a, b) -> a));
    }

    private EstadoTramite mapearEstadoFinal(String nombreEndEvent) {
        if (nombreEndEvent == null) throw new RuntimeException("El proceso terminó sin un end event nombrado");
        return switch (nombreEndEvent.toLowerCase()) {
            case "aprobado"  -> EstadoTramite.APROBADO;
            case "rechazado" -> EstadoTramite.RECHAZADO;
            case "cancelado" -> EstadoTramite.CANCELADO;
            default -> throw new RuntimeException("Estado final desconocido: " + nombreEndEvent);
        };
    }

    private String resolverTituloNotificacion(EstadoTramite estado) {
        return switch (estado) {
            case APROBADO  -> "¡Trámite aprobado!";
            case RECHAZADO -> "Trámite rechazado";
            default        -> "Trámite en proceso";
        };
    }

    private String resolverCuerpoNotificacion(SolicitudTramite solicitud, String departamentoQueRespondio) {
        String tramite = solicitud.getTramiteNombre();
        return switch (solicitud.getEstado()) {
            case APROBADO  -> "Tu solicitud de " + tramite + " fue aprobada. Ingresa a la app para ver el detalle.";
            case RECHAZADO -> "Tu solicitud de " + tramite + " fue rechazada. Ingresa a la app para ver los comentarios.";
            default -> {
                List<String> siguientes = solicitud.getRespuestasPorDepartamento().stream()
                        .filter(r -> r.getFechaRespuesta() == null)
                        .map(RespuestaDepartamento::getDepartamentoNombre)
                        .toList();
                String siguienteTexto = siguientes.isEmpty() ? ""
                        : " Ahora está en revisión por " + String.join(" y ", siguientes) + ".";
                yield "Tu solicitud de " + tramite + " fue procesada por " + departamentoQueRespondio + "." + siguienteTexto;
            }
        };
    }
}
