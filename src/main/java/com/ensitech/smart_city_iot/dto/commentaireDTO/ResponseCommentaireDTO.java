package com.ensitech.smart_city_iot.dto.commentaireDTO;

import com.ensitech.smart_city_iot.entity.Commentaire;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Builder
@Data
public class ResponseCommentaireDTO {

    private Long idCommentaire;
    private String titre;
    private String contenu;
    private String resume;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private Boolean actif;
    private Integer noteEvaluation;
    private Integer nombreLikes;
    private Integer nombreDislikes;
    private Integer totalInteractions;
    private Double ratioPositif;
    private String sujet;
    private String localisation;

    // Informations du citoyen
    private Long idCitoyen;
    private String nomCompletCitoyen;
    private String emailCitoyen;

    // Méta-données
    private boolean peutModifier;
    private boolean peutSupprimer;
    private int joursDepuisCreation;

    public static ResponseCommentaireDTO fromEntity(Commentaire commentaire) {
        ResponseCommentaireDTOBuilder builder = ResponseCommentaireDTO.builder()
                .idCommentaire(commentaire.getIdCommentaire())
                .titre(commentaire.getTitre())
                .contenu(commentaire.getContenu())
                .resume(commentaire.getResume())
                .dateCreation(commentaire.getDateCreation())
                .dateModification(commentaire.getDateModification())
                .actif(commentaire.getActif())
                .noteEvaluation(commentaire.getNoteEvaluation())
                .nombreLikes(commentaire.getNombreLikes())
                .nombreDislikes(commentaire.getNombreDislikes())
                .totalInteractions(commentaire.getTotalInteractions())
                .sujet(commentaire.getSujet())
                .localisation(commentaire.getLocalisation());

        // Informations du citoyen
        if (commentaire.getCitoyen() != null) {
            builder.idCitoyen(commentaire.getCitoyen().getIdUtilisateur())
                    .nomCompletCitoyen(commentaire.getCitoyen().getNomComplet())
                    .emailCitoyen(commentaire.getCitoyen().getEmail());
        }

        // Calculs
        if (commentaire.getDateCreation() != null) {
            builder.joursDepuisCreation(
                    (int) java.time.Duration.between(commentaire.getDateCreation(), LocalDateTime.now()).toDays()
            );
        }

        // Permissions (à adapter selon vos règles métier)
        builder.peutModifier(commentaire.isActif())
                .peutSupprimer(commentaire.isActif());

        return builder.build();
    }
}
