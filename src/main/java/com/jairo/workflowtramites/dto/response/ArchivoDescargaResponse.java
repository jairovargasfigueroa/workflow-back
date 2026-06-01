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
public class ArchivoDescargaResponse {

    private String archivoId;
    private String nombre;
    private String contentType;
    private String urlDescarga;
    private LocalDateTime expiraEn;
}
