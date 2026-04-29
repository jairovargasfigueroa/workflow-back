package com.jairo.workflowtramites.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransicionResponse {

    private String targetId;
    private String etiqueta;
    private String valor;
}
