package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    // Relation One-to-Many avec Rapport
    @OneToMany(mappedBy = "chercheur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rapport> rapports = new ArrayList<>();

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

    // Méthodes utilitaires pour la gestion des rapports
    public void ajouterRapport(Rapport rapport) {
        rapports.add(rapport);
        rapport.setChercheur(this);
    }

    public void retirerRapport(Rapport rapport) {
        rapports.remove(rapport);
        rapport.setChercheur(null);
    }

    public int getNombreRapports() {
        return rapports != null ? rapports.size() : 0;
    }

    public int getNombreRapportsTermines() {
        return rapports != null ?
                (int) rapports.stream().filter(Rapport::isTermine).count() : 0;
    }

    public List<Rapport> getRapportsRecents(int limite) {
        return rapports != null ?
                rapports.stream()
                        .sorted((r1, r2) -> r2.getDateCreation().compareTo(r1.getDateCreation()))
                        .limit(limite)
                        .toList() : new ArrayList<>();
    }
}
