package com.jairo.workflowtramites.seed.seeders;

import com.jairo.workflowtramites.model.Departamento;
import com.jairo.workflowtramites.model.FlujoTrabajo;
import com.jairo.workflowtramites.model.FormularioTemplate;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.model.enums.EstadoFlujo;
import com.jairo.workflowtramites.repository.DepartamentoRepository;
import com.jairo.workflowtramites.repository.FlujoTrabajoRepository;
import com.jairo.workflowtramites.repository.FormularioTemplateRepository;
import com.jairo.workflowtramites.repository.TramiteRepository;
import com.jairo.workflowtramites.service.FlujoTrabajoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Seeder que guarda como BORRADOR el diagrama BPMN de cada flujo.
 *
 * Para cada trámite definido en el mapa TRAMITE_A_ARCHIVO:
 *   1. Lee el XML de resources/seed/flujos/{archivo}.bpmn
 *   2. Reemplaza placeholders:
 *        {{DEPTO:<nombre>}}  →  id del depto en Mongo (buscado por nombre)
 *        {{FORM:<titulo>}}   →  id del formulario en Mongo (buscado por título)
 *   3. Guarda el XML resultante como BORRADOR del flujo correspondiente
 *      (vía FlujoTrabajoService.guardarBorrador).
 *
 * No publica. El admin abre cada flujo en el editor, valida visualmente y
 * pulsa "Publicar" desde el front para desplegar a Camunda.
 *
 * Idempotencia: si el flujo ya tiene borrador o ya está publicado (estado
 * distinto de SIN_PUBLICAR), se salta con log.
 *
 * Es independiente de los IDs entre ambientes: busca por nombre en tiempo
 * de ejecución, así funciona igual en local, dev server, producción.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FlujosSeeder {

    private static final Pattern PLACEHOLDER_DEPTO = Pattern.compile("\\{\\{DEPTO:([^}]+)\\}\\}");
    private static final Pattern PLACEHOLDER_FORM = Pattern.compile("\\{\\{FORM:([^}]+)\\}\\}");

    /** Mapa nombre-de-trámite (exacto como en SeedConfig) → archivo .bpmn en resources. */
    private static final Map<String, String> TRAMITE_A_ARCHIVO = new LinkedHashMap<>();
    static {
        TRAMITE_A_ARCHIVO.put("Solicitud de certificado de notas",  "certificado-notas.bpmn.tpl");
        TRAMITE_A_ARCHIVO.put("Solicitud de egresamiento",           "egresamiento.bpmn.tpl");
        TRAMITE_A_ARCHIVO.put("Tramite de titulacion",               "titulacion.bpmn.tpl");
        TRAMITE_A_ARCHIVO.put("Solicitud de beca",                   "beca.bpmn.tpl");
        TRAMITE_A_ARCHIVO.put("Convalidacion de materias",           "convalidacion.bpmn.tpl");
        TRAMITE_A_ARCHIVO.put("Retiro de materia",                   "retiro-materia.bpmn.tpl");
        TRAMITE_A_ARCHIVO.put("Justificacion de falta",              "justificacion.bpmn.tpl");
        TRAMITE_A_ARCHIVO.put("Cambio de carrera",                   "cambio-carrera.bpmn.tpl");
        TRAMITE_A_ARCHIVO.put("Carta de honorabilidad",              "carta-honorabilidad.bpmn.tpl");
        TRAMITE_A_ARCHIVO.put("Permiso de investigacion",            "permiso-investigacion.bpmn.tpl");
    }

    private final TramiteRepository tramiteRepository;
    private final FlujoTrabajoRepository flujoTrabajoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final FormularioTemplateRepository formularioTemplateRepository;
    private final FlujoTrabajoService flujoTrabajoService;

    /** Siembra todos los BPMN. Método original invocado desde SeedRunner (--seed=flujos-bpmn). */
    public void sembrar() {
        sembrar(null);
    }

    /**
     * Siembra el BPMN de un solo trámite por nombre, o todos si filtroTramite es null/blank.
     * El filtro se compara de forma case-insensitive contra los nombres del mapa.
     */
    public void sembrar(String filtroTramite) {
        int exitos = 0;
        int saltados = 0;
        int errores = 0;

        for (Map.Entry<String, String> entry : TRAMITE_A_ARCHIVO.entrySet()) {
            String nombreTramite = entry.getKey();
            String archivo = entry.getValue();

            if (filtroTramite != null && !filtroTramite.isBlank()
                    && !nombreTramite.toLowerCase().contains(filtroTramite.toLowerCase())) {
                continue;
            }

            try {
                boolean hecho = sembrarUno(nombreTramite, archivo);
                if (hecho) exitos++; else saltados++;
            } catch (Exception e) {
                errores++;
                log.error("[FlujosSeeder] Error procesando '{}': {}", nombreTramite, e.getMessage(), e);
            }
        }

        log.info("[FlujosSeeder] Resumen — borradores guardados: {}, saltados: {}, con error: {}",
                exitos, saltados, errores);
    }

    /** Retorna true si guardó borrador, false si saltó (ya tenía). */
    private boolean sembrarUno(String nombreTramite, String archivo) throws IOException {
        Tramite tramite = tramiteRepository.findAll().stream()
                .filter(t -> nombreTramite.equals(t.getNombre()))
                .findFirst()
                .orElse(null);
        if (tramite == null) {
            log.warn("[FlujosSeeder] Trámite '{}' no existe en Mongo. Skip.", nombreTramite);
            return false;
        }

        if (tramite.getFlujoTrabajoId() == null) {
            log.warn("[FlujosSeeder] Trámite '{}' no tiene flujoTrabajoId. Skip.", nombreTramite);
            return false;
        }

        FlujoTrabajo flujo = flujoTrabajoRepository.findById(tramite.getFlujoTrabajoId())
                .orElse(null);
        if (flujo == null) {
            log.warn("[FlujosSeeder] Flujo del trámite '{}' no existe. Skip.", nombreTramite);
            return false;
        }

        boolean yaTieneBorrador = flujo.getXmlBorrador() != null && !flujo.getXmlBorrador().isBlank();
        boolean yaPublicado = flujo.getEstadoFlujo() != null
                && flujo.getEstadoFlujo() != EstadoFlujo.SIN_PUBLICAR;

        if (yaTieneBorrador || yaPublicado) {
            log.info("[FlujosSeeder] '{}' ya tiene borrador/publicado. Skip.", flujo.getNombre());
            return false;
        }

        String xmlPlantilla = cargarXml(archivo);
        String xmlFinal = reemplazarPlaceholders(xmlPlantilla, nombreTramite);

        flujoTrabajoService.guardarBorrador(flujo.getId(), xmlFinal);

        log.info("[FlujosSeeder] ✓ Borrador guardado para '{}' (archivo: {})",
                flujo.getNombre(), archivo);
        return true;
    }

    private String cargarXml(String archivo) throws IOException {
        ClassPathResource recurso = new ClassPathResource("seed/flujos/" + archivo);
        try (var is = recurso.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String reemplazarPlaceholders(String xml, String nombreTramite) {
        StringBuffer sb = new StringBuffer();
        Matcher md = PLACEHOLDER_DEPTO.matcher(xml);
        while (md.find()) {
            String nombre = md.group(1).trim();
            Departamento d = departamentoRepository.findByNombre(nombre)
                    .orElseThrow(() -> new IllegalStateException(
                            "Flujo '" + nombreTramite + "': no existe departamento '" + nombre + "'"));
            md.appendReplacement(sb, Matcher.quoteReplacement(d.getId()));
        }
        md.appendTail(sb);

        String intermedio = sb.toString();
        StringBuffer sb2 = new StringBuffer();
        Matcher mf = PLACEHOLDER_FORM.matcher(intermedio);
        while (mf.find()) {
            String titulo = mf.group(1).trim();
            FormularioTemplate f = formularioTemplateRepository.findByTitulo(titulo)
                    .orElseThrow(() -> new IllegalStateException(
                            "Flujo '" + nombreTramite + "': no existe formulario '" + titulo + "'"));
            mf.appendReplacement(sb2, Matcher.quoteReplacement(f.getId()));
        }
        mf.appendTail(sb2);

        return sb2.toString();
    }
}
