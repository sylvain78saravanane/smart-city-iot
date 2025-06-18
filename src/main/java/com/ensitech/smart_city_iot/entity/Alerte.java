package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alerte")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerte")
    private Long idAlerte;

    @Column(name = "titre", nullable = false, length = 100)
    private String titre;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "seuil_valeur")
    private Double seuilValeur;

    @Column(name = "type_condition", length = 20)
    private String typeCondition; // SUPERIEUR, INFERIEUR, EGAL

    @Column(name = "priorite", length = 20)
    private String priorite; // FAIBLE, MOYENNE, HAUTE, CRITIQUE

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // Relation avec Capteur (Une alarme est associée à un seul capteur)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_capteur", nullable = false)
    private Capteur capteur;

    // Relation avec Notification (Une alerte peut déclencher plusieurs notifications)
    @OneToMany(mappedBy = "alerte", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
        if (this.active == null) {
            this.active = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // Méthodes utilitaires
    public boolean isActive() {
        return active != null && active;
    }

    public void ajouterNotification(Notification notification) {
        notifications.add(notification);
        notification.setAlerte(this);
    }

    public void retirerNotification(Notification notification) {
        notifications.remove(notification);
        notification.setAlerte(null);

    }

}
