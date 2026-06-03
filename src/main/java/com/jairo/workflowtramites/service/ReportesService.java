package com.jairo.workflowtramites.service;

import com.jairo.workflowtramites.dto.response.reportes.DemoraResponse;
import com.jairo.workflowtramites.dto.response.reportes.DepartamentoReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.MetricaTiempoResponse;
import com.jairo.workflowtramites.dto.response.reportes.PaginaResponse;
import com.jairo.workflowtramites.dto.response.reportes.ProductividadDepartamentoResponse;
import com.jairo.workflowtramites.dto.response.reportes.SolicitudReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.TopNItemResponse;
import com.jairo.workflowtramites.dto.response.reportes.TramiteReporteResponse;
import com.jairo.workflowtramites.dto.response.reportes.UsuarioReporteResponse;
import com.jairo.workflowtramites.mapper.ReportesMapper;
import com.jairo.workflowtramites.model.Departamento;
import com.jairo.workflowtramites.model.SolicitudTramite;
import com.jairo.workflowtramites.model.Tramite;
import com.jairo.workflowtramites.model.Usuario;
import com.jairo.workflowtramites.model.embeds.RespuestaDepartamento;
import com.jairo.workflowtramites.model.enums.EstadoTramite;
import com.jairo.workflowtramites.model.enums.Rol;
import com.jairo.workflowtramites.repository.DepartamentoRepository;
import com.jairo.workflowtramites.repository.SolicitudTramiteRepository;
import com.jairo.workflowtramites.repository.TramiteRepository;
import com.jairo.workflowtramites.repository.UsuarioRepository;
import com.jairo.workflowtramites.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportesService {

    private final TramiteRepository tramiteRepository;
    private final DepartamentoRepository departamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudTramiteRepository solicitudTramiteRepository;

    // ---------- 1. TRÁMITES (catálogo) ----------
    public List<TramiteReporteResponse> listarTramites(boolean soloActivos) {
        List<Tramite> tramites = soloActivos
                ? tramiteRepository.findByActivoTrue()
                : tramiteRepository.findAll();
        return tramites.stream()
                .map(t -> ReportesMapper.toTramiteReporteResponse(
                        t, solicitudTramiteRepository.findByTramiteId(t.getId()).size()))
                .toList();
    }

    // ---------- 2. DEPARTAMENTOS (catálogo) ----------
    public List<DepartamentoReporteResponse> listarDepartamentos(boolean soloActivos, AuthenticatedUser ctx) {
        List<Departamento> deptos = departamentoRepository.findAll();
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<SolicitudTramite> solicitudes = solicitudTramiteRepository.findAll();

        return deptos.stream()
                .filter(d -> !soloActivos || d.isActivo())
                .filter(d -> ctx.getRol() != Rol.FUNCIONARIO || d.getId().equals(ctx.getDepartamentoId()))
                .map(d -> {
                    long funcionarios = usuarios.stream()
                            .filter(u -> u.getRol() == Rol.FUNCIONARIO
                                    && d.getId().equals(u.getDepartamentoId())
                                    && u.isActivo())
                            .count();
                    long activas = solicitudes.stream()
                            .filter(s -> s.getDepartamentosActuales() != null
                                    && s.getDepartamentosActuales().contains(d.getId())
                                    && esEstadoActivo(s.getEstado()))
                            .count();
                    return ReportesMapper.toDepartamentoReporteResponse(d, funcionarios, activas);
                })
                .toList();
    }

    // ---------- 3. USUARIOS (catálogo con filtros) ----------
    public List<UsuarioReporteResponse> listarUsuarios(
            Rol rol, String departamentoId, Boolean activo, AuthenticatedUser ctx) {

        Map<String, String> nombresDeptos = departamentoRepository.findAll().stream()
                .collect(Collectors.toMap(Departamento::getId, Departamento::getNombre));

        return usuarioRepository.findAll().stream()
                .filter(u -> rol == null || u.getRol() == rol)
                .filter(u -> departamentoId == null || departamentoId.equals(u.getDepartamentoId()))
                .filter(u -> activo == null || u.isActivo() == activo)
                .filter(u -> ctx.getRol() != Rol.FUNCIONARIO
                        || ctx.getDepartamentoId() == null
                        || ctx.getDepartamentoId().equals(u.getDepartamentoId()))
                .map(u -> ReportesMapper.toUsuarioReporteResponse(
                        u, u.getDepartamentoId() != null ? nombresDeptos.get(u.getDepartamentoId()) : null))
                .toList();
    }

    // ---------- 4. SOLICITUDES (filtros + paginación) ----------
    public PaginaResponse<SolicitudReporteResponse> listarSolicitudes(
            EstadoTramite estado, String tramiteId, String departamentoActualId,
            String solicitanteId, LocalDateTime fechaDesde, LocalDateTime fechaHasta,
            int page, int size, AuthenticatedUser ctx) {

        int tamano = Math.min(Math.max(size, 1), 200);
        int pagina = Math.max(page, 0);

        List<SolicitudTramite> filtradas = solicitudTramiteRepository.findAll().stream()
                .filter(s -> estado == null || s.getEstado() == estado)
                .filter(s -> tramiteId == null || tramiteId.equals(s.getTramiteId()))
                .filter(s -> departamentoActualId == null
                        || (s.getDepartamentosActuales() != null
                            && s.getDepartamentosActuales().contains(departamentoActualId)))
                .filter(s -> solicitanteId == null || solicitanteId.equals(s.getSolicitanteId()))
                .filter(s -> fechaDesde == null
                        || (s.getFechaCreacion() != null && !s.getFechaCreacion().isBefore(fechaDesde)))
                .filter(s -> fechaHasta == null
                        || (s.getFechaCreacion() != null && !s.getFechaCreacion().isAfter(fechaHasta)))
                .filter(s -> filtroPorRol(s, ctx))
                .sorted(Comparator.comparing(SolicitudTramite::getFechaCreacion,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        long total = filtradas.size();
        int totalPaginas = (int) Math.ceil((double) total / tamano);

        List<SolicitudReporteResponse> contenido = filtradas.stream()
                .skip((long) pagina * tamano)
                .limit(tamano)
                .map(ReportesMapper::toSolicitudReporteResponse)
                .toList();

        return PaginaResponse.<SolicitudReporteResponse>builder()
                .contenido(contenido)
                .pagina(pagina)
                .tamano(tamano)
                .total(total)
                .totalPaginas(totalPaginas)
                .build();
    }

    // ---------- 5. TOP N (ranking) ----------
    public List<TopNItemResponse> topN(String entidad, String criterio, int n, String periodo, AuthenticatedUser ctx) {
        int limite = Math.min(Math.max(n, 1), 100);
        LocalDateTime desde = inicioPeriodo(periodo);

        List<SolicitudTramite> solicitudes = solicitudTramiteRepository.findAll().stream()
                .filter(s -> s.getFechaCreacion() != null && !s.getFechaCreacion().isBefore(desde))
                .filter(s -> filtroPorRol(s, ctx))
                .toList();

        return switch (entidad.toLowerCase()) {
            case "tramite" -> topNTramites(solicitudes, criterio, limite);
            case "departamento" -> topNDepartamentos(solicitudes, criterio, limite, ctx);
            case "solicitante" -> topNSolicitantes(solicitudes, criterio, limite);
            default -> throw new IllegalArgumentException("Entidad inválida: " + entidad);
        };
    }

    // ---------- 6. MÉTRICAS DE TIEMPO ----------
    public List<MetricaTiempoResponse> metricasTiempo(
            String agrupacion, String periodo, String entidadId, AuthenticatedUser ctx) {

        LocalDateTime desde = inicioPeriodo(periodo);
        List<SolicitudTramite> solicitudes = solicitudTramiteRepository.findAll().stream()
                .filter(s -> s.getFechaCreacion() != null && !s.getFechaCreacion().isBefore(desde))
                .filter(s -> filtroPorRol(s, ctx))
                .toList();

        return switch (agrupacion.toLowerCase()) {
            case "tramite" -> metricasPorTramite(solicitudes, entidadId);
            case "departamento" -> metricasPorDepartamento(solicitudes, entidadId, ctx);
            case "nodo" -> metricasPorNodo(solicitudes, entidadId);
            default -> throw new IllegalArgumentException("Agrupación inválida: " + agrupacion);
        };
    }

    // ---------- 7. PRODUCTIVIDAD DEPARTAMENTOS ----------
    public List<ProductividadDepartamentoResponse> productividadDepartamentos(String periodo, AuthenticatedUser ctx) {
        LocalDateTime desde = inicioPeriodo(periodo);
        Map<String, String> nombresDeptos = departamentoRepository.findAll().stream()
                .collect(Collectors.toMap(Departamento::getId, Departamento::getNombre));
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<SolicitudTramite> solicitudes = solicitudTramiteRepository.findAll().stream()
                .filter(s -> s.getFechaCreacion() != null && !s.getFechaCreacion().isBefore(desde))
                .toList();

        Map<String, List<RespuestaDepartamento>> respuestasPorDepto = new java.util.HashMap<>();
        for (SolicitudTramite s : solicitudes) {
            if (s.getRespuestasPorDepartamento() == null) continue;
            for (RespuestaDepartamento r : s.getRespuestasPorDepartamento()) {
                if (r.getFechaRespuesta() == null || r.getFechaEntrada() == null) continue;
                respuestasPorDepto.computeIfAbsent(r.getDepartamentoId(), k -> new ArrayList<>()).add(r);
            }
        }

        return respuestasPorDepto.entrySet().stream()
                .filter(e -> ctx.getRol() != Rol.FUNCIONARIO || e.getKey().equals(ctx.getDepartamentoId()))
                .map(e -> {
                    String deptoId = e.getKey();
                    List<RespuestaDepartamento> respuestas = e.getValue();
                    double promHoras = respuestas.stream()
                            .mapToDouble(r -> Duration.between(r.getFechaEntrada(), r.getFechaRespuesta()).toMinutes() / 60.0)
                            .average().orElse(0);
                    long rechazos = respuestas.stream()
                            .filter(r -> "RECHAZAR".equalsIgnoreCase(r.getAccion())).count();
                    double tasaRechazo = respuestas.isEmpty() ? 0.0 : (double) rechazos / respuestas.size();
                    long funcs = usuarios.stream()
                            .filter(u -> u.getRol() == Rol.FUNCIONARIO
                                    && deptoId.equals(u.getDepartamentoId())
                                    && u.isActivo())
                            .count();
                    return ProductividadDepartamentoResponse.builder()
                            .departamentoId(deptoId)
                            .departamentoNombre(nombresDeptos.getOrDefault(deptoId, deptoId))
                            .totalSolicitudesProcesadas(respuestas.size())
                            .tiempoPromedioHoras(redondear(promHoras))
                            .tasaRechazo(redondear(tasaRechazo))
                            .funcionariosActivos(funcs)
                            .build();
                })
                .sorted(Comparator.comparingLong(ProductividadDepartamentoResponse::getTotalSolicitudesProcesadas).reversed())
                .toList();
    }

    // ---------- 8. DEMORAS ----------
    public List<DemoraResponse> demoras(double umbralHoras, EstadoTramite estado, String periodo, AuthenticatedUser ctx) {
        LocalDateTime desde = inicioPeriodo(periodo);
        LocalDateTime ahora = LocalDateTime.now();
        Map<String, String> nombresDeptos = departamentoRepository.findAll().stream()
                .collect(Collectors.toMap(Departamento::getId, Departamento::getNombre));

        EstadoTramite estadoFiltro = estado != null ? estado : EstadoTramite.EN_PROCESO;

        return solicitudTramiteRepository.findAll().stream()
                .filter(s -> s.getEstado() == estadoFiltro)
                .filter(s -> s.getFechaCreacion() != null && !s.getFechaCreacion().isBefore(desde))
                .filter(s -> filtroPorRol(s, ctx))
                .map(s -> {
                    LocalDateTime ref = s.getFechaFinalizacion() != null ? s.getFechaFinalizacion() : ahora;
                    double horas = Duration.between(s.getFechaCreacion(), ref).toMinutes() / 60.0;
                    String deptoId = (s.getDepartamentosActuales() != null && !s.getDepartamentosActuales().isEmpty())
                            ? s.getDepartamentosActuales().get(0) : null;
                    return ReportesMapper.toDemoraResponse(
                            s, deptoId,
                            deptoId != null ? nombresDeptos.getOrDefault(deptoId, deptoId) : null,
                            redondear(horas));
                })
                .filter(d -> d.getHorasTranscurridas() >= umbralHoras)
                .sorted(Comparator.comparingDouble(DemoraResponse::getHorasTranscurridas).reversed())
                .toList();
    }

    // =============== HELPERS ===============

    private static LocalDateTime inicioPeriodo(String periodo) {
        LocalDateTime ahora = LocalDateTime.now();
        if (periodo == null) return ahora.minusMonths(1);
        return switch (periodo.toLowerCase()) {
            case "hoy" -> ahora.toLocalDate().atStartOfDay();
            case "semana" -> ahora.minusDays(7);
            case "mes" -> ahora.minusMonths(1);
            case "trimestre" -> ahora.minusMonths(3);
            case "anio", "año" -> ahora.minusYears(1);
            default -> ahora.minusMonths(1);
        };
    }

    private static boolean esEstadoActivo(EstadoTramite e) {
        return e == EstadoTramite.PENDIENTE || e == EstadoTramite.EN_PROCESO;
    }

    private static boolean filtroPorRol(SolicitudTramite s, AuthenticatedUser ctx) {
        if (ctx.getRol() != Rol.FUNCIONARIO) return true;
        if (ctx.getDepartamentoId() == null) return false;
        // FUNCIONARIO ve solicitudes donde su depto participa (actual o histórico)
        if (s.getDepartamentosActuales() != null
                && s.getDepartamentosActuales().contains(ctx.getDepartamentoId())) return true;
        if (s.getRespuestasPorDepartamento() != null) {
            return s.getRespuestasPorDepartamento().stream()
                    .anyMatch(r -> ctx.getDepartamentoId().equals(r.getDepartamentoId()));
        }
        return false;
    }

    private static double redondear(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static double mediana(List<Double> valores) {
        if (valores.isEmpty()) return 0;
        List<Double> ordenados = valores.stream().sorted().toList();
        int n = ordenados.size();
        return n % 2 == 0
                ? (ordenados.get(n / 2 - 1) + ordenados.get(n / 2)) / 2.0
                : ordenados.get(n / 2);
    }

    private List<TopNItemResponse> topNTramites(List<SolicitudTramite> sols, String criterio, int n) {
        Map<String, List<SolicitudTramite>> porTramite = sols.stream()
                .filter(s -> s.getTramiteId() != null)
                .collect(Collectors.groupingBy(SolicitudTramite::getTramiteId));

        return porTramite.entrySet().stream()
                .map(e -> {
                    String tramiteId = e.getKey();
                    List<SolicitudTramite> lista = e.getValue();
                    String nombre = lista.get(0).getTramiteNombre();
                    double valor = calcularValorRanking(lista, criterio);
                    return TopNItemResponse.builder()
                            .id(tramiteId).nombre(nombre).valor(redondear(valor))
                            .unidad(unidadCriterio(criterio)).build();
                })
                .sorted(Comparator.comparingDouble(TopNItemResponse::getValor).reversed())
                .limit(n)
                .toList();
    }

    private List<TopNItemResponse> topNDepartamentos(
            List<SolicitudTramite> sols, String criterio, int n, AuthenticatedUser ctx) {
        Map<String, String> nombres = departamentoRepository.findAll().stream()
                .collect(Collectors.toMap(Departamento::getId, Departamento::getNombre));

        Map<String, List<SolicitudTramite>> porDepto = new java.util.HashMap<>();
        for (SolicitudTramite s : sols) {
            if (s.getRespuestasPorDepartamento() == null) continue;
            s.getRespuestasPorDepartamento().forEach(r ->
                    porDepto.computeIfAbsent(r.getDepartamentoId(), k -> new ArrayList<>()).add(s));
        }

        return porDepto.entrySet().stream()
                .filter(e -> ctx.getRol() != Rol.FUNCIONARIO || e.getKey().equals(ctx.getDepartamentoId()))
                .map(e -> {
                    String deptoId = e.getKey();
                    double valor = calcularValorRanking(e.getValue(), criterio);
                    return TopNItemResponse.builder()
                            .id(deptoId).nombre(nombres.getOrDefault(deptoId, deptoId))
                            .valor(redondear(valor)).unidad(unidadCriterio(criterio)).build();
                })
                .sorted(Comparator.comparingDouble(TopNItemResponse::getValor).reversed())
                .limit(n)
                .toList();
    }

    private List<TopNItemResponse> topNSolicitantes(List<SolicitudTramite> sols, String criterio, int n) {
        Map<String, List<SolicitudTramite>> porSolicitante = sols.stream()
                .filter(s -> s.getSolicitanteId() != null)
                .collect(Collectors.groupingBy(SolicitudTramite::getSolicitanteId));

        return porSolicitante.entrySet().stream()
                .map(e -> {
                    String nombre = e.getValue().get(0).getSolicitanteNombre();
                    double valor = calcularValorRanking(e.getValue(), criterio);
                    return TopNItemResponse.builder()
                            .id(e.getKey()).nombre(nombre).valor(redondear(valor))
                            .unidad(unidadCriterio(criterio)).build();
                })
                .sorted(Comparator.comparingDouble(TopNItemResponse::getValor).reversed())
                .limit(n)
                .toList();
    }

    private static double calcularValorRanking(List<SolicitudTramite> lista, String criterio) {
        return switch (criterio.toLowerCase()) {
            case "volumen" -> lista.size();
            case "rechazados" -> lista.stream().filter(s -> s.getEstado() == EstadoTramite.RECHAZADO).count();
            case "lentos" -> lista.stream()
                    .filter(s -> s.getFechaCreacion() != null)
                    .mapToDouble(s -> {
                        LocalDateTime ref = s.getFechaFinalizacion() != null
                                ? s.getFechaFinalizacion() : LocalDateTime.now();
                        return Duration.between(s.getFechaCreacion(), ref).toMinutes() / 60.0;
                    })
                    .average().orElse(0);
            default -> throw new IllegalArgumentException("Criterio inválido: " + criterio);
        };
    }

    private static String unidadCriterio(String criterio) {
        return switch (criterio.toLowerCase()) {
            case "volumen", "rechazados" -> "solicitudes";
            case "lentos" -> "horas_promedio";
            default -> "";
        };
    }

    private List<MetricaTiempoResponse> metricasPorTramite(List<SolicitudTramite> sols, String entidadId) {
        return sols.stream()
                .filter(s -> entidadId == null || entidadId.equals(s.getTramiteId()))
                .filter(s -> s.getFechaCreacion() != null && s.getFechaFinalizacion() != null)
                .collect(Collectors.groupingBy(SolicitudTramite::getTramiteId))
                .entrySet().stream()
                .map(e -> {
                    List<Double> horas = e.getValue().stream()
                            .map(s -> Duration.between(s.getFechaCreacion(), s.getFechaFinalizacion()).toMinutes() / 60.0)
                            .toList();
                    return MetricaTiempoResponse.builder()
                            .id(e.getKey())
                            .nombre(e.getValue().get(0).getTramiteNombre())
                            .tiempoPromedioHoras(redondear(horas.stream().mapToDouble(Double::doubleValue).average().orElse(0)))
                            .tiempoMedianaHoras(redondear(mediana(horas)))
                            .tiempoMaxHoras(redondear(horas.stream().mapToDouble(Double::doubleValue).max().orElse(0)))
                            .totalSolicitudes(horas.size())
                            .build();
                })
                .toList();
    }

    private List<MetricaTiempoResponse> metricasPorDepartamento(
            List<SolicitudTramite> sols, String entidadId, AuthenticatedUser ctx) {
        Map<String, String> nombres = departamentoRepository.findAll().stream()
                .collect(Collectors.toMap(Departamento::getId, Departamento::getNombre));

        Map<String, List<Double>> horasPorDepto = new java.util.HashMap<>();
        for (SolicitudTramite s : sols) {
            if (s.getRespuestasPorDepartamento() == null) continue;
            for (RespuestaDepartamento r : s.getRespuestasPorDepartamento()) {
                if (r.getFechaEntrada() == null || r.getFechaRespuesta() == null) continue;
                if (entidadId != null && !entidadId.equals(r.getDepartamentoId())) continue;
                if (ctx.getRol() == Rol.FUNCIONARIO
                        && !ctx.getDepartamentoId().equals(r.getDepartamentoId())) continue;
                double h = Duration.between(r.getFechaEntrada(), r.getFechaRespuesta()).toMinutes() / 60.0;
                horasPorDepto.computeIfAbsent(r.getDepartamentoId(), k -> new ArrayList<>()).add(h);
            }
        }

        return horasPorDepto.entrySet().stream()
                .map(e -> MetricaTiempoResponse.builder()
                        .id(e.getKey())
                        .nombre(nombres.getOrDefault(e.getKey(), e.getKey()))
                        .tiempoPromedioHoras(redondear(e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0)))
                        .tiempoMedianaHoras(redondear(mediana(e.getValue())))
                        .tiempoMaxHoras(redondear(e.getValue().stream().mapToDouble(Double::doubleValue).max().orElse(0)))
                        .totalSolicitudes(e.getValue().size())
                        .build())
                .toList();
    }

    private List<MetricaTiempoResponse> metricasPorNodo(List<SolicitudTramite> sols, String entidadId) {
        Map<String, List<Double>> horasPorNodo = new java.util.HashMap<>();
        Map<String, String> nombresNodo = new java.util.HashMap<>();
        for (SolicitudTramite s : sols) {
            if (s.getRespuestasPorDepartamento() == null) continue;
            for (RespuestaDepartamento r : s.getRespuestasPorDepartamento()) {
                if (r.getFechaEntrada() == null || r.getFechaRespuesta() == null || r.getElementId() == null) continue;
                if (entidadId != null && !entidadId.equals(r.getElementId())) continue;
                double h = Duration.between(r.getFechaEntrada(), r.getFechaRespuesta()).toMinutes() / 60.0;
                horasPorNodo.computeIfAbsent(r.getElementId(), k -> new ArrayList<>()).add(h);
                nombresNodo.putIfAbsent(r.getElementId(),
                        r.getDepartamentoNombre() != null ? r.getDepartamentoNombre() : r.getElementId());
            }
        }

        return horasPorNodo.entrySet().stream()
                .map(e -> MetricaTiempoResponse.builder()
                        .id(e.getKey())
                        .nombre(nombresNodo.get(e.getKey()))
                        .tiempoPromedioHoras(redondear(e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0)))
                        .tiempoMedianaHoras(redondear(mediana(e.getValue())))
                        .tiempoMaxHoras(redondear(e.getValue().stream().mapToDouble(Double::doubleValue).max().orElse(0)))
                        .totalSolicitudes(e.getValue().size())
                        .build())
                .toList();
    }
}
