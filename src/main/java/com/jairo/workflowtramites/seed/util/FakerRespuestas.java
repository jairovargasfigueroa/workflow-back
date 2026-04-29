package com.jairo.workflowtramites.seed.util;

import com.jairo.workflowtramites.model.embeds.Adjunto;
import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import com.jairo.workflowtramites.model.embeds.RespuestaCampo;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/**
 * Genera respuestas y adjuntos fake para los formularios del seeder.
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

    public List<Adjunto> generarAdjuntos(List<String> requisitos) {
        if (requisitos == null || requisitos.isEmpty()) return new ArrayList<>();
        List<Adjunto> adjuntos = new ArrayList<>(requisitos.size());
        for (String requisito : requisitos) {
            adjuntos.add(Adjunto.builder()
                    .nombre(requisito + ".pdf")
                    .url(urlFake())
                    .tipo("application/pdf")
                    .fechaSubida(LocalDateTime.now())
                    .build());
        }
        return adjuntos;
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
            case SELECT -> (c.getOpciones() != null && !c.getOpciones().isEmpty())
                    ? c.getOpciones().get(random.nextInt(c.getOpciones().size()))
                    : "Opcion 1";
            case FILE -> urlFake();
        };
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
