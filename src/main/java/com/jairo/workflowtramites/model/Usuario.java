package com.jairo.workflowtramites.model;

import com.jairo.workflowtramites.model.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario extends AuditableDocument {

    @Id
    private String id;
    private String nombre;

    @Indexed(unique = true)
    private String email;

    private String password;
    private Rol rol;
    private boolean activo;

    // Solo funcionarios
    private String departamentoId;

    // Solo solicitantes
    private String telefono;
    private String direccion;
    private String cedula;
    private String fcmToken;
}
