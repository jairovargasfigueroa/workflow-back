package com.jairo.workflowtramites.service.notification;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.jairo.workflowtramites.model.Usuario;
import com.jairo.workflowtramites.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmNotificationService implements NotificationService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Async
    public void enviar(NotificationPayload payload) {
        if (FirebaseApp.getApps().isEmpty()) {
            return;
        }

        Usuario usuario = usuarioRepository.findById(payload.getUsuarioId()).orElse(null);
        if (usuario == null || usuario.getFcmToken() == null || usuario.getFcmToken().isBlank()) {
            return;
        }

        Message message = Message.builder()
                .setToken(usuario.getFcmToken())
                .setNotification(Notification.builder()
                        .setTitle(payload.getTitulo())
                        .setBody(payload.getCuerpo())
                        .build())
                .putData("solicitudId", payload.getSolicitudId() == null ? "" : payload.getSolicitudId())
                .putData("tipo", "ESTADO_CAMBIADO")
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                usuario.setFcmToken(null);
                usuarioRepository.save(usuario);
            } else {
                log.warn("Error enviando notificacion FCM a usuario {}: {}", usuario.getId(), e.getMessage());
            }
        }
    }
}
