package com.jairo.workflowtramites.seed.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "seed")
public class SeedConfig {

    /** Cantidad de solicitudes a generar. */
    private int cantidadSolicitudes = 2000;

    /** Distribución temporal: cuántos días atrás pueden haberse creado las solicitudes. */
    private int distribuirUltimosDias = 180;

    /** Cantidad de admins a generar. */
    private int admins = 5;

    /** Cantidad de funcionarios a generar por cada departamento. */
    private int funcionariosPorDepartamento = 5;

    /** Cantidad de solicitantes a generar. */
    private int solicitantes = 250;

    /** Departamentos a crear. Dominio: Universidad. */
    private List<String> departamentos = List.of(
            "Secretaria Academica",
            "Decanato",
            "Bienestar Estudiantil",
            "Financiero",
            "Biblioteca",
            "Tesoreria",
            "Registro",
            "Investigacion",
            "Posgrado",
            "Direccion de Carrera"
    );

    /** Trámites a crear (1:1 con flujos). Dominio: Universidad. */
    private List<String> tramites = List.of(
            "Solicitud de certificado de notas",
            "Solicitud de egresamiento",
            "Tramite de titulacion",
            "Solicitud de beca",
            "Convalidacion de materias",
            "Retiro de materia",
            "Justificacion de falta",
            "Cambio de carrera",
            "Carta de honorabilidad",
            "Permiso de investigacion"
    );

    /**
     * Formularios a crear.
     *
     * Los primeros 10 son "de proceso" — se asignan a userTask en el BPMN (lo hace el admin
     * o el FlujosSeeder).
     *
     * Los últimos 10 son "iniciales del solicitante" — uno por trámite, en orden
     * correspondiente con la lista de trámites (índice N del trámite usa el formulario
     * en el índice N + 10).
     */
    private List<String> formularios = List.of(
            // 0-9: formularios de proceso (para userTask del BPMN)
            "Revision academica",
            "Dictamen del decano",
            "Evaluacion socioeconomica",
            "Validacion de pagos",
            "Verificacion de biblioteca",
            "Informe de registro",
            "Aprobacion de investigacion",
            "Revision de posgrado",
            "Aprobacion de direccion de carrera",
            "Observaciones academicas",

            // 10-19: formularios iniciales del solicitante (uno por trámite)
            "Datos para certificado de notas",
            "Datos de egresamiento",
            "Datos de titulacion",
            "Datos de solicitud de beca",
            "Datos de convalidacion",
            "Datos de retiro de materia",
            "Datos de justificacion",
            "Datos de cambio de carrera",
            "Datos de carta de honorabilidad",
            "Datos de permiso de investigacion"
    );

    /**
     * Requisitos específicos por trámite. Cada trámite tiene una lista coherente
     * con su naturaleza (beca pide socioeconómico, titulación pide certificado de
     * prácticas, etc.).
     */
    private Map<String, List<String>> requisitosPorTramite = Map.ofEntries(
            Map.entry("Solicitud de certificado de notas", List.of(
                    "Copia de cedula",
                    "Comprobante de pago de especie valorada",
                    "Certificado de matricula"
            )),
            Map.entry("Solicitud de egresamiento", List.of(
                    "Copia de cedula",
                    "Record academico",
                    "Certificado de no adeudar a biblioteca",
                    "Comprobante de pago de especie valorada",
                    "Carta dirigida al decano"
            )),
            Map.entry("Tramite de titulacion", List.of(
                    "Copia de cedula",
                    "Record academico",
                    "Certificado de no adeudar a biblioteca",
                    "Certificado de practicas preprofesionales",
                    "Comprobante de pago de especie valorada",
                    "Carta dirigida al decano"
            )),
            Map.entry("Solicitud de beca", List.of(
                    "Copia de cedula",
                    "Certificado socioeconomico",
                    "Record academico",
                    "Certificado medico",
                    "Fotografias tamano carnet"
            )),
            Map.entry("Convalidacion de materias", List.of(
                    "Copia de cedula",
                    "Record academico de universidad de origen",
                    "Syllabus de materias a convalidar",
                    "Certificado de matricula actual",
                    "Comprobante de pago de especie valorada"
            )),
            Map.entry("Retiro de materia", List.of(
                    "Copia de cedula",
                    "Certificado de matricula",
                    "Carta dirigida al decano"
            )),
            Map.entry("Justificacion de falta", List.of(
                    "Copia de cedula",
                    "Certificado medico",
                    "Carta dirigida al decano"
            )),
            Map.entry("Cambio de carrera", List.of(
                    "Copia de cedula",
                    "Record academico",
                    "Carta dirigida al decano",
                    "Certificado de matricula actual",
                    "Comprobante de pago de especie valorada"
            )),
            Map.entry("Carta de honorabilidad", List.of(
                    "Copia de cedula",
                    "Certificado de matricula",
                    "Carta dirigida al decano"
            )),
            Map.entry("Permiso de investigacion", List.of(
                    "Copia de cedula",
                    "Record academico",
                    "Carta dirigida al decano",
                    "Propuesta de investigacion firmada"
            ))
    );

