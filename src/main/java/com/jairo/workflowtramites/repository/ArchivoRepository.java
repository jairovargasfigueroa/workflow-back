package com.jairo.workflowtramites.repository;

import com.jairo.workflowtramites.model.Archivo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ArchivoRepository extends MongoRepository<Archivo, String> {

    List<Archivo> findBySolicitudIdAndEstadoNot(String solicitudId, Archivo.EstadoArchivo estado);

    List<Archivo> findByEstado(Archivo.EstadoArchivo estado);

    List<Archivo> findByClienteIdAndEstadoNot(String clienteId, Archivo.EstadoArchivo estado);

    List<Archivo> findByPoliticaIdAndEstadoNot(String politicaId, Archivo.EstadoArchivo estado);

    List<Archivo> findByLinajeIdOrderByVersionAsc(String linajeId);

    List<Archivo> findBySolicitudIdAndCampoFormularioOrigenAndEstado(
            String solicitudId, String campoFormularioOrigen, Archivo.EstadoArchivo estado);
}
