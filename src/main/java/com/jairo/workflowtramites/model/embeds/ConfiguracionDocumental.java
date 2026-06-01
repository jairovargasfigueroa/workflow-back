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

    @Builder.Default
    private List<DocumentoConfig> documentosEsperados = new ArrayList<>();

    @Builder.Default
    private List<DocumentoConfig> documentosProducidos = new ArrayList<>();

    @Builder.Default
    private PermisoSet permisosDefaultAdHoc = PermisoSet.builder().build();
}
