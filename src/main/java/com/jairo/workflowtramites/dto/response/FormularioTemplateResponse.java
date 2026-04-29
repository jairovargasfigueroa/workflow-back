package com.jairo.workflowtramites.dto.response;

import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FormularioTemplateResponse {

    private String id;
    private String titulo;
    private String descripcion;
    private List<CampoFormulario> campos;
    private boolean activo;
    private LocalDateTime fechaCreacion;
}
