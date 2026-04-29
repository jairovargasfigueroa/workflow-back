package com.jairo.workflowtramites.model;

import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "formulario_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormularioTemplate extends AuditableDocument {

    @Id
    private String id;
    private String titulo;
    private String descripcion;

    @Builder.Default
    private List<CampoFormulario> campos = new ArrayList<>();

    private boolean activo;
}
