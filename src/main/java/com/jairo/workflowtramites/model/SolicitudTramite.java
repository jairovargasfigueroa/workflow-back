package com.jairo.workflowtramites.model;

import com.jairo.workflowtramites.model.embeds.Adjunto;
import com.jairo.workflowtramites.model.embeds.RespuestaCampo;
import com.jairo.workflowtramites.model.embeds.RespuestaDepartamento;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "solicitudes")
@CompoundIndexes({
        @CompoundIndex(name = "idx_depto_pendiente_asignado",
                def = "{'respuestasPorDepartamento.departamentoId': 1, " +
                      "'respuestasPorDepartamento.fechaRespuesta': 1, " +
                      "'respuestasPorDepartamento.funcionarioAsignadoId': 1}"),
        @CompoundIndex(name = "idx_asignado_pendiente",
                def = "{'respuestasPorDepartamento.funcionarioAsignadoId': 1, " +
                      "'respuestasPorDepartamento.fechaRespuesta': 1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudTramite extends AuditableDocument {

    @Id
    private String id;
    private String tramiteId;
    private String tramiteNombre;
    private String solicitanteId;
    private String solicitanteNombre;
    private EstadoTramite estado;
    @Builder.Default
    private List<String> departamentosActuales = new ArrayList<>();
    private String processInstanceId;
    private String versionFlujoId;
    private LocalDateTime fechaFinalizacion;

    @Builder.Default
    private List<RespuestaCampo> respuestasSolicitante = new ArrayList<>();

    @Builder.Default
    private List<RespuestaDepartamento> respuestasPorDepartamento = new ArrayList<>();

    @Builder.Default
    private List<Adjunto> adjuntos = new ArrayList<>();
}
