package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "commentaire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commentaire")
    public Long idCommentaire;

    @Column(name = "titre", length = 100)
    private String titre;

    @Column(name = "contenu", columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @Column(name = "note_evaluation")
    private Integer noteEvaluation; // Note de 1 à 5 par exemple

    @Column(name = "nombre_likes")
    private Integer nombreLikes = 0;

    @Column(name = "nombre_dislikes")
    private Integer nombreDislikes = 0;

    @Column(name = "sujet", length = 50)
    private String sujet; // CAPTEUR, POLLUTION, TRAFIC, GENERAL, etc.

    @Column(name = "localisation", length = 200)
    private String localisation;

    // Relation avec Citoyen
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_citoyen", nullable = false)
    private Citoyen citoyen;

    // Méthodes utilitaires
    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.actif == null) {
            this.actif = true;
        }
        if (this.nombreLikes == null) {
            this.nombreLikes = 0;
        }
        if (this.nombreDislikes == null) {
            this.nombreDislikes = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // Méthodes métier
    public boolean isActif() {
        return actif != null && actif;
    }

    public int getTotalInteractions() {
        return (nombreLikes != null ? nombreLikes : 0) +
                (nombreDislikes != null ? nombreDislikes : 0);
    }



    public void ajouterLike() {
        this.nombreLikes = (this.nombreLikes != null ? this.nombreLikes : 0) + 1;
    }

    public void retirerLike() {
        this.nombreLikes = Math.max(0, (this.nombreLikes != null ? this.nombreLikes : 0) - 1);
    }

    public void ajouterDislike() {
        this.nombreDislikes = (this.nombreDislikes != null ? this.nombreDislikes : 0) + 1;
    }

    public void retirerDislike() {
        this.nombreDislikes = Math.max(0, (this.nombreDislikes != null ? this.nombreDislikes : 0) - 1);
    }

    public String getAuteurNom() {
        return citoyen != null ? citoyen.getNomComplet() : "Anonyme";
    }

    public String getResume() {
        if (contenu == null) return "";
        return contenu.length() > 100 ? contenu.substring(0, 100) + "..." : contenu;
    }
}
