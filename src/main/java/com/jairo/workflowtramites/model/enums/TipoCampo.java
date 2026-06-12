package com.jairo.workflowtramites.model.enums;

public enum TipoCampo {
    TEXT,
    NUMBER,
    DATE,
    SELECT,
    FILE,
    TEXTAREA,
    RADIO,      // seleccion unica (usa "opciones", como SELECT pero con botones)
    CHECKBOX,   // seleccion multiple / checklist (usa "opciones") -> valor: JSON de marcados
    BOOLEAN,    // si/no -> valor: "true"/"false"
    EMAIL,      // texto validado como email (el back lo guarda como string)
    PHONE,      // texto validado como telefono (el back lo guarda como string)
    TABLA,      // filas dinamicas (usa "columnas") -> valor: JSON array de filas
    GRID        // matriz de seleccion (usa "filas" + "columnas") -> valor: JSON {fila: opcion}
}
