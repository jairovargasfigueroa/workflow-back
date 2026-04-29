package com.jairo.workflowtramites.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VersionFlujoDetalleResponse {

    private String id;
    private int numero;
    private String xml;
    private LocalDateTime fechaCreacion;
    private String creadoPor;
}
