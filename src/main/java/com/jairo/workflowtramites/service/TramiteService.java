package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.request.TramiteRequest;
import com.jairo.workflowtramites.dto.response.FormularioTemplateResponse;
import com.jairo.workflowtramites.dto.response.TramiteDisponibleResponse;
import com.jairo.workflowtramites.dto.response.TramiteResponse;
import com.jairo.workflowtramites.mapper.TramiteMapper;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.repository.TramiteRepository;
import com.jairo.workflowtramites.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TramiteService {

    private final TramiteRepository tramiteRepository;
    private final FormularioTemplateService formularioTemplateService;

    public List<TramiteResponse> listar() {
        return tramiteRepository.findAll()
                .stream()
                .map(TramiteMapper::toResponse)
                .toList();
    }

    public TramiteResponse obtenerPorId(String id) {
        return TramiteMapper.toResponse(obtenerEntidad(id));
    }

    public Tramite obtenerEntidad(String id) {
        return tramiteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Trámite no encontrado: " + id));
    }

    public TramiteResponse crear(TramiteRequest request) {
        return TramiteMapper.toResponse(tramiteRepository.save(TramiteMapper.toModel(request)));
    }

    public TramiteResponse actualizar(String id, TramiteRequest request) {
        Tramite existente = tramiteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Trámite no encontrado: " + id));

        existente.setNombre(request.getNombre());
        existente.setDescripcion(request.getDescripcion());
        existente.setFormularioSolicitanteId(request.getFormularioSolicitanteId());
        existente.setFlujoTrabajoId(request.getFlujoTrabajoId());
        existente.setRequisitos(request.getRequisitos());
        if (request.getEtiquetas() != null) {
            existente.setEtiquetas(request.getEtiquetas());
        }

        return TramiteMapper.toResponse(tramiteRepository.save(existente));
    }

    public void eliminar(String id) {
        tramiteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Trámite no encontrado: " + id));
        tramiteRepository.deleteById(id);
    }

    public List<TramiteDisponibleResponse> listarDisponibles() {
        return tramiteRepository.findByActivoTrue()
                .stream()
                .map(t -> TramiteDisponibleResponse.builder()
                        .id(t.getId())
                        .nombre(t.getNombre())
                        .descripcion(t.getDescripcion())
                        .requisitos(t.getRequisitos())
                        .etiquetas(t.getEtiquetas())
                        .formularioSolicitanteId(t.getFormularioSolicitanteId())
                        .build())
                .toList();
    }

    public FormularioTemplateResponse obtenerFormularioSolicitante(String tramiteId) {
        Tramite tramite = tramiteRepository.findById(tramiteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Trámite no encontrado: " + tramiteId));
        if (!tramite.isActivo()) {
            throw new RuntimeException("Trámite no está activo: " + tramiteId);
        }
        if (tramite.getFormularioSolicitanteId() == null) {
            throw new RecursoNoEncontradoException("Trámite no tiene formulario asociado: " + tramiteId);
        }
        return formularioTemplateService.obtenerPorId(tramite.getFormularioSolicitanteId());
    }
}
