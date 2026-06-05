package com.jairo.workflowtramites.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChatAgenteTramitesRequest {

    // Nullable: null si es sesión nueva, el micro la crea y devuelve el id en el primer evento
    private String sesionId;

    @NotBlank
    private String clientMessageId;

    @NotBlank
    private String mensaje;

    private Map<String, Object> datos;

    private List<Map<String, Object>> archivosListos;
}
