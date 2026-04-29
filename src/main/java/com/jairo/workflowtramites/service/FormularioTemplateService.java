package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.request.FormularioTemplateRequest;
import com.jairo.workflowtramites.dto.response.FormularioTemplateResponse;
import com.jairo.workflowtramites.mapper.FormularioTemplateMapper;
import com.jairo.workflowtramites.model.FormularioTemplate;
import com.jairo.workflowtramites.repository.FormularioTemplateRepository;
import com.jairo.workflowtramites.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormularioTemplateService {

    private final FormularioTemplateRepository formularioTemplateRepository;

    public List<FormularioTemplateResponse> listar() {
        return formularioTemplateRepository.findAll()
                .stream()
                .map(FormularioTemplateMapper::toResponse)
                .toList();
    }

    public FormularioTemplateResponse obtenerPorId(String id) {
        FormularioTemplate formulario = formularioTemplateRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Formulario no encontrado: " + id));
        return FormularioTemplateMapper.toResponse(formulario);
    }

    public FormularioTemplateResponse crear(FormularioTemplateRequest request) {
        FormularioTemplate guardado = formularioTemplateRepository.save(
                FormularioTemplateMapper.toModel(request));
        return FormularioTemplateMapper.toResponse(guardado);
    }

    public FormularioTemplateResponse actualizar(String id, FormularioTemplateRequest request) {
        FormularioTemplate existente = formularioTemplateRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Formulario no encontrado: " + id));

        existente.setTitulo(request.getTitulo());
        existente.setDescripcion(request.getDescripcion());
        existente.setCampos(request.getCampos());

        return FormularioTemplateMapper.toResponse(formularioTemplateRepository.save(existente));
    }

    public void eliminar(String id) {
        formularioTemplateRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Formulario no encontrado: " + id));
        formularioTemplateRepository.deleteById(id);
    }
}
