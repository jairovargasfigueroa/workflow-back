package com.jairo.workflowtramites.seed.config;

import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import com.jairo.workflowtramites.model.enums.TipoCampo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Catálogo de definiciones de campos por nombre de formulario.
 *
 * Dominio: Universidad.
 *
 * Cada entrada mapea el título del formulario (igual que en SeedConfig.formularios)
 * a la lista de CampoFormulario específica para ese formulario. Así los 20 formularios
 * del seeder tienen campos realistas en vez de plantilla genérica.
 *
 * Si un nombre no está en el mapa, se devuelve una lista por defecto (fallback seguro).
 */
@Component
public class FormulariosCatalogo {

    private final Map<String, List<CampoFormulario>> catalogo = new HashMap<>();

    public FormulariosCatalogo() {
        cargarFormulariosDelSolicitante();
        cargarFormulariosDeProceso();
    }

    public List<CampoFormulario> camposPara(String nombreFormulario) {
        List<CampoFormulario> campos = catalogo.get(nombreFormulario);
        return campos != null ? campos : fallback();
    }

    // ========================================================
    // Formularios iniciales del solicitante (uno por trámite)
    // ========================================================

    private void cargarFormulariosDelSolicitante() {

        catalogo.put("Datos para certificado de notas", List.of(
                select("periodoAcademico", "Periodo académico", true,
                        List.of("2024-1", "2024-2", "2025-1", "2025-2", "2026-1")),
                select("tipoCertificado", "Tipo de certificado", true,
                        List.of("Notas", "Promedio", "Oficial con firma")),
                textarea("motivo", "Motivo de la solicitud", false)
        ));

        catalogo.put("Datos de egresamiento", List.of(
                date("fechaEsperada", "Fecha esperada de egresamiento", true),
                number("promedioObtenido", "Promedio obtenido", true),
                select("cumplePracticas", "¿Cumple prácticas preprofesionales?", true,
                        List.of("Sí", "No", "En curso"))
        ));

        catalogo.put("Datos de titulacion", List.of(
                select("modalidad", "Modalidad de titulación", true,
                        List.of("Tesis", "Examen complexivo", "Proyecto integrador")),
                text("tituloTrabajo", "Título del trabajo", true),
                text("tutor", "Tutor académico", true)
        ));

        catalogo.put("Datos de solicitud de beca", List.of(
                select("periodoAcademico", "Periodo académico", true,
                        List.of("2025-1", "2025-2", "2026-1")),
                number("ingresosFamiliares", "Ingresos familiares mensuales (USD)", true),
                number("numeroDependientes", "Número de dependientes", true),
                select("tipoVivienda", "Tipo de vivienda", true,
                        List.of("Propia", "Arrendada", "Familiar")),
                textarea("descripcionSituacion", "Descripción de la situación", false)
        ));

        catalogo.put("Datos de convalidacion", List.of(
                text("universidadOrigen", "Universidad de origen", true),
                textarea("materiasConvalidar", "Materias a convalidar", true)
        ));

        catalogo.put("Datos de retiro de materia", List.of(
                text("codigoMateria", "Código de la materia", true),
                text("nombreMateria", "Nombre de la materia", true),
                select("motivo", "Motivo del retiro", true,
                        List.of("Salud", "Laboral", "Familiar", "Otro")),
                textarea("justificacion", "Justificación", false)
        ));

        catalogo.put("Datos de justificacion", List.of(
                date("fechaFalta", "Fecha de la falta", true),
                select("motivo", "Motivo", true,
                        List.of("Médico", "Familiar", "Laboral", "Otro")),
                textarea("descripcion", "Descripción detallada", false)
        ));

        catalogo.put("Datos de cambio de carrera", List.of(
                text("carreraActual", "Carrera actual", true),
                text("carreraDeseada", "Carrera deseada", true),
                number("promedioActual", "Promedio actual", true),
                textarea("motivo", "Motivo del cambio", true)
        ));

        catalogo.put("Datos de carta de honorabilidad", List.of(
                text("destinatario", "Destinatario de la carta", true),
                textarea("motivo", "Motivo para solicitar la carta", true),
                select("urgencia", "Urgencia", true,
                        List.of("Normal", "Alta"))
        ));

        catalogo.put("Datos de permiso de investigacion", List.of(
                text("temaInvestigacion", "Tema de investigación", true),
                text("director", "Director de investigación", true),
                date("fechaInicio", "Fecha de inicio", true),
                date("fechaFin", "Fecha estimada de finalización", true)
        ));
    }

    // ==========================================
    // Formularios de proceso (usados en userTask)
    // ==========================================

