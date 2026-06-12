package com.jairo.workflowtramites.repository;

import com.jairo.workflowtramites.model.Archivo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ArchivoRepository extends MongoRepository<Archivo, String> {

    /** Idempotencia: busca un archivo por el clientId que mando el cliente (movil offline).
     *  findFirst para tolerar duplicados historicos sin romper con "non unique". */
    Optional<Archivo> findFirstByClientId(String clientId);

    List<Archivo> findBySolicitudIdAndEstadoNot(String solicitudId, Archivo.EstadoArchivo estado);

    List<Archivo> findByEstado(Archivo.EstadoArchivo estado);

    List<Archivo> findByClienteIdAndEstadoNot(String clienteId, Archivo.EstadoArchivo estado);

    List<Archivo> findByPoliticaIdAndEstadoNot(String politicaId, Archivo.EstadoArchivo estado);

    List<Archivo> findByLinajeIdOrderByVersionAsc(String linajeId);

    List<Archivo> findBySolicitudIdAndCampoFormularioOrigenAndEstado(
            String solicitudId, String campoFormularioOrigen, Archivo.EstadoArchivo estado);
}
