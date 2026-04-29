package com.jairo.workflowtramites.dto.request;

import com.jairo.workflowtramites.model.enums.EstadoFlujo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarEstadoFlujoRequest {

    @NotNull
    private EstadoFlujo estado;
}
