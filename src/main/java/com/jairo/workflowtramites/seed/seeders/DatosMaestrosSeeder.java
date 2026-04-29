package com.jairo.workflowtramites.seed.seeders;

import com.jairo.workflowtramites.model.Departamento;
import com.jairo.workflowtramites.model.FlujoTrabajo;
import com.jairo.workflowtramites.model.FormularioTemplate;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.model.Usuario;
import com.jairo.workflowtramites.model.enums.EstadoFlujo;
import com.jairo.workflowtramites.model.enums.Rol;
import com.jairo.workflowtramites.repository.DepartamentoRepository;
import com.jairo.workflowtramites.repository.FlujoTrabajoRepository;
import com.jairo.workflowtramites.repository.FormularioTemplateRepository;
import com.jairo.workflowtramites.repository.TramiteRepository;
import com.jairo.workflowtramites.repository.UsuarioRepository;
import com.jairo.workflowtramites.seed.config.FormulariosCatalogo;
import com.jairo.workflowtramites.seed.config.SeedConfig;
import com.jairo.workflowtramites.util.ProcesoKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Seed de datos maestros. Cada método público es independiente e idempotente.
 *
 * Se invocan desde SeedRunner con argumentos --seed=departamentos|usuarios|formularios|flujos|tramites|todo.
 *
 * Reglas clave:
 *  - Los formularios NO están atados a departamentos. Son entidades libres. Su asignación
 *    a un userTask ocurre en el BPMN (lo hace el admin o el FlujosSeeder).
 *  - El Tramite sí referencia un formulario específico en Tramite.formularioSolicitanteId
 *    (el que el solicitante rellena al crear la solicitud).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatosMaestrosSeeder {

    private final DepartamentoRepository departamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final FormularioTemplateRepository formularioTemplateRepository;
    private final TramiteRepository tramiteRepository;
    private final FlujoTrabajoRepository flujoTrabajoRepository;
    private final PasswordEncoder passwordEncoder;
    private final SeedConfig seedConfig;
    private final FormulariosCatalogo formulariosCatalogo;

    private final Faker faker = new Faker(new Locale("es"));

    // ==================== DEPARTAMENTOS ====================

    public void sembrarDepartamentos() {
        if (departamentoRepository.count() > 0) {
            log.info("[DatosMaestros/Departamentos] Ya existen, skip.");
            return;
        }

        List<Departamento> creados = new ArrayList<>();
        for (String nombre : seedConfig.getDepartamentos()) {
            creados.add(departamentoRepository.save(Departamento.builder()
                    .nombre(nombre)
                    .activo(true)
                    .build()));
        }
        log.info("[DatosMaestros/Departamentos] {} departamentos creados:", creados.size());
        for (Departamento d : creados) {
            log.info("   - {}  (id: {})", d.getNombre(), d.getId());
        }
    }

    // ==================== USUARIOS ====================

    public void sembrarUsuarios() {
        List<Departamento> deptos = departamentoRepository.findAll();
        if (deptos.isEmpty()) {
            throw new IllegalStateException(
                    "No hay departamentos. Ejecuta primero --seed=departamentos");
        }

        if (usuarioRepository.count() > 0) {
            log.info("[DatosMaestros/Usuarios] Ya existen, skip.");
            return;
        }

        sembrarAdmins();
        sembrarFuncionarios(deptos);
        sembrarSolicitantes();

        log.info("[DatosMaestros/Usuarios] Total usuarios: {}", usuarioRepository.count());
    }

    private void sembrarAdmins() {
        int cantidad = seedConfig.getAdmins();
        List<String> creados = new ArrayList<>();

        // Primer admin con email fijo para testing rápido
        Usuario primero = Usuario.builder()
                .nombre("Administrador Principal")
                .email("admin@workflow.com")
                .password(passwordEncoder.encode("admin123"))
                .rol(Rol.ADMIN)
                .activo(true)
                .build();
        usuarioRepository.save(primero);
        creados.add("Administrador Principal <admin@workflow.com>");

        // Resto con emails admin2, admin3, ...
        for (int i = 2; i <= cantidad; i++) {
            String nombre = faker.name().fullName();
            String email = "admin" + i + "@workflow.com";
            Usuario a = Usuario.builder()
                    .nombre(nombre)
                    .email(email)
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ADMIN)
                    .activo(true)
                    .build();
            usuarioRepository.save(a);
            creados.add(nombre + " <" + email + ">");
        }

        log.info("[DatosMaestros/Usuarios] {} admins creados (password: admin123):", creados.size());
        for (String c : creados) {
            log.info("   - {}", c);
        }
    }

    private void sembrarFuncionarios(List<Departamento> deptos) {
        List<String> ejemplos = new ArrayList<>();
        int total = 0;
        for (Departamento depto : deptos) {
            for (int i = 0; i < seedConfig.getFuncionariosPorDepartamento(); i++) {
                String nombre = faker.name().fullName();
                String email = (nombre.replaceAll("[^A-Za-z]", "").toLowerCase()
                        + i + "@workflow.com");

                Usuario f = Usuario.builder()
                        .nombre(nombre)
                        .email(email)
                        .password(passwordEncoder.encode("funcionario123"))
                        .rol(Rol.FUNCIONARIO)
                        .activo(true)
                        .departamentoId(depto.getId())
                        .build();
                usuarioRepository.save(f);
                total++;

                if (ejemplos.size() < 5) {
                    ejemplos.add(String.format("%s <%s> [%s]",
                            nombre, email, depto.getNombre()));
                }
            }
        }
        log.info("[DatosMaestros/Usuarios] {} funcionarios creados (password: funcionario123). Ejemplos:", total);
        for (String ej : ejemplos) {
            log.info("   - {}", ej);
        }
    }

    private void sembrarSolicitantes() {
        List<String> ejemplos = new ArrayList<>();
        for (int i = 0; i < seedConfig.getSolicitantes(); i++) {
            String nombre = faker.name().fullName();
            String email = (nombre.replaceAll("[^A-Za-z]", "").toLowerCase()
                    + i + "@mail.com");
            String cedula = String.valueOf(faker.number().numberBetween(10_000_000, 99_999_999));

            Usuario s = Usuario.builder()
                    .nombre(nombre)
                    .email(email)
                    .password(passwordEncoder.encode("solicitante123"))
                    .rol(Rol.SOLICITANTE)
                    .activo(true)
                    .telefono(faker.phoneNumber().cellPhone())
                    .direccion(faker.address().fullAddress())
                    .cedula(cedula)
                    .build();
            usuarioRepository.save(s);

            if (ejemplos.size() < 5) {
                ejemplos.add(String.format("%s <%s> cédula: %s", nombre, email, cedula));
            }
        }
        log.info("[DatosMaestros/Usuarios] {} solicitantes creados (password: solicitante123). Ejemplos:",
                seedConfig.getSolicitantes());
        for (String ej : ejemplos) {
            log.info("   - {}", ej);
        }
    }

    // ==================== FORMULARIOS (independientes) ====================

    public void sembrarFormularios() {
        if (formularioTemplateRepository.count() > 0) {
            log.info("[DatosMaestros/Formularios] Ya existen, skip.");
            return;
        }

        List<FormularioTemplate> creados = new ArrayList<>();
        for (String nombre : seedConfig.getFormularios()) {
            FormularioTemplate f = FormularioTemplate.builder()
                    .titulo(nombre)
                    .descripcion("Formulario: " + nombre)
                    .activo(true)
                    .campos(formulariosCatalogo.camposPara(nombre))
                    .build();
            creados.add(formularioTemplateRepository.save(f));
        }

        log.info("[DatosMaestros/Formularios] {} formularios creados:", creados.size());
        for (FormularioTemplate f : creados) {
            log.info("   - {}  (id: {}, {} campos)",
                    f.getTitulo(), f.getId(), f.getCampos().size());
        }
    }

    // ==================== FLUJOS (entidad vacía, sin publicar) ====================

    public void sembrarFlujos() {
        if (flujoTrabajoRepository.count() > 0) {
            log.info("[DatosMaestros/Flujos] Ya existen, skip.");
            return;
        }

        List<FlujoTrabajo> creados = new ArrayList<>();
        for (String nombreTramite : seedConfig.getTramites()) {
            String nombreFlujo = "Flujo " + nombreTramite;
            String procesoKey = ProcesoKeyUtil.generar(nombreFlujo);
            creados.add(flujoTrabajoRepository.save(FlujoTrabajo.builder()
                    .nombre(nombreFlujo)
                    .descripcion("Flujo de trabajo para el trámite: " + nombreTramite)
                    .procesoKey(procesoKey)
                    .estadoFlujo(EstadoFlujo.SIN_PUBLICAR)
                    .build()));
        }
        log.info("[DatosMaestros/Flujos] {} flujos creados (sin publicar). FlujosSeeder los desplegará:",
                creados.size());
        for (FlujoTrabajo f : creados) {
            log.info("   - {}  [procesoKey: {}]  (id: {})",
                    f.getNombre(), f.getProcesoKey(), f.getId());
        }
    }

    // ==================== TRÁMITES ====================

    public void sembrarTramites() {
        if (tramiteRepository.count() > 0) {
            log.info("[DatosMaestros/Tramites] Ya existen, skip.");
            return;
        }

        List<FormularioTemplate> forms = formularioTemplateRepository.findAllByOrderByFechaCreacionAsc();
        if (forms.isEmpty()) {
            throw new IllegalStateException(
                    "No hay formularios. Ejecuta primero --seed=formularios");
        }

        List<FlujoTrabajo> flujos = flujoTrabajoRepository.findAllByOrderByFechaCreacionAsc();
        if (flujos.isEmpty()) {
            throw new IllegalStateException(
                    "No hay flujos. Ejecuta primero --seed=flujos");
        }

        // Formularios "iniciales del solicitante": los que empiezan con "Datos"
        List<FormularioTemplate> formsSolicitante = forms.stream()
                .filter(f -> f.getTitulo() != null
                        && f.getTitulo().toLowerCase().startsWith("datos"))
                .toList();

        if (formsSolicitante.isEmpty()) {
            throw new IllegalStateException(
                    "No hay formularios 'iniciales del solicitante' (los que empiezan con 'Datos'). "
                    + "Revisa la lista de seed.formularios.");
        }

        List<String> nombresTramites = seedConfig.getTramites();
        List<Tramite> creados = new ArrayList<>();

        for (int i = 0; i < nombresTramites.size(); i++) {
            String nombre = nombresTramites.get(i);
            FlujoTrabajo flujo = flujos.get(Math.min(i, flujos.size() - 1));
            FormularioTemplate formSolic = formsSolicitante.get(
                    Math.min(i, formsSolicitante.size() - 1));

            List<String> requisitos = seedConfig.getRequisitosPorTramite()
                    .getOrDefault(nombre, List.of("Copia de cedula"));

            creados.add(tramiteRepository.save(Tramite.builder()
                    .nombre(nombre)
                    .descripcion("Trámite: " + nombre)
                    .formularioSolicitanteId(formSolic.getId())
                    .flujoTrabajoId(flujo.getId())
                    .requisitos(new ArrayList<>(requisitos))
                    .activo(true)
                    .build()));
        }
        log.info("[DatosMaestros/Tramites] {} trámites creados:", creados.size());
        for (Tramite t : creados) {
            log.info("   - {}  (id: {}, flujoId: {}, formSolicitanteId: {})",
                    t.getNombre(), t.getId(), t.getFlujoTrabajoId(), t.getFormularioSolicitanteId());
        }
    }

    // ==================== TODO-EN-UNO ====================

    public void sembrarTodo() {
        sembrarDepartamentos();
        sembrarUsuarios();
        sembrarFormularios();
        sembrarFlujos();
        sembrarTramites();
    }

}
