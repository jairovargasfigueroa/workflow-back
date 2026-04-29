package com.jairo.workflowtramites.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "departamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Departamento extends AuditableDocument {

    @Id
    private String id;
    private String nombre;
    private boolean activo;
}
