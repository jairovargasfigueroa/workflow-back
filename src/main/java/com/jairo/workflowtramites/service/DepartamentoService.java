package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.request.DepartamentoRequest;
import com.jairo.workflowtramites.dto.response.DepartamentoResponse;
import com.jairo.workflowtramites.mapper.DepartamentoMapper;
import com.jairo.workflowtramites.model.Departamento;
import com.jairo.workflowtramites.repository.DepartamentoRepository;
import com.jairo.workflowtramites.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public List<DepartamentoResponse> listar() {
        return departamentoRepository.findAll()
                .stream()
                .map(DepartamentoMapper::toResponse)
                .toList();
    }

    public DepartamentoResponse obtenerPorId(String id) {
        Departamento dept = departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado: " + id));
        return DepartamentoMapper.toResponse(dept);
    }

    public DepartamentoResponse crear(DepartamentoRequest request) {
        Departamento guardado = departamentoRepository.save(DepartamentoMapper.toModel(request));
        return DepartamentoMapper.toResponse(guardado);
    }

    public DepartamentoResponse actualizar(String id, DepartamentoRequest request) {
        Departamento existente = departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado: " + id));
        existente.setNombre(request.getNombre());
        return DepartamentoMapper.toResponse(departamentoRepository.save(existente));
    }

    public void eliminar(String id) {
        departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado: " + id));
        departamentoRepository.deleteById(id);
    }
}
