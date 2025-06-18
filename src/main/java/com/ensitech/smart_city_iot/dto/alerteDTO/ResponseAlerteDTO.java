package com.ensitech.smart_city_iot.dto.alerteDTO;

import com.ensitech.smart_city_iot.entity.Alerte;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ResponseAlerteDTO {
    private Long idAlerte;
    private String titre;
    private String description;
    private Double seuilValeur;
    private String typeCondition;
    private String priorite;
    private Boolean active;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Informations du capteur associé
    private Long idCapteur;
    private String nomCapteur;
    private String typeCapteur;

    // Statistiques
    private int nombreNotifications;

    public static ResponseAlerteDTO fromEntity(Alerte alerte) {
        ResponseAlerteDTOBuilder builder = ResponseAlerteDTO.builder()
                .idAlerte(alerte.getIdAlerte())
                .titre(alerte.getTitre())
                .description(alerte.getDescription())
                .seuilValeur(alerte.getSeuilValeur())
                .typeCondition(alerte.getTypeCondition())
                .priorite(alerte.getPriorite())
                .active(alerte.getActive())
                .dateCreation(alerte.getDateCreation())
                .dateModification(alerte.getDateModification());

        // Informations du capteur
        if (alerte.getCapteur() != null) {
            builder.idCapteur(alerte.getCapteur().getIdCapteur())
                    .nomCapteur(alerte.getCapteur().getNomCapteur())
                    .typeCapteur(alerte.getCapteur().getTypeCapteur());
        }

        // Statistiques
        builder.nombreNotifications(alerte.getNotifications() != null ?
                alerte.getNotifications().size() : 0);

        return builder.build();
    }

}
