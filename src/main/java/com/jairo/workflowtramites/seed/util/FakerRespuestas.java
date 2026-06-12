package com.jairo.workflowtramites.seed.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import com.jairo.workflowtramites.model.embeds.RespuestaCampo;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Genera respuestas fake para los formularios del seeder.
 *
 * Orden de prioridad al generar un valor TEXT/TEXTAREA:
 *   1. Heurística por nombre del campo → CatalogoRespuestas (listas realistas,
 *      plantillas combinatorias).
 *   2. Datos de persona (nombre/email/cedula) → Faker.
 *   3. Frase corta neutra → Faker (fallback).
 */
@Component
@RequiredArgsConstructor
public class FakerRespuestas {

    private final CatalogoRespuestas catalogo;
    private final Faker faker = new Faker(new Locale("es"));
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<RespuestaCampo> generarRespuestas(List<CampoFormulario> campos) {
        if (campos == null || campos.isEmpty()) return new ArrayList<>();
        List<RespuestaCampo> respuestas = new ArrayList<>(campos.size());
        for (CampoFormulario campo : campos) {
            respuestas.add(RespuestaCampo.builder()
                    .nombreCampo(campo.getNombre())
                    .valor(generarValor(campo))
                    .build());
        }
        return respuestas;
    }

    public String generarComentario() {
        return catalogo.generarMotivoTexto();
    }

    private String generarValor(CampoFormulario c) {
        if (c.getTipo() == null) return catalogo.generarMotivoTexto();
        return switch (c.getTipo()) {
            case TEXT, TEXTAREA -> generarTextoSegunNombre(c.getNombre());
            case NUMBER -> String.valueOf(faker.number().numberBetween(1, 1000));
            case DATE -> LocalDate.now().minusDays(random.nextInt(1825)).toString();
            case SELECT, RADIO -> pickOpcion(c);
            case CHECKBOX -> generarCheckbox(c);
            case BOOLEAN -> random.nextBoolean() ? "true" : "false";
            case EMAIL -> faker.internet().emailAddress();
            case PHONE -> faker.phoneNumber().cellPhone();
            case FILE -> urlFake();
            case TABLA -> generarTabla(c);
            case GRID -> generarGrid(c);
        };
    }

    private String pickOpcion(CampoFormulario c) {
        return (c.getOpciones() != null && !c.getOpciones().isEmpty())
                ? c.getOpciones().get(random.nextInt(c.getOpciones().size()))
                : "Opcion 1";
    }

    /** CHECKBOX: marca al azar algunas opciones -> JSON array de los marcados (al menos uno). */
    private String generarCheckbox(CampoFormulario c) {
        List<String> ops = (c.getOpciones() != null && !c.getOpciones().isEmpty())
                ? c.getOpciones() : List.of("Opcion 1");
        List<String> marcadas = new ArrayList<>();
        for (String op : ops) if (random.nextBoolean()) marcadas.add(op);
        if (marcadas.isEmpty()) marcadas.add(ops.get(0));
        return toJson(marcadas);
    }

    /** TABLA: 1-3 filas, un valor por columna -> JSON array de objetos {columna: valor}. */
    private String generarTabla(CampoFormulario c) {
        List<String> cols = (c.getColumnas() != null && !c.getColumnas().isEmpty())
                ? c.getColumnas() : List.of("Columna 1");
        List<Map<String, String>> filas = new ArrayList<>();
        int n = 1 + random.nextInt(3);
        for (int i = 0; i < n; i++) {
            Map<String, String> fila = new LinkedHashMap<>();
            for (String col : cols) fila.put(col, generarTextoSegunNombre(col));
            filas.add(fila);
        }
        return toJson(filas);
    }

    /** GRID: cada fila elige una opcion (columna) -> JSON {fila: opcion}. */
    private String generarGrid(CampoFormulario c) {
        List<String> filas = (c.getFilas() != null && !c.getFilas().isEmpty())
                ? c.getFilas() : List.of("Fila 1");
        List<String> cols = (c.getColumnas() != null && !c.getColumnas().isEmpty())
                ? c.getColumnas() : List.of("Opcion 1");
        Map<String, String> resp = new LinkedHashMap<>();
        for (String fila : filas) resp.put(fila, cols.get(random.nextInt(cols.size())));
        return toJson(resp);
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            return "[]";
        }
    }

    /**
     * Devuelve un valor realista según el nombre del campo.
     * El orden de matching importa: patrones más específicos primero.
     */
    private String generarTextoSegunNombre(String nombre) {
        String n = nombre == null ? "" : nombre.toLowerCase();

        // --- Identidad (caso poco común en formularios actuales, pero defensivo) ---
        if (n.contains("nombre") && !n.contains("materia")) return faker.name().fullName();
        if (n.contains("apellido")) return faker.name().lastName();
        if (n.contains("email") || n.contains("correo")) return faker.internet().emailAddress();
        if (n.contains("telefono") || n.contains("celular")) return faker.phoneNumber().cellPhone();
        if (n.contains("direccion")) return faker.address().fullAddress();
        if (n.contains("cedula") || n.contains("dni")) return faker.number().digits(10);

        // --- Plantillas combinatorias (únicos) ---
        if (n.contains("tema") && n.contains("investigacion")) return catalogo.generarTemaInvestigacion();
        if (n.contains("titulo") && (n.contains("trabajo") || n.contains("tesis") || n.contains("propuesta"))) {
            return catalogo.generarTituloAcademico();
        }

        // --- Catálogo: personas ---
        if (n.contains("tutor") || n.contains("director") || n.contains("profesor")) {
            return catalogo.pickProfesor();
        }
        if (n.contains("destinatario")) return catalogo.pickDestinatario();

        // --- Catálogo: lugares y áreas ---
        if (n.contains("universidad")) return catalogo.pickUniversidad();
        if (n.contains("carrera")) return catalogo.pickCarrera();

        // --- Materias: código vs nombre ---
        if (n.contains("codigo") && (n.contains("materia") || n.contains("asignatura"))) {
            return catalogo.generarCodigoMateria();
        }
        if (n.contains("materia") || n.contains("asignatura")) {
            return catalogo.pickMateria();
        }

        // --- Motivos, justificaciones, descripciones ---
        if (n.contains("motivo") || n.contains("razon") || n.contains("justificacion")
                || n.contains("descripcion") || n.contains("observacion") || n.contains("situacion")) {
            return catalogo.generarMotivoTexto();
        }

        // --- Fallback neutro ---
        return catalogo.generarMotivoTexto();
    }

    private String urlFake() {
        return "https://s3.fake/uploads/" + UUID.randomUUID() + ".pdf";
    }
}
