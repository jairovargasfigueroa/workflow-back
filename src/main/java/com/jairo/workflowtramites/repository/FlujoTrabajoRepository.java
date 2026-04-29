package com.jairo.workflowtramites.repository;

import com.jairo.workflowtramites.model.FlujoTrabajo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FlujoTrabajoRepository extends MongoRepository<FlujoTrabajo, String> {

    Optional<FlujoTrabajo> findByProcesoKey(String procesoKey);

    boolean existsByProcesoKey(String procesoKey);

    boolean existsByNombre(String nombre);

    List<FlujoTrabajo> findAllByOrderByFechaCreacionAsc();
}
