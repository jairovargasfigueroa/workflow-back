package com.jairo.workflowtramites.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VersionFlujoResponse {

    private String id;
    private String flujoId;
    private int numero;
    private List<NodoFlujoResponse> nodos;
    private LocalDateTime fechaCreacion;
    private String creadoPor;
}
