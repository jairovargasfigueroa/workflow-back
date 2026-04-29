package com.jairo.workflowtramites.repository;

import com.jairo.workflowtramites.model.VersionFlujo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VersionFlujoRepository extends MongoRepository<VersionFlujo, String> {

    List<VersionFlujo> findByFlujoIdOrderByNumeroAsc(String flujoId);

    Optional<VersionFlujo> findByFlujoIdAndNumero(String flujoId, int numero);
}
