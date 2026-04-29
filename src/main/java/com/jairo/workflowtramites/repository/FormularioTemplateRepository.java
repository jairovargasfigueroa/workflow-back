package com.jairo.workflowtramites.repository;

import com.jairo.workflowtramites.model.FormularioTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FormularioTemplateRepository extends MongoRepository<FormularioTemplate, String> {

    List<FormularioTemplate> findByActivoTrue();

    List<FormularioTemplate> findAllByOrderByFechaCreacionAsc();

    Optional<FormularioTemplate> findByTitulo(String titulo);
}
