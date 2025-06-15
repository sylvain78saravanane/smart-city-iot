package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "chercheur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chercheur extends Utilisateur{

    @Column(precision = 15, scale = 2)
    private BigDecimal salaire;

    @Column(length = 100)
    private String institut;

    @Column(name = "domaine_recherche", length = 100)
    private String domaineRecherche;

    //@OneToMany(mappedBy = "chercheur", cascade = CascadeType.ALL)
    //private List<CollaborationModele> collaborations = new ArrayList<>();

    @Override
    public String getRole() {
        return "CHERCHEUR";
    }

    @Override
    public boolean peutCreerCapteur() {
        return false;
    }

    @Override
    public boolean peutVoirDonneesBrutes() {
        return true; // Pour la recherche
    }

    @Override
    public boolean peutGenererRapport() {
        return true;
    }

    @Override
    public boolean peutGererUtilisateurs() {
        return false;
    }

    @Override
    public String getNomComplet() {
        return "Dr. " + super.getNomComplet();
    }

    public boolean canCollaborate() {
        return true;
    }
}
