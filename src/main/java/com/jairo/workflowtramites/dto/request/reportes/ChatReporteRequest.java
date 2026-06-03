package com.jairo.workflowtramites.dto.request.reportes;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatReporteRequest {

    @NotBlank
    private String sesionId;

    @NotBlank
    private String mensaje;
}
