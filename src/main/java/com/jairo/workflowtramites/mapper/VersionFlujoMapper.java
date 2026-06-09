package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.response.NodoFlujoResponse;
import com.jairo.workflowtramites.dto.response.TransicionResponse;
import com.jairo.workflowtramites.dto.response.VersionFlujoDetalleResponse;
import com.jairo.workflowtramites.dto.response.VersionFlujoResumen;
import com.jairo.workflowtramites.dto.response.VersionFlujoResponse;
import com.jairo.workflowtramites.model.VersionFlujo;
import com.jairo.workflowtramites.model.embeds.NodoFlujo;
import com.jairo.workflowtramites.model.embeds.TransicionFlujo;

import java.util.List;

public class VersionFlujoMapper {

    public static VersionFlujoResponse toResponse(VersionFlujo v) {
        return VersionFlujoResponse.builder()
                .id(v.getId())
                .flujoId(v.getFlujoId())
                .numero(v.getNumero())
                .nodos(toNodosResponse(v.getNodos()))
                .fechaCreacion(v.getFechaCreacion())
                .creadoPor(v.getCreadoPor())
                .build();
    }

    public static VersionFlujoResumen toResumen(VersionFlujo v) {
        return VersionFlujoResumen.builder()
                .id(v.getId())
                .numero(v.getNumero())
                .fechaCreacion(v.getFechaCreacion())
                .creadoPor(v.getCreadoPor())
                .build();
    }

    public static VersionFlujoDetalleResponse toDetalle(VersionFlujo v) {
        return VersionFlujoDetalleResponse.builder()
                .id(v.getId())
                .numero(v.getNumero())
                .xml(v.getXml())
                .fechaCreacion(v.getFechaCreacion())
                .creadoPor(v.getCreadoPor())
                .build();
    }

    private static List<NodoFlujoResponse> toNodosResponse(List<NodoFlujo> nodos) {
        if (nodos == null) return List.of();
        return nodos.stream().map(VersionFlujoMapper::toNodoResponse).toList();
    }

    private static NodoFlujoResponse toNodoResponse(NodoFlujo n) {
        return NodoFlujoResponse.builder()
                .elementId(n.getElementId())
                .tipo(n.getTipo())
                .nombre(n.getNombre())
                .departamentoId(n.getDepartamentoId())
                .carrilId(n.getCarrilId())
                .carrilNombre(n.getCarrilNombre())
                .formularioId(n.getFormularioId())
                .camposFormulario(n.getCamposFormulario())
                .transiciones(toTransicionesResponse(n.getTransiciones()))
                .configuracionDocumental(n.getConfiguracionDocumental())
                .slaNodoHoras(n.getSlaNodoHoras())
                .build();
    }

    private static List<TransicionResponse> toTransicionesResponse(List<TransicionFlujo> transiciones) {
        if (transiciones == null) return List.of();
        return transiciones.stream().map(VersionFlujoMapper::toTransicionResponse).toList();
    }

    private static TransicionResponse toTransicionResponse(TransicionFlujo t) {
        return TransicionResponse.builder()
                .targetId(t.getTargetId())
                .etiqueta(t.getEtiqueta())
                .valor(t.getValor())
                .build();
    }
}
