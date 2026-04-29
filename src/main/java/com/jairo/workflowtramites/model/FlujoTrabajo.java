package com.jairo.workflowtramites.model;

import com.jairo.workflowtramites.model.enums.EstadoFlujo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "flujos_trabajo")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlujoTrabajo extends AuditableDocument {

    @Id
    private String id;
    private String nombre;
    private String descripcion;

    @Indexed(unique = true)
    private String procesoKey;

    private EstadoFlujo estadoFlujo;
    private String xmlBorrador;
    private LocalDateTime borradorActualizacion;
    private String versionActualId;
    private Integer versionActualNumero;
}
