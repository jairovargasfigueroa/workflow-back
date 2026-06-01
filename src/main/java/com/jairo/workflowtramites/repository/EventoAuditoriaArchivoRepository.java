package com.jairo.workflowtramites.repository;

import com.jairo.workflowtramites.model.EventoAuditoriaArchivo;
import com.jairo.workflowtramites.model.enums.TipoEventoArchivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoAuditoriaArchivoRepository extends MongoRepository<EventoAuditoriaArchivo, String> {

    List<EventoAuditoriaArchivo> findByArchivoIdOrderByFechaDesc(String archivoId);

    List<EventoAuditoriaArchivo> findBySolicitudIdOrderByFechaDesc(String solicitudId);

    @Query("{ $and: [ " +
            "?#{ [0] == null ? { $where: 'true' } : { 'archivoId': [0] } }, " +
            "?#{ [1] == null ? { $where: 'true' } : { 'solicitudId': [1] } }, " +
            "?#{ [2] == null ? { $where: 'true' } : { 'usuarioId': [2] } }, " +
            "?#{ [3] == null ? { $where: 'true' } : { 'tipo': [3] } }, " +
            "?#{ [4] == null ? { $where: 'true' } : { 'fecha': { $gte: [4] } } }, " +
            "?#{ [5] == null ? { $where: 'true' } : { 'fecha': { $lte: [5] } } } " +
            "] }")
    Page<EventoAuditoriaArchivo> buscarConFiltros(String archivoId,
                                                  String solicitudId,
                                                  String usuarioId,
                                                  TipoEventoArchivo tipo,
                                                  LocalDateTime desde,
                                                  LocalDateTime hasta,
                                                  Pageable pageable);
}
