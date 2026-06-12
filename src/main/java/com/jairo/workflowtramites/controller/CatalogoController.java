package com.jairo.workflowtramites.controller;

import com.jairo.workflowtramites.dto.response.AccionCatalogoResponse;
import com.jairo.workflowtramites.model.enums.AccionFlujo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Catalogos que consume el front. Por ahora, las acciones de decision del editor de flujos:
 * el editor las ofrece en un dropdown (en vez de texto libre) y el back valida contra estas.
 */
@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    @GetMapping("/acciones-flujo")
    public List<AccionCatalogoResponse> accionesFlujo() {
        return Arrays.stream(AccionFlujo.values())
                .map(a -> new AccionCatalogoResponse(a.getEtiqueta(), a.getValor(), a.isEsFinal()))
                .toList();
    }
}
