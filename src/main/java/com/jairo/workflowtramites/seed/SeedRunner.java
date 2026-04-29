package com.jairo.workflowtramites.seed;

import com.jairo.workflowtramites.seed.seeders.DatosMaestrosSeeder;
import com.jairo.workflowtramites.seed.seeders.FlujosSeeder;
import com.jairo.workflowtramites.seed.seeders.SolicitudesSeeder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orquestador del seed de datos de prueba.
 *
 * Solo se ejecuta cuando la app arranca con el perfil "seed" activo:
 *
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,seed
 *
 * Acepta el argumento --seed=... para ejecutar seeders individuales.
 *
 * Objetivos soportados (separados por coma si son varios):
 *   departamentos  — datos maestros: departamentos
 *   usuarios       — datos maestros: admin + funcionarios + solicitantes
 *   formularios    — datos maestros: 20 formularios (catálogo)
 *   flujos         — datos maestros: 10 flujos sin publicar (solo entidad)
 *   tramites       — datos maestros: 10 trámites
 *   flujos-bpmn    — guarda como BORRADOR el diagrama BPMN de los 10 flujos
 *   solicitudes    — (TODO) genera solicitudes con historial
 *   todo           — todos los de datos maestros + flujos-bpmn + solicitudes
 *
 * También acepta:
 *   --tramite=<substring>   — filtrar para flujos-bpmn (ej. --tramite=beca)
 *
 * Ejemplos:
 *   --seed=departamentos
 *   --seed=flujos-bpmn
 *   --seed=flujos-bpmn --tramite=beca
 *   --seed=departamentos,usuarios,formularios
 */
@Component
@Profile("seed")
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class SeedRunner implements CommandLineRunner {

    private final DatosMaestrosSeeder datosMaestrosSeeder;
    private final FlujosSeeder flujosSeeder;
    private final SolicitudesSeeder solicitudesSeeder;

    @Override
    public void run(String... args) {
        Set<String> objetivos = parseSeedArgs(args);
        String filtroTramite = parseStringArg(args, "--tramite=");

        log.info("====================================");
        log.info("  SEED DE DATOS - INICIO  (objetivos: {})", objetivos);
        if (filtroTramite != null) {
            log.info("  Filtro tramite: {}", filtroTramite);
        }
        log.info("====================================");

        long inicio = System.currentTimeMillis();

        if (objetivos.isEmpty() || objetivos.contains("todo")) {
            datosMaestrosSeeder.sembrarTodo();
            flujosSeeder.sembrar();
            solicitudesSeeder.sembrar();
        } else {
            if (objetivos.contains("departamentos")) datosMaestrosSeeder.sembrarDepartamentos();
            if (objetivos.contains("usuarios"))      datosMaestrosSeeder.sembrarUsuarios();
            if (objetivos.contains("formularios"))   datosMaestrosSeeder.sembrarFormularios();
            if (objetivos.contains("flujos"))        datosMaestrosSeeder.sembrarFlujos();
            if (objetivos.contains("tramites"))      datosMaestrosSeeder.sembrarTramites();
            if (objetivos.contains("flujos-bpmn"))   flujosSeeder.sembrar(filtroTramite);
            if (objetivos.contains("solicitudes"))   solicitudesSeeder.sembrar();
        }

        long duracion = (System.currentTimeMillis() - inicio) / 1000;

        log.info("====================================");
        log.info("  SEED DE DATOS - FINALIZADO ({}s)", duracion);
        log.info("====================================");
    }

    /**
     * Busca un argumento con forma --seed=a,b,c y devuelve el set {a, b, c}.
     * Si no hay argumento, devuelve set vacío (se interpreta como "todo").
     */
    private Set<String> parseSeedArgs(String[] args) {
        if (args == null) return Set.of();
        for (String arg : args) {
            if (arg != null && arg.startsWith("--seed=")) {
                String valor = arg.substring("--seed=".length());
                return Arrays.stream(valor.split(","))
                        .map(String::trim)
                        .map(String::toLowerCase)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toCollection(HashSet::new));
            }
        }
        return Set.of();
    }

    /** Lee un argumento simple con prefijo (ej. --tramite=beca) y devuelve su valor. */
    private String parseStringArg(String[] args, String prefijo) {
        if (args == null) return null;
        for (String arg : args) {
            if (arg != null && arg.startsWith(prefijo)) {
                return arg.substring(prefijo.length());
            }
        }
        return null;
    }
}
