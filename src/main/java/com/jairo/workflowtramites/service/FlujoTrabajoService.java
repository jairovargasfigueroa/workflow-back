package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.request.FlujoTrabajoRequest;
import com.jairo.workflowtramites.dto.response.FlujoTrabajoResponse;
import com.jairo.workflowtramites.dto.response.VersionFlujoDetalleResponse;
import com.jairo.workflowtramites.dto.response.VersionFlujoResumen;
import com.jairo.workflowtramites.dto.response.VersionFlujoResponse;
import com.jairo.workflowtramites.exception.ValidacionBpmnException;
import com.jairo.workflowtramites.mapper.FlujoTrabajoMapper;
import com.jairo.workflowtramites.mapper.VersionFlujoMapper;
import com.jairo.workflowtramites.model.FlujoTrabajo;
import com.jairo.workflowtramites.model.FormularioTemplate;
import com.jairo.workflowtramites.model.VersionFlujo;
import com.jairo.workflowtramites.model.embeds.NodoFlujo;
import com.jairo.workflowtramites.model.enums.EstadoFlujo;
import com.jairo.workflowtramites.repository.FlujoTrabajoRepository;
import com.jairo.workflowtramites.repository.FormularioTemplateRepository;
import com.jairo.workflowtramites.repository.VersionFlujoRepository;
import com.jairo.workflowtramites.exception.RecursoNoEncontradoException;
import com.jairo.workflowtramites.service.workflow.WorkflowService;
import com.jairo.workflowtramites.util.ProcesoKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlujoTrabajoService {

    private final FlujoTrabajoRepository flujoTrabajoRepository;
    private final VersionFlujoRepository versionFlujoRepository;
    private final FormularioTemplateRepository formularioTemplateRepository;
    private final WorkflowService workflowService;
    private final BpmnParserService bpmnParserService;

    public List<FlujoTrabajoResponse> listar() {
        return flujoTrabajoRepository.findAll()
                .stream()
                .map(FlujoTrabajoMapper::toResponse)
                .toList();
    }

    public FlujoTrabajoResponse obtenerPorId(String id) {
        return FlujoTrabajoMapper.toResponse(obtenerEntidad(id));
    }

    public FlujoTrabajo obtenerEntidad(String id) {
        return flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));
    }

    public FlujoTrabajoResponse crear(FlujoTrabajoRequest request) {
        if (flujoTrabajoRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe un flujo con el nombre: " + request.getNombre());
        }
        String procesoKey = ProcesoKeyUtil.generar(request.getNombre());
        if (flujoTrabajoRepository.existsByProcesoKey(procesoKey)) {
            throw new RuntimeException("Ya existe un flujo con clave de proceso equivalente: " + procesoKey);
        }
        FlujoTrabajo guardado = flujoTrabajoRepository.save(
                FlujoTrabajoMapper.toModel(request, procesoKey));
        return FlujoTrabajoMapper.toResponse(guardado);
    }

    public FlujoTrabajoResponse actualizar(String id, FlujoTrabajoRequest request) {
        FlujoTrabajo existente = flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));

        // Si renombra, validar que el nombre nuevo no colisione con otro flujo distinto
        if (!existente.getNombre().equals(request.getNombre())
                && flujoTrabajoRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe un flujo con el nombre: " + request.getNombre());
        }

        existente.setNombre(request.getNombre());
        existente.setDescripcion(request.getDescripcion());
        // procesoKey es inmutable: no se modifica aunque cambie el nombre.

        return FlujoTrabajoMapper.toResponse(flujoTrabajoRepository.save(existente));
    }

    public void eliminar(String id) {
        flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));
        flujoTrabajoRepository.deleteById(id);
    }

    public FlujoTrabajoResponse guardarBorrador(String id, String xml) {
        FlujoTrabajo flujo = flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));

        if (flujo.getEstadoFlujo() == EstadoFlujo.ARCHIVADO)
            throw new RuntimeException("No se puede editar un flujo archivado");

        flujo.setXmlBorrador(xml);
        flujo.setBorradorActualizacion(LocalDateTime.now());

        return FlujoTrabajoMapper.toResponse(flujoTrabajoRepository.save(flujo));
    }

    public FlujoTrabajoResponse publicar(String id) {
        FlujoTrabajo flujo = flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));

        if (flujo.getEstadoFlujo() == EstadoFlujo.ARCHIVADO)
            throw new RuntimeException("No se puede publicar un flujo archivado");

        if (flujo.getXmlBorrador() == null || flujo.getXmlBorrador().isBlank())
            throw new RuntimeException("No hay borrador para publicar");

        String xmlConCondiciones = bpmnParserService.inyectarCondiciones(flujo.getXmlBorrador());
        String xmlFinal = bpmnParserService.inyectarCandidateGroupsDesdeLanes(xmlConCondiciones);

        List<String> errores = bpmnParserService.validar(xmlFinal);
        if (!errores.isEmpty())
            throw new ValidacionBpmnException(errores);

        String despliegueId = workflowService.desplegarProceso(
                flujo.getProcesoKey(), flujo.getNombre(), xmlFinal);

        List<NodoFlujo> nodos = bpmnParserService.parsear(xmlFinal);

        for (NodoFlujo nodo : nodos) {
            if ("userTask".equals(nodo.getTipo()) && nodo.getFormularioId() != null) {
                FormularioTemplate tpl = formularioTemplateRepository.findById(nodo.getFormularioId())
                        .orElseThrow(() -> new ValidacionBpmnException(List.of(
                                "El formulario '" + nodo.getFormularioId() +
                                "' referenciado por el nodo '" + nodo.getElementId() + "' no existe")));
                nodo.setCamposFormulario(tpl.getCampos() != null ? new ArrayList<>(tpl.getCampos()) : new ArrayList<>());
            }
        }

        int nuevoNumero = flujo.getVersionActualNumero() != null ? flujo.getVersionActualNumero() + 1 : 1;

        VersionFlujo nuevaVersion = VersionFlujo.builder()
                .flujoId(id)
                .numero(nuevoNumero)
                .xml(flujo.getXmlBorrador())
                .camundaDespliegueId(despliegueId)
                .nodos(nodos)
                .build();

        VersionFlujo versionGuardada = versionFlujoRepository.save(nuevaVersion);

        flujo.setVersionActualId(versionGuardada.getId());
        flujo.setVersionActualNumero(nuevoNumero);
        flujo.setEstadoFlujo(EstadoFlujo.ACTIVO);
        flujo.setXmlBorrador(null);
        flujo.setBorradorActualizacion(null);

        return FlujoTrabajoMapper.toResponse(flujoTrabajoRepository.save(flujo));
    }

    public FlujoTrabajoResponse cambiarEstado(String id, EstadoFlujo nuevoEstado) {
        FlujoTrabajo flujo = flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));

        EstadoFlujo estadoActual = flujo.getEstadoFlujo();
        boolean transicionValida = switch (estadoActual) {
            case SIN_PUBLICAR -> false;
            case ACTIVO -> nuevoEstado == EstadoFlujo.DESACTIVADO;
            case DESACTIVADO -> nuevoEstado == EstadoFlujo.ACTIVO || nuevoEstado == EstadoFlujo.ARCHIVADO;
            case ARCHIVADO -> false;
        };

        if (!transicionValida)
            throw new RuntimeException(
                    "Transición de estado no permitida: " + estadoActual + " → " + nuevoEstado);

        flujo.setEstadoFlujo(nuevoEstado);

        return FlujoTrabajoMapper.toResponse(flujoTrabajoRepository.save(flujo));
    }

    public FlujoTrabajoResponse copiarVersionComoBorrador(String id, int numeroVersion) {
        FlujoTrabajo flujo = flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));

        if (flujo.getEstadoFlujo() == EstadoFlujo.ARCHIVADO)
            throw new RuntimeException("No se puede editar un flujo archivado");

        VersionFlujo version = versionFlujoRepository.findByFlujoIdAndNumero(id, numeroVersion)
                .orElseThrow(() -> new RuntimeException(
                        "Versión " + numeroVersion + " no encontrada para el flujo: " + id));

        flujo.setXmlBorrador(version.getXml());
        flujo.setBorradorActualizacion(LocalDateTime.now());

        return FlujoTrabajoMapper.toResponse(flujoTrabajoRepository.save(flujo));
    }

    public List<VersionFlujoResumen> listarVersiones(String id) {
        flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));
        return versionFlujoRepository.findByFlujoIdOrderByNumeroAsc(id)
                .stream()
                .map(VersionFlujoMapper::toResumen)
                .toList();
    }

    public VersionFlujoDetalleResponse obtenerVersion(String id, int numeroVersion) {
        flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));
        VersionFlujo version = versionFlujoRepository.findByFlujoIdAndNumero(id, numeroVersion)
                .orElseThrow(() -> new RuntimeException(
                        "Versión " + numeroVersion + " no encontrada para el flujo: " + id));
        return VersionFlujoMapper.toDetalle(version);
    }

    public VersionFlujoResponse obtenerVersionPorId(String versionId) {
        VersionFlujo version = versionFlujoRepository.findById(versionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión no encontrada: " + versionId));
        return VersionFlujoMapper.toResponse(version);
    }

    public FlujoTrabajoResponse desplegar(String id, String xml) {
        guardarBorrador(id, xml);
        return publicar(id);
    }

    public FlujoTrabajoResponse descartarBorrador(String id) {
        FlujoTrabajo flujo = flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));

        if (flujo.getEstadoFlujo() == EstadoFlujo.ARCHIVADO)
            throw new RuntimeException("No se puede editar un flujo archivado");

        flujo.setXmlBorrador(null);
        flujo.setBorradorActualizacion(null);

        return FlujoTrabajoMapper.toResponse(flujoTrabajoRepository.save(flujo));
    }

    public String obtenerXml(String id) {
        FlujoTrabajo flujo = flujoTrabajoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Flujo de trabajo no encontrado: " + id));
        if (flujo.getXmlBorrador() != null && !flujo.getXmlBorrador().isBlank())
            return flujo.getXmlBorrador();
        if (flujo.getVersionActualId() != null)
            return versionFlujoRepository.findById(flujo.getVersionActualId())
                    .map(VersionFlujo::getXml)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Versión activa no encontrada: " + flujo.getVersionActualId()));
        throw new RuntimeException("Este flujo no tiene XML disponible");
    }
}
