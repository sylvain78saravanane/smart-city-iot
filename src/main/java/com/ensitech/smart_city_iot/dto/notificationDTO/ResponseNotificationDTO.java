package com.ensitech.smart_city_iot.dto.notificationDTO;

import com.ensitech.smart_city_iot.entity.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ResponseNotificationDTO {

    private Long idNotification;
    private String titre;
    private String message;
    private String typeNotification;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime dateEnvoi;
    private Boolean lu;

    // Informations de l'alerte associée
    private Long idAlerte;
    private String titreAlerte;
    private String prioriteAlerte;

    // Informations des utilisateurs
    private List<UtilisateurSimpleDTO> utilisateurs;
    private int nombreUtilisateurs;

    public static ResponseNotificationDTO fromEntity(Notification notification) {
        ResponseNotificationDTOBuilder builder = ResponseNotificationDTO.builder()
                .idNotification(notification.getIdNotification())
                .titre(notification.getTitre())
                .message(notification.getMessage())
                .typeNotification(notification.getTypeNotification())
                .statut(notification.getStatut())
                .dateCreation(notification.getDateCreation())
                .dateEnvoi(notification.getDateEnvoi())
                .lu(notification.getLu());

        // Informations de l'alerte
        if (notification.getAlerte() != null) {
            builder.idAlerte(notification.getAlerte().getIdAlerte())
                    .titreAlerte(notification.getAlerte().getTitre())
                    .prioriteAlerte(notification.getAlerte().getPriorite());
        }

        // Informations des utilisateurs
        if (notification.getUtilisateurs() != null) {
            List<UtilisateurSimpleDTO> utilisateursDTO = notification.getUtilisateurs().stream()
                    .map(u -> UtilisateurSimpleDTO.builder()
                            .idUtilisateur(u.getIdUtilisateur())
                            .nomComplet(u.getNomComplet())
                            .email(u.getEmail())
                            .typeUtilisateur(u.getRole())
                            .build())
                    .collect(Collectors.toList());

            builder.utilisateurs(utilisateursDTO)
                    .nombreUtilisateurs(utilisateursDTO.size());
        } else {
            builder.nombreUtilisateurs(0);
        }

        return builder.build();
    }

    @Builder
    @Data
    public static class UtilisateurSimpleDTO {
        private Long idUtilisateur;
        private String nomComplet;
        private String email;
        private String typeUtilisateur;
    }
}
