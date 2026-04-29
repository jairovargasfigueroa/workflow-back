package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.request.UsuarioRequest;
import com.jairo.workflowtramites.dto.response.UsuarioResponse;
import com.jairo.workflowtramites.mapper.UsuarioMapper;
import com.jairo.workflowtramites.model.Usuario;
import com.jairo.workflowtramites.repository.UsuarioRepository;
import com.jairo.workflowtramites.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    public UsuarioResponse obtenerPorId(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
        return UsuarioMapper.toResponse(usuario);
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + request.getEmail());
        }
        Usuario usuario = UsuarioMapper.toModel(request);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        return UsuarioMapper.toResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponse actualizar(String id, UsuarioRequest request) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));

        existente.setNombre(request.getNombre());
        existente.setEmail(request.getEmail());
        existente.setPassword(passwordEncoder.encode(request.getPassword()));
        existente.setRol(request.getRol());
        existente.setDepartamentoId(request.getDepartamentoId());
        existente.setTelefono(request.getTelefono());
        existente.setDireccion(request.getDireccion());
        existente.setCedula(request.getCedula());

        return UsuarioMapper.toResponse(usuarioRepository.save(existente));
    }

    public void actualizarFcmToken(String id, String token) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
        usuario.setFcmToken(token);
        usuarioRepository.save(usuario);
    }

    public void eliminar(String id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
        usuarioRepository.deleteById(id);
    }
}
