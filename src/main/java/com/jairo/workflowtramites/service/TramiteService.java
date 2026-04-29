package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.request.TramiteRequest;
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

        return TramiteMapper.toResponse(tramiteRepository.save(existente));
    }

    public void eliminar(String id) {
        tramiteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Trámite no encontrado: " + id));
        tramiteRepository.deleteById(id);
    }
}
