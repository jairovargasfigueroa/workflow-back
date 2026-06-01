package com.jairo.workflowtramites.mapper;

import com.jairo.workflowtramites.dto.response.ArchivoResponse;
import com.jairo.workflowtramites.model.Archivo;

public final class ArchivoMapper {

    private ArchivoMapper() {}

    public static ArchivoResponse toResponse(Archivo archivo) {
        if (archivo == null) return null;
        return ArchivoResponse.builder()
                .id(archivo.getId())
                .solicitudId(archivo.getSolicitudId())
                .politicaId(archivo.getPoliticaId())
                .clienteId(archivo.getClienteId())
                .nombre(archivo.getNombre())
                .formato(archivo.getFormato())
                .tamanoBytes(archivo.getTamanoBytes())
                .contentType(archivo.getContentType())
                .subidoPor(archivo.getSubidoPor())
                .subidoPorNombre(archivo.getSubidoPorNombre())
                .fechaSubida(archivo.getFechaSubida())
                .departamentoOrigenId(archivo.getDepartamentoOrigenId())
                .campoFormularioOrigen(archivo.getCampoFormularioOrigen())
                .version(archivo.getVersion())
                .versionAnteriorId(archivo.getVersionAnteriorId())
                .estado(archivo.getEstado() != null ? archivo.getEstado().name() : null)
                .modificadoPor(archivo.getModificadoPor())
                .fechaModificacion(archivo.getFechaModificacion())
                .build();
    }
}
