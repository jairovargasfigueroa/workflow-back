package com.jairo.workflowtramites.dto.response.reportes;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginaResponse<T> {

    private List<T> contenido;
    private int pagina;
    private int tamano;
    private long total;
    private int totalPaginas;
}
