package com.jairo.workflowtramites.model;

import com.jairo.workflowtramites.model.enums.TipoEventoArchivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "auditoria_archivos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoAuditoriaArchivo {

    @Id
    private String id;

    @Indexed
    private String archivoId;

    @Indexed
    private String solicitudId;

    @Indexed
    private String usuarioId;

    private String usuarioNombre;
    private String usuarioRol;
    private String usuarioDepartamentoId;

    private TipoEventoArchivo tipo;

    @Indexed
    private LocalDateTime fecha;

    private String detalle;
    private String motivoDenegacion;
}
