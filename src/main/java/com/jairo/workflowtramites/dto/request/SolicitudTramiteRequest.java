package com.jairo.workflowtramites.dto.request;

import com.jairo.workflowtramites.model.embeds.RespuestaCampo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudTramiteRequest {

    @NotBlank
    private String tramiteId;

    private List<RespuestaCampo> respuestas;

    // Idempotencia: UUID que el cliente (movil offline) manda por operacion para deduplicar reintentos.
    private String clientId;
}
