package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "capteur")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Capteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_capteur")
    private Long idCapteur;

    @Column(name = "nom_capteur", nullable = false, length = 100)
    private String nomCapteur;

    @Column(name = "type_capteur", nullable = false, length = 50)
    private String typeCapteur; // TEMPERATURE, HUMIDITE, POLLUTION, TRAFIC, etc.

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "latitude", precision = 10, scale = 7)
    private Double latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private Double longitude;

    @Column(name = "adresse_installation", length = 200)
    private String adresseInstallation;

    @Column(name = "statut", nullable = false, length = 20)
    private String statut = "ACTIF"; // ACTIF, INACTIF, MAINTENANCE, DEFAILLANT

    @Column(name = "date_installation", nullable = false)
    private LocalDateTime dateInstallation;

    @Column(name = "date_derniere_maintenance")
    private LocalDateTime dateDerniereMaintenance;

    @Column(name = "frequence_mesure")
    private Integer frequenceMesure; // en minutes

    @Column(name = "unite_mesure", length = 20)
    private String uniteMesure; // °C, %, ppm, etc.

    @Column(name = "valeur_min")
    private Double valeurMin;

    @Column(name = "valeur_max")
    private Double valeurMax;

    @Column(name = "numero_serie", unique = true, length = 50)
    private String numeroSerie;

    @Column(name = "modele", length = 50)
    private String modele;

    @Column(name = "fabricant", length = 50)
    private String fabricant;

    // Relations avec les utilisateurs (Administrateur OU GestionnaireDeVille)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_administrateur")
    private Administrateur administrateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gestionnaire")
    private GestionnaireDeVille gestionnaireDeVille;

    // Métadonnées
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "cree_par", length = 100)
    private String creePar;

    @Column(name = "modifie_par", length = 100)
    private String modifiePar;

    // Méthodes utilitaires
    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.dateInstallation == null) {
            this.dateInstallation = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // Méthodes métier
    public boolean isActif() {
        return "ACTIF".equals(this.statut);
    }

    public boolean isEnMaintenance() {
        return "MAINTENANCE".equals(this.statut);
    }

    public String getCreePar() {
        if (administrateur != null) {
            return "Admin: " + administrateur.getNomComplet();
        } else if (gestionnaireDeVille != null) {
            return "Gestionnaire: " + gestionnaireDeVille.getNomComplet();
        }
        return creePar;
    }

    public String getGestionnaireResponsable() {
        if (administrateur != null) {
            return administrateur.getNomComplet() + " (Administrateur)";
        } else if (gestionnaireDeVille != null) {
            return gestionnaireDeVille.getNomComplet() + " (Gestionnaire de Ville)";
        }
        return "Non assigné";
    }

    public boolean appartientA(Utilisateur utilisateur) {
        if (utilisateur instanceof Administrateur) {
            return administrateur != null && administrateur.getIdUtilisateur().equals(utilisateur.getIdUtilisateur());
        } else if (utilisateur instanceof GestionnaireDeVille) {
            return gestionnaireDeVille != null && gestionnaireDeVille.getIdUtilisateur().equals(utilisateur.getIdUtilisateur());
        }
        return false;
    }

}
