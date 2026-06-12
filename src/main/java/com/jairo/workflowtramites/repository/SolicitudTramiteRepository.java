package com.jairo.workflowtramites.repository;

import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SolicitudTramiteRepository extends MongoRepository<SolicitudTramite, String> {

    /** Idempotencia: busca una solicitud por el clientId que mando el cliente (movil offline).
     *  findFirst (no findBy) para tolerar duplicados historicos sin romper con "non unique". */
    Optional<SolicitudTramite> findFirstByClientId(String clientId);

    List<SolicitudTramite> findByTramiteId(String tramiteId);

    List<SolicitudTramite> findBySolicitanteId(String solicitanteId);

    List<SolicitudTramite> findByDepartamentosActualesContaining(String departamentoId);

    List<SolicitudTramite> findByDepartamentosActualesContainingAndEstado(String departamentoId, EstadoTramite estado);

    @Query(value = "{ 'respuestasPorDepartamento': { $elemMatch: { " +
            "'departamentoId': ?0, 'fechaRespuesta': null, 'funcionarioAsignadoId': null } } }",
            sort = "{ 'fechaCreacion': 1 }")
    List<SolicitudTramite> findPendientesSinAsignar(String departamentoId);

    @Query(value = "{ 'respuestasPorDepartamento': { $elemMatch: { " +
            "'funcionarioAsignadoId': ?0, 'fechaRespuesta': null } } }",
            sort = "{ 'fechaCreacion': -1 }")
    List<SolicitudTramite> findMisTareasTomadas(String usuarioId);

    @Query(value = "{ 'respuestasPorDepartamento.departamentoId': ?0 }",
            sort = "{ 'fechaCreacion': -1 }")
    List<SolicitudTramite> findPorHistorialDepartamento(String departamentoId);
}
