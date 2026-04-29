package com.jairo.workflowtramites.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicarFlujoRequest {

    private String publicadoPor;
    private String comentario;
}
