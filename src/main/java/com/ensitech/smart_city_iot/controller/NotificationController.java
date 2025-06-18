package com.ensitech.smart_city_iot.controller;

import com.ensitech.smart_city_iot.dto.notificationDTO.CreateNotificationDTO;
import com.ensitech.smart_city_iot.dto.notificationDTO.ResponseNotificationDTO;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.service.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/v1")
@Validated
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notifications")
    public ResponseEntity<?> createNotification(@Valid @RequestBody CreateNotificationDTO createDto) {
        try {
            log.info("Création d'une nouvelle notification: {}", createDto.getTitre());
            ResponseNotificationDTO response = notificationService.createNotification(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessException e) {
            log.error("Erreur business lors de la création de la notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.error("Entité non trouvée: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la création de la notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/notifications/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        try {
            log.debug("Demande notification ID: {}", id);
            ResponseNotificationDTO response = notificationService.getNotificationById(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Notification non trouvée ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Notification non trouvée avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la notification ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getAllNotifications() {
        try {
            log.debug("Demande de toutes les notifications");
            List<ResponseNotificationDTO> notifications = notificationService.getAllNotifications();
            return ResponseEntity.ok(Map.of(
                    "notifications", notifications,
                    "total", notifications.size()
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des notifications", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/alertes/{idAlerte}/notifications")
    public ResponseEntity<?> getNotificationsByAlerte(@PathVariable Long idAlerte) {
        try {
            log.debug("Demande notifications pour alerte ID: {}", idAlerte);
            List<ResponseNotificationDTO> notifications = notificationService.getNotificationsByAlerte(idAlerte);
            return ResponseEntity.ok(Map.of(
                    "notifications", notifications,
                    "total", notifications.size(),
                    "alerte_id", idAlerte
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des notifications pour l'alerte ID: {}", idAlerte, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/utilisateurs/{idUtilisateur}/notifications")
    public ResponseEntity<?> getNotificationsByUtilisateur(@PathVariable Long idUtilisateur) {
        try {
            log.debug("Demande notifications pour utilisateur ID: {}", idUtilisateur);
            List<ResponseNotificationDTO> notifications = notificationService.getNotificationsByUtilisateur(idUtilisateur);
            return ResponseEntity.ok(Map.of(
                    "notifications", notifications,
                    "total", notifications.size(),
                    "utilisateur_id", idUtilisateur
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des notifications pour l'utilisateur ID: {}", idUtilisateur, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/utilisateurs/{idUtilisateur}/notifications/non-lues")
    public ResponseEntity<?> getNotificationsNonLues(@PathVariable Long idUtilisateur) {
        try {
            log.debug("Demande notifications non lues pour utilisateur ID: {}", idUtilisateur);
            List<ResponseNotificationDTO> notifications = notificationService.getNotificationsNonLues(idUtilisateur);
            return ResponseEntity.ok(Map.of(
                    "notifications", notifications,
                    "total", notifications.size(),
                    "utilisateur_id", idUtilisateur
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des notifications non lues pour l'utilisateur ID: {}", idUtilisateur, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }


    @PatchMapping("/notifications/{id}/marquer-lu")
    public ResponseEntity<?> marquerCommeLu(@PathVariable Long id) {
        try {
            log.info("Marquage comme lu de la notification ID: {}", id);
            notificationService.marquerCommeLu(id);
            return ResponseEntity.ok(Map.of("message", "Notification marquée comme lue"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative de marquage d'une notification inexistante ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Notification non trouvée avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors du marquage de la notification ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PatchMapping("/notifications/{id}/marquer-envoye")
    public ResponseEntity<?> marquerCommeEnvoye(@PathVariable Long id) {
        try {
            log.info("Marquage comme envoyé de la notification ID: {}", id);
            notificationService.marquerCommeEnvoye(id);
            return ResponseEntity.ok(Map.of("message", "Notification marquée comme envoyée"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative de marquage d'une notification inexistante ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Notification non trouvée avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors du marquage de la notification ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PostMapping("/notifications/{id}/envoyer")
    public ResponseEntity<?> envoyerNotification(@PathVariable Long id) {
        try {
            log.info("Envoi de la notification ID: {}", id);
            notificationService.envoyerNotification(id);
            return ResponseEntity.ok(Map.of("message", "Notification envoyée avec succès"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative d'envoi d'une notification inexistante ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Notification non trouvée avec l'ID: " + id));
        } catch (BusinessException e) {
            log.error("Erreur business lors de l'envoi: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }
}
