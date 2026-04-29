package com.jairo.workflowtramites.model.embeds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Adjunto {

    private String nombre;
    private String url;
    private String tipo;
    private LocalDateTime fechaSubida;
}
