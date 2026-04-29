package com.jairo.workflowtramites.model.embeds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransicionFlujo {

    private String targetId;
    private String etiqueta;
    private String valor;
}
