package com.jairo.workflowtramites.model;

import com.jairo.workflowtramites.model.enums.CategoriaCriticidad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "tramites")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tramite extends AuditableDocument {

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String formularioSolicitanteId;
    private String flujoTrabajoId;

    @Builder.Default
    private List<String> requisitos = new ArrayList<>();

    @Builder.Default
    private List<String> etiquetas = new ArrayList<>();

    private boolean activo;

    // SLA del trámite (compromiso con el solicitante - patrón ITIL)
    private Integer plazoObjetivoHoras;
    private Integer plazoMaximoHoras;
    private Integer umbralAlertaPorcentaje;
    private CategoriaCriticidad criticidad;
}