    private void cargarFormulariosDeProceso() {

        catalogo.put("Revision academica", List.of(
                number("promedioVerificado", "Promedio verificado", true),
                select("cumpleRequisitos", "¿Cumple requisitos académicos?", true,
                        List.of("Sí", "No", "Parcial")),
                textarea("observaciones", "Observaciones", false)
        ));

        catalogo.put("Dictamen del decano", List.of(
                select("decision", "Decisión", true,
                        List.of("Aprobado", "Rechazado", "Devuelto para correcciones")),
                textarea("justificacion", "Justificación de la decisión", true)
        ));

        catalogo.put("Evaluacion socioeconomica", List.of(
                select("nivelSocioeconomico", "Nivel socioeconómico", true,
                        List.of("A", "B", "C", "D", "E")),
                number("porcentajeBeca", "Porcentaje de beca recomendado (%)", true),
                textarea("observaciones", "Observaciones", false)
        ));

        catalogo.put("Validacion de pagos", List.of(
                select("tieneAdeudo", "¿Tiene adeudos?", true,
                        List.of("Sí", "No")),
                number("montoAdeudo", "Monto del adeudo (USD)", false),
                textarea("observaciones", "Observaciones", false)
        ));

        catalogo.put("Verificacion de biblioteca", List.of(
                number("librosPendientes", "Libros pendientes de devolución", true),
                number("multasPendientes", "Multas pendientes (USD)", true),
                select("puedeProceder", "¿Puede proceder el trámite?", true,
                        List.of("Sí", "No"))
        ));

        catalogo.put("Informe de registro", List.of(
                select("estadoMatricula", "Estado de la matrícula", true,
                        List.of("Activa", "Inactiva", "Retirada", "Egresado")),
                text("periodoVerificado", "Período verificado", true),
                textarea("observaciones", "Observaciones", false)
        ));

        catalogo.put("Aprobacion de investigacion", List.of(
                select("comiteAprobo", "¿El comité aprobó?", true,
                        List.of("Sí", "No", "Con observaciones")),
                date("fechaReunion", "Fecha de reunión del comité", true),
                textarea("observaciones", "Observaciones del comité", false)
        ));

        catalogo.put("Revision de posgrado", List.of(
                select("tipoPosgrado", "Tipo de posgrado", true,
                        List.of("Maestría", "Doctorado", "Especialización")),
                select("cumpleRequisitos", "¿Cumple requisitos?", true,
                        List.of("Sí", "No", "Parcial")),
                textarea("observaciones", "Observaciones", false)
        ));

        catalogo.put("Aprobacion de direccion de carrera", List.of(
                select("directorAprueba", "¿El director aprueba?", true,
                        List.of("Sí", "No")),
                textarea("condiciones", "Condiciones o comentarios", false)
        ));

        catalogo.put("Observaciones academicas", List.of(
                textarea("observaciones", "Observaciones académicas", true)
        ));
    }

    // ==========================================
    // Fallback (solo por seguridad)
    // ==========================================

    private List<CampoFormulario> fallback() {
        return List.of(
                textarea("observaciones", "Observaciones", false)
        );
    }

    // ==========================================
    // Helpers para construir los CampoFormulario
    // ==========================================

    private CampoFormulario text(String nombre, String etiqueta, boolean requerido) {
        return CampoFormulario.builder()
                .nombre(nombre).etiqueta(etiqueta)
                .tipo(TipoCampo.TEXT).requerido(requerido).build();
    }

    private CampoFormulario textarea(String nombre, String etiqueta, boolean requerido) {
        return CampoFormulario.builder()
                .nombre(nombre).etiqueta(etiqueta)
                .tipo(TipoCampo.TEXTAREA).requerido(requerido).build();
    }

    private CampoFormulario number(String nombre, String etiqueta, boolean requerido) {
        return CampoFormulario.builder()
                .nombre(nombre).etiqueta(etiqueta)
                .tipo(TipoCampo.NUMBER).requerido(requerido).build();
    }

    private CampoFormulario date(String nombre, String etiqueta, boolean requerido) {
        return CampoFormulario.builder()
                .nombre(nombre).etiqueta(etiqueta)
                .tipo(TipoCampo.DATE).requerido(requerido).build();
    }

    private CampoFormulario select(String nombre, String etiqueta, boolean requerido,
                                    List<String> opciones) {
        return CampoFormulario.builder()
                .nombre(nombre).etiqueta(etiqueta)
                .tipo(TipoCampo.SELECT).requerido(requerido)
                .opciones(opciones).build();
    }
}
