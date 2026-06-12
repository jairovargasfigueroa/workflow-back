package com.jairo.workflowtramites.model.embeds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisoSet {

    // Quien puede SUBIR un archivo nuevo (la primera vez). Separado de editores (= MODIFICAR
    // version). El docente los distingue: "algunos los van a poder subir, otros que no;
    // los van a poder solo leer, los van a poder modificar".
    @Builder.Default
    private List<SujetoPermiso> subidores = new ArrayList<>();

    @Builder.Default
    private List<SujetoPermiso> lectores = new ArrayList<>();

    @Builder.Default
    private List<SujetoPermiso> editores = new ArrayList<>();

    @Builder.Default
    private List<SujetoPermiso> eliminadores = new ArrayList<>();
}
