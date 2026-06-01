package com.jairo.workflowtramites.config;

import com.jairo.workflowtramites.dto.response.ArchivoDescargaResponse;
import com.jairo.workflowtramites.model.Archivo;
import com.jairo.workflowtramites.model.enums.TipoEventoArchivo;
import com.jairo.workflowtramites.service.AuditoriaArchivoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditoriaArchivoAspect {

    private final AuditoriaArchivoService auditoria;

    @AfterReturning(
            pointcut = "execution(* com.jairo.workflowtramites.service.ArchivoService.subir(..))",
            returning = "resultado")
    public void onSubir(Object resultado) {
        if (resultado instanceof Archivo a) {
            auditoria.registrar(TipoEventoArchivo.UPLOAD, a.getId(), a.getSolicitudId(),
                    "Subida v" + a.getVersion() + " de " + a.getNombre());
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.jairo.workflowtramites.service.ArchivoService.subirNuevaVersion(..))",
            returning = "resultado")
    public void onNuevaVersion(Object resultado) {
        if (resultado instanceof Archivo a) {
            auditoria.registrar(TipoEventoArchivo.NEW_VERSION, a.getId(), a.getSolicitudId(),
                    "Nueva versión v" + a.getVersion() + " de " + a.getNombre());
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.jairo.workflowtramites.service.ArchivoService.revertirAVersion(..))",
            returning = "resultado")
    public void onRevertir(Object resultado) {
        if (resultado instanceof Archivo a) {
            auditoria.registrar(TipoEventoArchivo.REVERT, a.getId(), a.getSolicitudId(),
                    "Revertido a v" + a.getVersion() + " (" + a.getNombre() + ")");
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.jairo.workflowtramites.service.ArchivoService.generarUrlDescarga(..))",
            returning = "resultado")
    public void onDescargar(Object resultado) {
        if (resultado instanceof ArchivoDescargaResponse r) {
            auditoria.registrar(TipoEventoArchivo.DOWNLOAD, r.getArchivoId(), null,
                    "Descarga de " + r.getNombre());
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.jairo.workflowtramites.service.ArchivoService.obtenerMetadata(..))",
            returning = "resultado")
    public void onMetadata(Object resultado) {
        if (resultado instanceof Archivo a) {
            auditoria.registrar(TipoEventoArchivo.VIEW_METADATA, a.getId(), a.getSolicitudId(),
                    "Consulta de metadata de " + a.getNombre());
        }
    }

    @AfterReturning(
            pointcut = "execution(* com.jairo.workflowtramites.service.ArchivoService.eliminar(..)) && args(archivoId)",
            argNames = "archivoId")
    public void onEliminar(String archivoId) {
        auditoria.registrar(TipoEventoArchivo.DELETE, archivoId, null, "Soft delete del archivo");
    }

    @AfterThrowing(
            pointcut = "execution(* com.jairo.workflowtramites.service.ArchivoService.*(..))",
            throwing = "ex")
    public void onDenegado(JoinPoint jp, Throwable ex) {
        if (!(ex instanceof AccessDeniedException)) return;
        String metodo = jp.getSignature().getName();
        TipoEventoArchivo intento = switch (metodo) {
            case "generarUrlDescarga" -> TipoEventoArchivo.DOWNLOAD;
            case "obtenerMetadata" -> TipoEventoArchivo.VIEW_METADATA;
            case "subirNuevaVersion" -> TipoEventoArchivo.NEW_VERSION;
            case "revertirAVersion" -> TipoEventoArchivo.REVERT;
            case "eliminar" -> TipoEventoArchivo.DELETE;
            default -> TipoEventoArchivo.ACCESS_DENIED;
        };
        String archivoId = jp.getArgs().length > 0 && jp.getArgs()[0] instanceof String s ? s : null;
        auditoria.registrarDenegado(intento, archivoId, ex.getMessage());
    }
}