    /** Configuración por depto (duraciones y tasas) para sesgos realistas en el modelo ML. */
    private Map<String, ConfigDepto> configPorDepto = Map.ofEntries(
            Map.entry("Secretaria Academica",  new ConfigDepto(10, 4 * 60,    20, 4 * 60,    0.03, 0.05)),
            Map.entry("Decanato",              new ConfigDepto(60, 48 * 60,   120, 24 * 60,  0.10, 0.08)),
            Map.entry("Bienestar Estudiantil", new ConfigDepto(30, 24 * 60,   60, 12 * 60,   0.15, 0.10)),
            Map.entry("Financiero",            new ConfigDepto(120, 72 * 60,  240, 48 * 60,  0.20, 0.05)),
            Map.entry("Biblioteca",            new ConfigDepto(5, 60,         10, 90,        0.02, 0.00)),
            Map.entry("Tesoreria",             new ConfigDepto(30, 12 * 60,   60, 8 * 60,    0.05, 0.02)),
            Map.entry("Registro",              new ConfigDepto(15, 6 * 60,    30, 4 * 60,    0.04, 0.03)),
            Map.entry("Investigacion",         new ConfigDepto(120, 96 * 60,  240, 60 * 60,  0.18, 0.12)),
            Map.entry("Posgrado",              new ConfigDepto(60, 48 * 60,   120, 24 * 60,  0.12, 0.08)),
            Map.entry("Direccion de Carrera",  new ConfigDepto(60, 36 * 60,   120, 20 * 60,  0.08, 0.05))
    );

    public int getCantidadSolicitudes()        { return cantidadSolicitudes; }
    public void setCantidadSolicitudes(int v)  { this.cantidadSolicitudes = v; }
    public int getDistribuirUltimosDias()      { return distribuirUltimosDias; }
    public void setDistribuirUltimosDias(int v){ this.distribuirUltimosDias = v; }
    public int getAdmins()                     { return admins; }
    public void setAdmins(int v)               { this.admins = v; }
    public int getFuncionariosPorDepartamento(){ return funcionariosPorDepartamento; }
    public void setFuncionariosPorDepartamento(int v) { this.funcionariosPorDepartamento = v; }
    public int getSolicitantes()               { return solicitantes; }
    public void setSolicitantes(int v)         { this.solicitantes = v; }
    public List<String> getDepartamentos()     { return departamentos; }
    public void setDepartamentos(List<String> v) { this.departamentos = v; }
    public List<String> getTramites()          { return tramites; }
    public void setTramites(List<String> v)    { this.tramites = v; }
    public List<String> getFormularios()       { return formularios; }
    public void setFormularios(List<String> v) { this.formularios = v; }
    public Map<String, List<String>> getRequisitosPorTramite() { return requisitosPorTramite; }
    public void setRequisitosPorTramite(Map<String, List<String>> v) { this.requisitosPorTramite = v; }
    public Map<String, ConfigDepto> getConfigPorDepto() { return configPorDepto; }
    public void setConfigPorDepto(Map<String, ConfigDepto> v) { this.configPorDepto = v; }
}
