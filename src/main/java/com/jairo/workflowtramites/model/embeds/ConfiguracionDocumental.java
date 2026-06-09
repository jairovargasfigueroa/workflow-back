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
public class ConfiguracionDocumental {

    // Documentos que el funcionario genera en el nodo (con permisos por documento).
    @Builder.Default
    private List<DocumentoConfig> documentosProducidos = new ArrayList<>();

    // Permisos por defecto para archivos sueltos (ad-hoc) subidos en el nodo
    // sin asociarlos a un documento declarado.
    @Builder.Default
    private PermisoSet permisosDefaultAdHoc = PermisoSet.builder().build();
}
