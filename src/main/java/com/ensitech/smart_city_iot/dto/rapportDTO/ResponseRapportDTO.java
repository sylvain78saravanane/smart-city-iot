package com.ensitech.smart_city_iot.dto.rapportDTO;

import com.ensitech.smart_city_iot.entity.Rapport;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;

@Builder
@Data
public class ResponseRapportDTO {
    private Long idRapport;
    private String nomRapport;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDateTime periodeDebut;
    private LocalDateTime periodeFin;
    private String typeRapport;
    private String formatFichier;
    private Long tailleFichier;
    private String tailleFichierFormatee;
    private String statut;
    private Long nombreDonnees;
    private String cheminFichier;

    // Informations du chercheur
    private Long idChercheur;
    private String nomCompletChercheur;
    private String emailChercheur;
    private String institutChercheur;
    private String domaineRecherche;

    // Informations calculées
    private String dureeGeneration;
    private boolean peutTelecharger;
    private String resumeContenu;

    public static ResponseRapportDTO fromEntity(Rapport rapport) {
        ResponseRapportDTOBuilder builder = ResponseRapportDTO.builder()
                .idRapport(rapport.getIdRapport())
                .nomRapport(rapport.getNomRapport())
                .description(rapport.getDescription())
                .dateCreation(rapport.getDateCreation())
                .periodeDebut(rapport.getPeriodeDebut())
                .periodeFin(rapport.getPeriodeFin())
                .typeRapport(rapport.getTypeRapport())
                .formatFichier(rapport.getFormatFichier())
                .tailleFichier(rapport.getTailleFichier())
                .tailleFichierFormatee(rapport.getTailleFichierFormatee())
                .statut(rapport.getStatut())
                .nombreDonnees(rapport.getNombreDonnees())
                .cheminFichier(rapport.getCheminFichier())
                .dureeGeneration(rapport.getDureeGeneration())
                .peutTelecharger(rapport.isTermine());

        // Informations du chercheur
        if (rapport.getChercheur() != null) {
            builder.idChercheur(rapport.getChercheur().getIdUtilisateur())
                    .nomCompletChercheur(rapport.getChercheur().getNomComplet())
                    .emailChercheur(rapport.getChercheur().getEmail())
                    .institutChercheur(rapport.getChercheur().getInstitut())
                    .domaineRecherche(rapport.getChercheur().getDomaineRecherche());
        }

        // Résumé du contenu
        if (rapport.getContenu() != null) {
            builder.resumeContenu(rapport.getContenu().length() > 200 ?
                    rapport.getContenu().substring(0, 200) + "..." :
                    rapport.getContenu());
        }

        return builder.build();
    }
}
