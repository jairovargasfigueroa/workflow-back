package com.jairo.workflowtramites.repository;

import com.jairo.workflowtramites.model.Tramite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TramiteRepository extends MongoRepository<Tramite, String> {

    List<Tramite> findByActivoTrue();
}
