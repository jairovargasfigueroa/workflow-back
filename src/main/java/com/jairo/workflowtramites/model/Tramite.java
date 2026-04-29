package com.jairo.workflowtramites.model;

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

    private boolean activo;
}
