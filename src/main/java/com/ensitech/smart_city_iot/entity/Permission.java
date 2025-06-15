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
@Table(name = "permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permission")
    private Long idPermission;

    @Column(name = "module_concerne", length = 50, nullable = false)
    private String moduleConcerne;

    @Column(nullable = false)
    private Boolean lire = false;

    @Column(nullable = false)
    private Boolean modifier = false;

    @Column(nullable = false)
    private Boolean supprimer = false;

    @Column(nullable = false)
    private Boolean creer = false;

    @Column(name = "accordee_par", length = 100)
    private String accordeePar;

    @Column(name = "date_attribution", nullable = false)
    private LocalDateTime dateAttribution;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @Column(length = 100)
    private String description;

    @Column(name = "niveau_acces", length = 20)
    private String niveauAcces = "STANDARD";

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    // Méthodes utilitaires
    public boolean hasReadAccess() {
        return active && lire;
    }

    public boolean hasWriteAccess() {
        return active && (creer || modifier);
    }

    public boolean hasDeleteAccess() {
        return active && supprimer;
    }

    public boolean hasFullAccess() {
        return active && lire && modifier && supprimer && creer;
    }

    public boolean isExpired() {
        return dateExpiration != null && LocalDateTime.now().isAfter(dateExpiration);
    }

    public String getPermissionSummary() {
        StringBuilder summary = new StringBuilder();
        if (lire) summary.append("R");
        if (creer) summary.append("C");
        if (modifier) summary.append("U");
        if (supprimer) summary.append("D");
        return summary.toString();
    }

    public boolean hasPermissionFor(String action) {
        if (!active || isExpired()) return false;

        switch (action.toUpperCase()) {
            case "READ":
            case "LIRE":
                return lire;
            case "CREATE":
            case "CREER":
                return creer;
            case "UPDATE":
            case "MODIFIER":
                return modifier;
            case "DELETE":
            case "SUPPRIMER":
                return supprimer;
            default:
                return false;
        }
    }

}
