package com.jairo.workflowtramites.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jairo.workflowtramites.model.embeds.CampoFormulario;
import com.jairo.workflowtramites.model.embeds.ConfiguracionDocumental;
import com.jairo.workflowtramites.model.embeds.NodoFlujo;
import com.jairo.workflowtramites.model.embeds.TransicionFlujo;
import com.jairo.workflowtramites.model.enums.AccionFlujo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class BpmnParserService {

    private static final String NS_CAMUNDA = "http://camunda.org/schema/1.0/bpmn";
    private static final List<String> TIPOS_NODO = List.of("startEvent", "endEvent", "userTask", "exclusiveGateway", "parallelGateway");
    private static final String PROP_DEPARTAMENTO_ID = "departamentoId";
    private static final String PROP_CONFIGURACION_DOCUMENTAL = "configuracionDocumental";
    private static final String PROP_CAMPOS_FORMULARIO = "camposFormulario";
    private static final String PROP_SLA_NODO_HORAS = "slaNodoHoras";

    // Tolerante a campos desconocidos en el JSON de configuracionDocumental
    // (ej. campos viejos como "documentosEsperados" que el front aun pueda enviar).
    // Sin esto, un campo extra hace fallar la deserializacion y se descarta TODA la config.
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public List<NodoFlujo> parsear(String xml) {
        Document doc = parsearXml(xml);
        Map<String, List<TransicionFlujo>> transicionesPorOrigen = construirMapaTransiciones(doc);
        Map<String, InfoCarril> carrilesPorNodo = extraerInfoCarrilesPorNodo(doc);
        List<NodoFlujo> nodos = new ArrayList<>();

        for (String tipo : TIPOS_NODO) {
            NodeList elementos = doc.getElementsByTagNameNS("*", tipo);
            for (int i = 0; i < elementos.getLength(); i++) {
                Element el = (Element) elementos.item(i);
                String elementId = el.getAttribute("id");
                String nombre = el.getAttribute("name");

                String departamentoId = null;
                String formularioId = null;
                String carrilId = null;
                String carrilNombre = null;
                ConfiguracionDocumental configuracionDocumental = null;
                List<CampoFormulario> camposFormulario = null;
                Integer slaNodoHoras = null;

                InfoCarril infoCarril = carrilesPorNodo.get(elementId);
                if (infoCarril != null) {
                    carrilId = infoCarril.id;
                    carrilNombre = infoCarril.nombre;
                }

                if ("userTask".equals(tipo)) {
                    if (infoCarril != null && infoCarril.departamentoId != null) {
                        departamentoId = infoCarril.departamentoId;
                    } else {
                        departamentoId = obtenerAtributoCamunda(el, "candidateGroups");
                    }
                    formularioId = obtenerAtributoCamunda(el, "formKey");
                    configuracionDocumental = extraerConfiguracionDocumental(el);
                    camposFormulario = extraerCamposFormulario(el);   // override inline (si el editor los puso en el XML)
                    slaNodoHoras = extraerSlaNodoHoras(el);
                } else if ("startEvent".equals(tipo)) {
                    // El nodo inicial lleva los documentos que el SOLICITANTE sube al crear el
                    // tramite (el "kit"). Misma estructura que documentosProducidos de un userTask.
                    configuracionDocumental = extraerConfiguracionDocumental(el);
                }

                nodos.add(NodoFlujo.builder()
                        .elementId(elementId)
                        .tipo(tipo)
                        .nombre(nombre.isBlank() ? elementId : nombre)
                        .departamentoId(departamentoId)
                        .carrilId(carrilId)
                        .carrilNombre(carrilNombre)
                        .formularioId(formularioId)
                        .camposFormulario(camposFormulario)
                        .configuracionDocumental(configuracionDocumental)
                        .slaNodoHoras(slaNodoHoras)
                        .transiciones(transicionesPorOrigen.getOrDefault(elementId, List.of()))
                        .build());
            }
        }
        return nodos;
    }

    private Map<String, InfoCarril> extraerInfoCarrilesPorNodo(Document doc) {
        Map<String, InfoCarril> mapa = new HashMap<>();
        NodeList lanes = doc.getElementsByTagNameNS("*", "lane");

        for (int i = 0; i < lanes.getLength(); i++) {
            Element lane = (Element) lanes.item(i);
            String carrilId = lane.getAttribute("id");
            String carrilNombre = lane.getAttribute("name");
            String departamentoId = obtenerDepartamentoIdDelCarril(lane);

            InfoCarril info = new InfoCarril();
            info.id = carrilId;
            info.nombre = carrilNombre.isBlank() ? carrilId : carrilNombre;
            info.departamentoId = departamentoId;

            NodeList refs = lane.getElementsByTagNameNS("*", "flowNodeRef");
            for (int j = 0; j < refs.getLength(); j++) {
                String elementId = refs.item(j).getTextContent().trim();
                if (!elementId.isBlank()) {
                    mapa.put(elementId, info);
                }
            }
        }
        return mapa;
    }

    private String obtenerDepartamentoIdDelCarril(Element lane) {
        return obtenerValorPropiedadCamunda(lane, PROP_DEPARTAMENTO_ID);
    }

    private String obtenerValorPropiedadCamunda(Element el, String nombrePropiedad) {
        NodeList propiedades = el.getElementsByTagNameNS(NS_CAMUNDA, "property");
        for (int i = 0; i < propiedades.getLength(); i++) {
            Element prop = (Element) propiedades.item(i);
            if (nombrePropiedad.equals(prop.getAttribute("name"))) {
                String valor = prop.getAttribute("value");
                if (valor != null && !valor.isBlank()) return valor;
            }
        }
        return null;
    }

    private ConfiguracionDocumental extraerConfiguracionDocumental(Element userTask) {
        String json = obtenerValorPropiedadCamunda(userTask, PROP_CONFIGURACION_DOCUMENTAL);
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, ConfiguracionDocumental.class);
        } catch (Exception e) {
            log.warn("No se pudo deserializar configuracionDocumental del nodo '{}': {}",
                    userTask.getAttribute("id"), e.getMessage());
            return null;
        }
    }

    /**
     * Campos del formulario embebidos en el XML (override "inline"): el editor define/edita los
     * campos en la config del nodo y los guarda como JSON en un camunda:property. Si estan, se usan
     * tal cual (snapshot en la version); si no, FlujoTrabajoService copia los del FormularioTemplate.
     */
    private List<CampoFormulario> extraerCamposFormulario(Element userTask) {
        String json = obtenerValorPropiedadCamunda(userTask, PROP_CAMPOS_FORMULARIO);
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<List<CampoFormulario>>() {});
        } catch (Exception e) {
            log.warn("No se pudo deserializar camposFormulario del nodo '{}': {}",
                    userTask.getAttribute("id"), e.getMessage());
            return null;
        }
    }

    private Integer extraerSlaNodoHoras(Element userTask) {
        String valor = obtenerValorPropiedadCamunda(userTask, PROP_SLA_NODO_HORAS);
        if (valor == null || valor.isBlank()) return null;
        try {
            int horas = Integer.parseInt(valor.trim());
            return horas > 0 ? horas : null;
        } catch (NumberFormatException e) {
            log.warn("slaNodoHoras inválido en nodo '{}': '{}'", userTask.getAttribute("id"), valor);
            return null;
        }
    }

    private static class InfoCarril {
        String id;
        String nombre;
        String departamentoId;
    }

    public List<String> validar(String xml) {
        List<String> errores = new ArrayList<>();
        try {
            Document doc = parsearXml(xml);
            Map<String, List<TransicionFlujo>> transicionesPorOrigen = construirMapaTransiciones(doc);
            Map<String, InfoCarril> carrilesPorNodo = extraerInfoCarrilesPorNodo(doc);

            if (doc.getElementsByTagNameNS("*", "startEvent").getLength() == 0)
                errores.add("El diagrama no tiene evento de inicio");

            if (doc.getElementsByTagNameNS("*", "endEvent").getLength() == 0)
                errores.add("El diagrama no tiene evento de fin");

            NodeList lanes = doc.getElementsByTagNameNS("*", "lane");
            for (int i = 0; i < lanes.getLength(); i++) {
                Element lane = (Element) lanes.item(i);
                String nombreLane = lane.getAttribute("name");
                String etiquetaLane = nombreLane.isBlank() ? lane.getAttribute("id") : nombreLane;
                if (obtenerDepartamentoIdDelCarril(lane) == null) {
                    errores.add("El carril '" + etiquetaLane + "' no tiene departamento asignado");
                }
            }

            Set<String> elementosConEntrada = new HashSet<>();
            NodeList flujos = doc.getElementsByTagNameNS("*", "sequenceFlow");
            for (int i = 0; i < flujos.getLength(); i++) {
                elementosConEntrada.add(((Element) flujos.item(i)).getAttribute("targetRef"));
            }

            NodeList tareas = doc.getElementsByTagNameNS("*", "userTask");
            for (int i = 0; i < tareas.getLength(); i++) {
                Element tarea = (Element) tareas.item(i);
                String elementId = tarea.getAttribute("id");
                String etiqueta = label(tarea);

                InfoCarril infoCarril = carrilesPorNodo.get(elementId);
                boolean tieneDeptoEnCarril = infoCarril != null && infoCarril.departamentoId != null;
                boolean tieneDeptoDirecto = obtenerAtributoCamunda(tarea, "candidateGroups") != null;

                if (!tieneDeptoEnCarril && !tieneDeptoDirecto)
                    errores.add("La tarea '" + etiqueta + "' no tiene departamento asignado (ni por carril ni directo)");

                // El formulario puede venir por plantilla (formKey) O por campos inline en el XML.
                if (obtenerAtributoCamunda(tarea, "formKey") == null && extraerCamposFormulario(tarea) == null)
                    errores.add("La tarea '" + etiqueta + "' no tiene formulario asignado (ni plantilla ni campos)");

                if (!elementosConEntrada.contains(elementId))
                    errores.add("La tarea '" + etiqueta + "' no está conectada al flujo");
            }

            NodeList gateways = doc.getElementsByTagNameNS("*", "exclusiveGateway");
            for (int i = 0; i < gateways.getLength(); i++) {
                Element gw = (Element) gateways.item(i);
                String elementId = gw.getAttribute("id");
                String etiqueta = label(gw);

                if (transicionesPorOrigen.getOrDefault(elementId, List.of()).size() < 2)
                    errores.add("El punto de decisión '" + etiqueta + "' tiene menos de 2 salidas");

                if (!elementosConEntrada.contains(elementId))
                    errores.add("El punto de decisión '" + etiqueta + "' no está conectado al flujo");
            }

            NodeList parallelGws = doc.getElementsByTagNameNS("*", "parallelGateway");
            for (int i = 0; i < parallelGws.getLength(); i++) {
                Element gw = (Element) parallelGws.item(i);
                String elementId = gw.getAttribute("id");
                String etiqueta = label(gw);

                if (!elementosConEntrada.contains(elementId))
                    errores.add("La compuerta paralela '" + etiqueta + "' no está conectada al flujo");

                boolean tieneDosOmasSalidas = transicionesPorOrigen.getOrDefault(elementId, List.of()).size() >= 2;
                long entradas = 0;
                for (int j = 0; j < flujos.getLength(); j++) {
                    if (elementId.equals(((Element) flujos.item(j)).getAttribute("targetRef"))) entradas++;
                }
                if (!tieneDosOmasSalidas && entradas < 2)
                    errores.add("La compuerta paralela '" + etiqueta + "' necesita al menos 2 entradas (join) o 2 salidas (fork)");
            }

            // Catalogo de acciones: las salidas de un punto de decision deben usar una accion conocida.
            for (int g = 0; g < gateways.getLength(); g++) {
                Element gw = (Element) gateways.item(g);
                String etiquetaGw = label(gw);
                for (TransicionFlujo t : transicionesPorOrigen.getOrDefault(gw.getAttribute("id"), List.of())) {
                    if (t.getValor() != null && !AccionFlujo.valoresValidos().contains(t.getValor())) {
                        errores.add("La salida '" + (t.getEtiqueta() != null ? t.getEtiqueta() : t.getValor())
                                + "' del punto de decision '" + etiquetaGw
                                + "' no es una accion valida (use: Aprobado, Rechazado, Observado, Corregido, Cancelado)");
                    }
                }
            }

            // Los eventos de fin deben nombrarse con un estado final del catalogo (define el estado del tramite).
            NodeList endEvents = doc.getElementsByTagNameNS("*", "endEvent");
            for (int k = 0; k < endEvents.getLength(); k++) {
                Element endEvent = (Element) endEvents.item(k);
                String nombreEnd = endEvent.getAttribute("name");
                String valorEnd = normalizarValor(nombreEnd, endEvent.getAttribute("id"));
                if (nombreEnd.isBlank() || !AccionFlujo.valoresFinales().contains(valorEnd)) {
                    errores.add("El evento de fin '" + label(endEvent)
                            + "' debe llamarse 'Aprobado', 'Rechazado' o 'Cancelado'");
                }
            }

        } catch (Exception e) {
            errores.add("El XML no es un BPMN válido: " + e.getMessage());
        }
        return errores;
    }

    public String inyectarCandidateGroupsDesdeLanes(String xml) {
        Document doc = parsearXml(xml);
        Map<String, InfoCarril> carrilesPorNodo = extraerInfoCarrilesPorNodo(doc);
        if (carrilesPorNodo.isEmpty()) return xml;

        NodeList tareas = doc.getElementsByTagNameNS("*", "userTask");
        boolean modificado = false;

        for (int i = 0; i < tareas.getLength(); i++) {
            Element tarea = (Element) tareas.item(i);
            String elementId = tarea.getAttribute("id");

            if (obtenerAtributoCamunda(tarea, "candidateGroups") != null) continue;

            InfoCarril infoCarril = carrilesPorNodo.get(elementId);
            if (infoCarril == null || infoCarril.departamentoId == null) continue;

            tarea.setAttributeNS(NS_CAMUNDA, "camunda:candidateGroups", infoCarril.departamentoId);
            modificado = true;
        }

        return modificado ? serializarXml(doc) : xml;
    }

    public String inyectarCondiciones(String xml) {
        Document doc = parsearXml(xml);

        // Solo las salidas de un exclusiveGateway son DECISIONES (llevan ${accion == 'valor'}).
        // Cualquier otra flecha con nombre (secuencial/paralela, o con un name viejo) NO recibe
        // condicion: avanza directo. Asi un nombre viejo en una flecha secuencial NO atasca el flujo.
        Set<String> exclusiveGateways = new HashSet<>();
        NodeList gws = doc.getElementsByTagNameNS("*", "exclusiveGateway");
        for (int i = 0; i < gws.getLength(); i++) {
            exclusiveGateways.add(((Element) gws.item(i)).getAttribute("id"));
        }

        NodeList flujos = doc.getElementsByTagNameNS("*", "sequenceFlow");

        for (int i = 0; i < flujos.getLength(); i++) {
            Element flow = (Element) flujos.item(i);
            String nombre = flow.getAttribute("name");

            if (nombre.isBlank()) continue;
            if (!exclusiveGateways.contains(flow.getAttribute("sourceRef"))) continue;
            if (flow.getElementsByTagNameNS("*", "conditionExpression").getLength() > 0) continue;

            String valor = normalizarValor(nombre, flow.getAttribute("id"));
            String prefix = flow.getPrefix();
            String ns = flow.getNamespaceURI();
            String tagName = prefix != null ? prefix + ":conditionExpression" : "conditionExpression";

            Element condicion = doc.createElementNS(ns, tagName);
            String tipoFormal = prefix != null ? prefix + ":tFormalExpression" : "tFormalExpression";
            condicion.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", tipoFormal);
            condicion.setTextContent("${accion == '" + valor + "'}");
            flow.appendChild(condicion);
        }

        return serializarXml(doc);
    }

    private String serializarXml(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar XML: " + e.getMessage(), e);
        }
    }

    private Document parsearXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear el XML BPMN: " + e.getMessage(), e);
        }
    }

    private Map<String, List<TransicionFlujo>> construirMapaTransiciones(Document doc) {
        Map<String, List<TransicionFlujo>> mapa = new HashMap<>();
        NodeList flujos = doc.getElementsByTagNameNS("*", "sequenceFlow");
        for (int i = 0; i < flujos.getLength(); i++) {
            Element flow = (Element) flujos.item(i);
            String sourceRef = flow.getAttribute("sourceRef");
            String targetRef = flow.getAttribute("targetRef");
            String etiqueta = flow.getAttribute("name");
            String flowId = flow.getAttribute("id");

            mapa.computeIfAbsent(sourceRef, k -> new ArrayList<>())
                    .add(TransicionFlujo.builder()
                            .targetId(targetRef)
                            .etiqueta(etiqueta.isBlank() ? "Continuar" : etiqueta)
                            .valor(normalizarValor(etiqueta, flowId))
                            .build());
        }
        return mapa;
    }

    private String obtenerAtributoCamunda(Element el, String nombreLocal) {
        String valor = el.getAttributeNS(NS_CAMUNDA, nombreLocal);
        if (valor == null || valor.isBlank()) {
            valor = el.getAttribute("camunda:" + nombreLocal);
        }
        return (valor == null || valor.isBlank()) ? null : valor;
    }

    private String normalizarValor(String etiqueta, String flowId) {
        // Sin "name" = flecha SECUENCIAL/paralela (no es una decision del usuario). Devolvemos
        // "avanzar" (no el flowId feo): el funcionario ve "Continuar" y completa. Camunda NO le pone
        // condicion a estas flechas (inyectarCondiciones las salta), asi que "avanzar" no se evalua.
        if (etiqueta == null || etiqueta.isBlank()) return "avanzar";
        return etiqueta.trim().toLowerCase()
                .replace("á", "a").replace("é", "e").replace("í", "i")
                .replace("ó", "o").replace("ú", "u").replace("ü", "u")
                .replace("ñ", "n")
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }

    private String label(Element el) {
        String nombre = el.getAttribute("name");
        return nombre.isBlank() ? el.getAttribute("id") : nombre;
    }
}
