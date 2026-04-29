package com.jairo.workflowtramites.repository;

import com.jairo.workflowtramites.model.Departamento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DepartamentoRepository extends MongoRepository<Departamento, String> {

    Optional<Departamento> findByNombre(String nombre);
}
