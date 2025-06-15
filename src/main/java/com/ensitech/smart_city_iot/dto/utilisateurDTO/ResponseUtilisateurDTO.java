package com.ensitech.smart_city_iot.dto.utilisateurDTO;

import com.ensitech.smart_city_iot.entity.*;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder
public class ResponseUtilisateurDTO {

    private Long idUtilisateur;
    private String nom;
    private String prenom;
    private String nomComplet;
    private String email;
    private LocalDate dateNaissance;
    private String telephone;
    private String adresse;
    private String codePostal;
    private Boolean actif;
    private Boolean notificationActive;
    private String role;
    private String typeUtilisateur;
    private Map<String, Object> donneesSpecifiques;
    private Integer nombrePermissions;

    public static ResponseUtilisateurDTO fromEntity(Utilisateur utilisateur) {
        return ResponseUtilisateurDTO.builder()
                .idUtilisateur(utilisateur.getIdUtilisateur())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .nomComplet(utilisateur.getNom() + " " + utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .dateNaissance(utilisateur.getDateNaissance())
                .telephone(utilisateur.getTelephone())
                .adresse(utilisateur.getAdresse())
                .codePostal(utilisateur.getCodePostal())
                .actif(utilisateur.getActif())
                .notificationActive(utilisateur.getNotificationActive())
                .role(utilisateur.getRole())
                .typeUtilisateur(utilisateur.getRole())
                .donneesSpecifiques(getSpecificData(utilisateur))
                .nombrePermissions(utilisateur.getPermissions() != null ?
                        utilisateur.getPermissions().size() : 0)
                .build();
    }

    private static Map<String, Object> getSpecificData(Utilisateur utilisateur) {
        Map<String, Object> data = new HashMap<>();

        if (utilisateur instanceof Administrateur) {
            Administrateur admin = (Administrateur) utilisateur;
            data.put("codeAdmin", admin.getCodeAdmin());
            data.put("salaire", admin.getSalaire());
            data.put("type", "ADMINISTRATEUR");

        } else if (utilisateur instanceof GestionnaireDeVille) {
            GestionnaireDeVille gestionnaire = (GestionnaireDeVille) utilisateur;
            data.put("codeGV", gestionnaire.getCodeGV());
            data.put("nomDepartement", gestionnaire.getNomDepartement());
            data.put("salaire", gestionnaire.getSalaire());
            data.put("type", "GESTIONNAIRE_VILLE");

        } else if (utilisateur instanceof Chercheur) {
            Chercheur chercheur = (Chercheur) utilisateur;
            data.put("institut", chercheur.getInstitut());
            data.put("domaineRecherche", chercheur.getDomaineRecherche());
            data.put("salaire", chercheur.getSalaire());
            data.put("type", "CHERCHEUR");

        } else if (utilisateur instanceof Citoyen) {
            Citoyen citoyen = (Citoyen) utilisateur;
            data.put("latitude", citoyen.getLatitude());
            data.put("longitude", citoyen.getLongitude());
            data.put("type", "CITOYEN");
        }

        return data;
    }
}
