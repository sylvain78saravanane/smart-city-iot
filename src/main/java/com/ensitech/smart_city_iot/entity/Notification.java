package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Long idNotification;
    @Column(name = "titre", nullable = false, length = 100)
    private String titre;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "type_notification", length = 20)
    private String typeNotification; // EMAIL, SMS, PUSH, SYSTEME

    @Column(name = "statut", length = 20)
    private String statut = "EN_ATTENTE"; // EN_ATTENTE, ENVOYE, ECHEC

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    @Column(name = "lu", nullable = false)
    private Boolean lu = false;

    // Relation avec Alerte (Une notification est associée à une alerte)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alerte", nullable = false)
    private Alerte alerte;

    // Relation Many-to-Many avec Utilisateur (Une notification peut être envoyée à plusieurs utilisateurs)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "notification_utilisateur",
            joinColumns = @JoinColumn(name = "id_notification"),
            inverseJoinColumns = @JoinColumn(name = "id_utilisateur")
    )
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.lu == null) {
            this.lu = false;
        }
        if (this.statut == null) {
            this.statut = "EN_ATTENTE";
        }
    }

    // Méthodes utilitaires
    public boolean isLu() {
        return lu != null && lu;
    }

    public boolean isEnvoye() {
        return "ENVOYE".equals(statut);
    }

    public void marquerCommeLu() {
        this.lu = true;
    }

    public void marquerCommeEnvoye() {
        this.statut = "ENVOYE";
        this.dateEnvoi = LocalDateTime.now();
    }

    public void ajouterUtilisateur(Utilisateur utilisateur) {
        utilisateurs.add(utilisateur);
    }

    public void retirerUtilisateur(Utilisateur utilisateur) {
        utilisateurs.remove(utilisateur);
    }
}
