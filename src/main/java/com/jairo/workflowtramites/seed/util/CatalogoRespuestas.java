package com.jairo.workflowtramites.seed.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Catálogo de valores realistas para campos de formulario.
 *
 * Dos tipos de datos:
 *   - Listas: valores donde la repetición es natural (ej. un profesor
 *     supervisa varias tesis, una universidad tiene varios traslados).
 *   - Plantillas combinatorias: para campos que deben ser únicos (títulos
 *     de tesis, temas de investigación, motivos). Se arman con placeholders
 *     que se rellenan con listas auxiliares, logrando miles de combinaciones.
 */
@Component
public class CatalogoRespuestas {

    private final Random random = new Random();

    // ==========================================
    // Listas — repetición natural
    // ==========================================

    private static final List<String> PROFESORES = List.of(
            "Dr. Carlos Mendoza Paredes", "Ing. María Salinas Vera", "Mg. Jorge Paredes León",
            "Dra. Ana Villamar Ortiz", "Ing. Luis Cevallos Mora", "Mg. Patricia Ruiz Castillo",
            "Dr. Roberto Chang Ponce", "Ing. Diego Arias Benítez", "Mg. Sofía Bermeo Granda",
            "Dra. Lucía Intriago Zamora", "Ing. Fernando Quiroz Alarcón", "Mg. Elena Tapia Rosas",
            "Dr. Javier Andrade Cobeña", "Ing. Camila Moncayo Vélez", "Mg. Andrés Zurita Peña",
            "Dra. Verónica Orellana Núñez", "Ing. Pablo Montenegro Lara", "Mg. Isabel Carrión Vaca",
            "Dr. Manuel Espinoza Jaramillo", "Ing. Daniela Reyes Molina", "Mg. Ricardo Herrera Flores",
            "Dra. Gabriela Aguirre Campos", "Ing. Oscar Chávez Narváez", "Mg. Rosa Yépez Torres",
            "Dr. Esteban Bustamante Lucero", "Ing. Paola Freire Sánchez", "Mg. Alejandro Cueva Burgos",
            "Dra. Natalia Pinto Arellano", "Ing. Gabriel Donoso Palacios", "Mg. Tatiana Villalba Ríos",
            "Dr. Mauricio Salas Granja", "Ing. Lorena Bonilla Mendoza", "Mg. Sergio Guamán Espín",
            "Dra. Carolina Ávila Pesantes", "Ing. Héctor Jiménez Segovia", "Mg. Valeria Estrada Cornejo",
            "Dr. Cristian Vallejo Merino", "Ing. Mónica Alcívar Pozo", "Mg. Iván Cabrera Quishpe",
            "Dra. Silvia Maldonado Piedra"
    );

    private static final List<String> UNIVERSIDADES = List.of(
            "Universidad de Especialidades Espíritu Santo",
            "Escuela Superior Politécnica del Litoral",
            "Universidad Católica de Guayaquil",
            "Universidad de Guayaquil",
            "Universidad San Francisco de Quito",
            "Pontificia Universidad Católica del Ecuador",
            "Universidad Central del Ecuador",
            "Universidad Técnica de Ambato",
            "Universidad de Cuenca",
            "Escuela Politécnica Nacional",
            "Universidad Técnica Particular de Loja",
            "Universidad del Azuay",
            "Universidad Nacional de Loja",
            "Universidad Laica Eloy Alfaro de Manabí",
            "Universidad Agraria del Ecuador"
    );

    private static final List<String> CARRERAS = List.of(
            "Ingeniería de Software", "Medicina", "Derecho", "Administración de Empresas",
            "Psicología", "Arquitectura", "Economía", "Comunicación Social",
            "Ingeniería Civil", "Ingeniería Industrial", "Contabilidad y Auditoría",
            "Odontología", "Marketing", "Ingeniería Ambiental", "Educación"
    );

    private static final List<String> MATERIAS = List.of(
            "Cálculo I", "Cálculo II", "Álgebra Lineal", "Programación I", "Programación II",
            "Estadística", "Física General", "Química General", "Economía Política",
            "Contabilidad General", "Inglés Técnico", "Metodología de la Investigación",
            "Bases de Datos", "Estructuras de Datos", "Ingeniería de Software",
            "Redes de Computadoras", "Sistemas Operativos", "Inteligencia Artificial",
            "Derecho Constitucional", "Anatomía"
    );

    private static final String[] PREFIJOS_CODIGO_MATERIA = {
            "ING", "MAT", "FIS", "QUI", "PRG", "ADM", "ECO", "DER", "MED", "BDD"
    };

    private static final List<String> DESTINATARIOS = List.of(
            "A quien corresponda",
            "Embajada de Estados Unidos",
            "Embajada de Canadá",
            "Embajada de España",
            "Ministerio del Trabajo",
            "Ministerio de Educación Superior",
            "Empresa Tech Solutions S.A.",
            "Corporación Nacional de Telecomunicaciones",
            "Banco Pichincha",
            "Universidad de Destino (Extranjero)",
            "Senescyt",
            "Consulado General de Italia",
            "Compañía Industrial Ecuatoriana"
    );

