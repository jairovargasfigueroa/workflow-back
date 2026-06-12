package com.jairo.workflowtramites.model;

import com.jairo.workflowtramites.model.embeds.PermisoSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "archivos")
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Archivo extends AuditableDocument {

    @Id
    private String id;

    @Indexed
    private String solicitudId;

    private String politicaId;

    @Indexed
    private String clienteId;

    // Idempotencia: UUID del cliente (movil offline) para deduplicar reintentos de subida.
    @Indexed
    private String clientId;

    private String nombre;
    private String formato;
    private Long tamanoBytes;
    private String contentType;
    private String s3Key;

    private String subidoPor;
    private String subidoPorNombre;
    private LocalDateTime fechaSubida;

    private String departamentoOrigenId;
    private String campoFormularioOrigen;
    private String nodoElementId;

    @Builder.Default
    private Integer version = 1;

    private String versionAnteriorId;

    @Indexed
    private String linajeId;

    @Builder.Default
    private EstadoArchivo estado = EstadoArchivo.ACTIVO;

    private String modificadoPor;
    private LocalDateTime fechaModificacion;

    private String eliminadoPor;
    private LocalDateTime fechaEliminacion;

    @Builder.Default
    private PermisoSet permisos = PermisoSet.builder().build();

    @Builder.Default
    private boolean inmutable = false;

    public enum EstadoArchivo {
        ACTIVO,
        REEMPLAZADO,
        ELIMINADO
    }
}
