package com.jairo.workflowtramites.dto.response.motor;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginaAnomaliasResponse {
    private List<AnomaliaResponse> contenido;
    private long total;
    private int pagina;
    private int totalPaginas;
    private boolean disponible;

    public static PaginaAnomaliasResponse vacia(int pagina) {
        return PaginaAnomaliasResponse.builder()
                .contenido(List.of())
                .total(0)
                .pagina(pagina)
                .totalPaginas(0)
                .disponible(false)
                .build();
    }
}
