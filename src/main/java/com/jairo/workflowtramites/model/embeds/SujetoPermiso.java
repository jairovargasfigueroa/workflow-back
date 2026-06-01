package com.jairo.workflowtramites.model.embeds;

import com.jairo.workflowtramites.model.enums.TipoSujetoPermiso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SujetoPermiso {

    private TipoSujetoPermiso tipo;
    private String sujetoId;
}
