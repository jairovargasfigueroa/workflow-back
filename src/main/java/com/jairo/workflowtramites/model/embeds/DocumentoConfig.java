package com.jairo.workflowtramites.model.embeds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoConfig {

    private String nombre;
    private String campoFormularioAsociado;

    @Builder.Default
    private List<String> formatosAceptados = new ArrayList<>();

    @Builder.Default
    private boolean obligatorio = false;

    @Builder.Default
    private PermisoSet permisos = PermisoSet.builder().build();

    @Builder.Default
    private boolean inmutablePostCierre = false;
}