    // ==========================================
    // Plantillas combinatorias — variedad alta
    // ==========================================

    private static final String[] FORMATOS_TITULO = {
            "Análisis del impacto de %s en %s",
            "Aplicación de %s para mejorar %s",
            "Estudio comparativo entre %s y métodos tradicionales en %s",
            "Modelo predictivo basado en %s aplicado a %s",
            "Evaluación del uso de %s en procesos de %s",
            "Diseño de una solución con %s orientada a %s",
            "Implementación de %s como alternativa en %s",
            "Desarrollo de una plataforma de %s para %s"
    };

    private static final String[] TECNOLOGIAS = {
            "inteligencia artificial", "blockchain", "machine learning",
            "análisis de datos masivos", "computación en la nube", "realidad aumentada",
            "bases de datos NoSQL", "microservicios", "internet de las cosas",
            "procesamiento de lenguaje natural", "visión por computadora", "automatización robótica",
            "aprendizaje profundo", "redes neuronales", "arquitecturas serverless"
    };

    private static final String[] CONTEXTOS_ACADEMICOS = {
            "la educación superior", "la gestión universitaria", "la deserción estudiantil",
            "los procesos administrativos académicos", "la admisión de nuevos estudiantes",
            "el desempeño académico", "la evaluación docente", "el acompañamiento tutorial",
            "los servicios de biblioteca", "la gestión de becas", "los trámites estudiantiles",
            "la planificación curricular", "la investigación universitaria"
    };

    private static final String[] AREAS_INVESTIGACION = {
            "sostenibilidad ambiental", "eficiencia energética", "salud pública",
            "inclusión educativa", "desarrollo tecnológico", "innovación social",
            "bienestar estudiantil", "gestión del conocimiento", "transformación digital"
    };

    private static final String[] RAZONES_MOTIVO = {
            "razones académicas",
            "motivos personales debidamente justificados",
            "circunstancias laborales inesperadas",
            "necesidades familiares",
            "procesos de movilidad estudiantil",
            "motivos de salud",
            "participación en programas de intercambio",
            "requerimientos del proceso de titulación"
    };

    private static final String[] NECESIDADES_ACCION = {
            "avanzar en mi proceso académico",
            "cumplir con los requisitos establecidos",
            "continuar con los estudios regularmente",
            "formalizar mi situación estudiantil",
            "completar la documentación requerida",
            "gestionar el trámite correspondiente"
    };

    private static final String[] CIERRES = {
            "Agradezco de antemano su atención.",
            "Quedo atento a su respuesta.",
            "Espero contar con su apoyo.",
            "Solicito considerar esta petición.",
            "Agradezco el trámite correspondiente."
    };

    // ==========================================
    // API pública
    // ==========================================

    public String pickProfesor() { return pick(PROFESORES); }
    public String pickUniversidad() { return pick(UNIVERSIDADES); }
    public String pickCarrera() { return pick(CARRERAS); }
    public String pickMateria() { return pick(MATERIAS); }
    public String pickDestinatario() { return pick(DESTINATARIOS); }

    public String generarCodigoMateria() {
        String prefijo = PREFIJOS_CODIGO_MATERIA[random.nextInt(PREFIJOS_CODIGO_MATERIA.length)];
        int numero = 100 + random.nextInt(400);
        return prefijo + "-" + numero;
    }

    public String generarTituloAcademico() {
        String formato = FORMATOS_TITULO[random.nextInt(FORMATOS_TITULO.length)];
        String tech = TECNOLOGIAS[random.nextInt(TECNOLOGIAS.length)];
        String ctx = CONTEXTOS_ACADEMICOS[random.nextInt(CONTEXTOS_ACADEMICOS.length)];
        return String.format(formato, tech, ctx);
    }

    public String generarTemaInvestigacion() {
        String formato = FORMATOS_TITULO[random.nextInt(FORMATOS_TITULO.length)];
        String tech = TECNOLOGIAS[random.nextInt(TECNOLOGIAS.length)];
        String area = AREAS_INVESTIGACION[random.nextInt(AREAS_INVESTIGACION.length)];
        return String.format(formato, tech, area);
    }

    /** Párrafo corto tipo motivo/justificación, con partes combinatorias. */
    public String generarMotivoTexto() {
        String razon = RAZONES_MOTIVO[random.nextInt(RAZONES_MOTIVO.length)];
        String necesidad = NECESIDADES_ACCION[random.nextInt(NECESIDADES_ACCION.length)];
        String cierre = CIERRES[random.nextInt(CIERRES.length)];
        return "Por " + razon + ", solicito " + necesidad + ". " + cierre;
    }

    private String pick(List<String> lista) {
        return lista.get(random.nextInt(lista.size()));
    }
}
