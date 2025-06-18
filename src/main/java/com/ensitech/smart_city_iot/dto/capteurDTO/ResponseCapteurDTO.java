package com.ensitech.smart_city_iot.dto.capteurDTO;

import com.ensitech.smart_city_iot.entity.Capteur;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ResponseCapteurDTO {

    private Long idCapteur;
    private String nomCapteur;
    private String typeCapteur;
    private String description;
    private Double latitude;
    private Double longitude;
    private String adresseInstallation;
    private String statut;
    private LocalDateTime dateInstallation;
    private LocalDateTime dateDerniereMaintenance;
    private Integer frequenceMesure;
    private String uniteMesure;
    private Double valeurMin;
    private Double valeurMax;
    private String numeroSerie;
    private String modele;
    private String fabricant;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;

    // Informations sur le gestionnaire responsable
    private Long idGestionnaireResponsable;
    private String nomGestionnaireResponsable;
    private String typeGestionnaire;
    private String emailGestionnaire;

    // Informations calculées
    private boolean actif;
    private int joursDepuisInstallation;
    private String coordonneesGPS;

    public static ResponseCapteurDTO fromEntity(Capteur capteur) {
        ResponseCapteurDTOBuilder builder = ResponseCapteurDTO.builder()
                .idCapteur(capteur.getIdCapteur())
                .nomCapteur(capteur.getNomCapteur())
                .typeCapteur(capteur.getTypeCapteur())
                .description(capteur.getDescription())
                .latitude(capteur.getLatitude())
                .longitude(capteur.getLongitude())
                .adresseInstallation(capteur.getAdresseInstallation())
                .statut(capteur.getStatut())
                .dateInstallation(capteur.getDateInstallation())
                .dateDerniereMaintenance(capteur.getDateDerniereMaintenance())
                .frequenceMesure(capteur.getFrequenceMesure())
                .uniteMesure(capteur.getUniteMesure())
                .valeurMin(capteur.getValeurMin())
                .valeurMax(capteur.getValeurMax())
                .numeroSerie(capteur.getNumeroSerie())
                .modele(capteur.getModele())
                .fabricant(capteur.getFabricant())
                .dateCreation(capteur.getDateCreation())
                .dateModification(capteur.getDateModification())
                .actif(capteur.isActif());

        // Informations sur le gestionnaire responsable
        if (capteur.getAdministrateur() != null) {
            builder.idGestionnaireResponsable(capteur.getAdministrateur().getIdUtilisateur())
                    .nomGestionnaireResponsable(capteur.getAdministrateur().getNomComplet())
                    .typeGestionnaire("ADMINISTRATEUR")
                    .emailGestionnaire(capteur.getAdministrateur().getEmail());
        } else if (capteur.getGestionnaireDeVille() != null) {
            builder.idGestionnaireResponsable(capteur.getGestionnaireDeVille().getIdUtilisateur())
                    .nomGestionnaireResponsable(capteur.getGestionnaireDeVille().getNomComplet())
                    .typeGestionnaire("GESTIONNAIRE_VILLE")
                    .emailGestionnaire(capteur.getGestionnaireDeVille().getEmail());
        }

        // Calculs
        if (capteur.getDateInstallation() != null) {
            builder.joursDepuisInstallation(
                    (int) java.time.Duration.between(capteur.getDateInstallation(), LocalDateTime.now()).toDays()
            );
        }

        if (capteur.getLatitude() != null && capteur.getLongitude() != null) {
            builder.coordonneesGPS(capteur.getLatitude() + ", " + capteur.getLongitude());
        }

        return builder.build();
    }
}
