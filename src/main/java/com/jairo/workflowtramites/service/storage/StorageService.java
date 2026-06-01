package com.jairo.workflowtramites.service.storage;

import java.io.InputStream;
import java.time.Duration;

public interface StorageService {

    void subir(String key, InputStream contenido, long tamanoBytes, String contentType);

    String generarUrlPrefirmadaDescarga(String key, Duration duracion);

    InputStream descargar(String key);

    void eliminar(String key);

    boolean existe(String key);
}
