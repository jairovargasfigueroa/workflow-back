package com.jairo.workflowtramites.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VersionFlujoResumen {

    private String id;
    private int numero;
    private LocalDateTime fechaCreacion;
    private String creadoPor;
}
