package com.jairo.workflowtramites.service.notification;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationPayload {
    private String usuarioId;
    private String titulo;
    private String cuerpo;
    private String solicitudId;
}
