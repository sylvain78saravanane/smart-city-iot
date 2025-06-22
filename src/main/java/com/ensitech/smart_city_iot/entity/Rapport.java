package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rapport")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rapport")
    private Long idRapport;

    @Column(name = "nom_rapport", nullable = false, length = 100)
    private String nomRapport;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "contenu", columnDefinition = "LONGTEXT")
    private String contenu;

    @Column(name = "periode_debut")
    private LocalDateTime periodeDebut;

    @Column(name = "periode_fin")
    private LocalDateTime periodeFin;

    @Column(name = "type_rapport", length = 50)
    private String typeRapport; // TEMPERATURE, POLLUTION, TRAFIC, GLOBAL, etc.

    @Column(name = "format_fichier", length = 20)
    private String formatFichier = "PDF"; // PDF, CSV, JSON

    @Column(name = "taille_fichier")
    private Long tailleFichier; // en bytes

    @Column(name = "statut", length = 20)
    private String statut = "EN_COURS"; // EN_COURS, TERMINE, ERREUR

    @Column(name = "nombre_donnees")
    private Long nombreDonnees;

    @Column(name = "chemin_fichier", length = 500)
    private String cheminFichier;

    // Relation avec Chercheur (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chercheur", nullable = false)
    private Chercheur chercheur;

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.statut == null) {
            this.statut = "EN_COURS";
        }
        if (this.formatFichier == null) {
            this.formatFichier = "PDF";
        }
    }

    // Méthodes utilitaires
    public boolean isTermine() {
        return "TERMINE".equals(this.statut);
    }

    public boolean isEnCours() {
        return "EN_COURS".equals(this.statut);
    }

    public boolean hasError() {
        return "ERREUR".equals(this.statut);
    }

    public String getTailleFichierFormatee() {
        if (tailleFichier == null) return "0 KB";

        if (tailleFichier < 1024) {
            return tailleFichier + " B";
        } else if (tailleFichier < 1024 * 1024) {
            return String.format("%.1f KB", tailleFichier / 1024.0);
        } else {
            return String.format("%.1f MB", tailleFichier / (1024.0 * 1024.0));
        }
    }

    public String getDureeGeneration() {
        if (dateCreation == null) return "N/A";
        // Calcul simple basé sur la date de création
        // Dans un vrai contexte, vous pourriez avoir une date de fin de génération
        return "Généré le " + dateCreation.toLocalDate();
    }


}
