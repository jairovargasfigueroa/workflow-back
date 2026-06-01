package com.jairo.workflowtramites.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoResponse {

    private String id;
    private String solicitudId;
    private String politicaId;
    private String clienteId;

    private String nombre;
    private String formato;
    private Long tamanoBytes;
    private String contentType;

    private String subidoPor;
    private String subidoPorNombre;
    private LocalDateTime fechaSubida;

    private String departamentoOrigenId;
    private String campoFormularioOrigen;

    private Integer version;
    private String versionAnteriorId;

    private String estado;

    private String modificadoPor;
    private LocalDateTime fechaModificacion;
}
