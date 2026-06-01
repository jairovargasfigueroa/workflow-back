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

    @Builder.Default
    private List<SujetoPermiso> lectores = new ArrayList<>();

    @Builder.Default
    private List<SujetoPermiso> editores = new ArrayList<>();

    @Builder.Default
    private List<SujetoPermiso> eliminadores = new ArrayList<>();
}
