package com.jairo.workflowtramites.model;

import com.jairo.workflowtramites.model.embeds.NodoFlujo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "flujos_versiones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionFlujo extends AuditableDocument {

    @Id
    private String id;

    @Indexed
    private String flujoId;

    private int numero;
    private String xml;
    private String camundaDespliegueId;

    private List<NodoFlujo> nodos;
}
