package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "citoyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Citoyen extends Utilisateur{

    @Column(precision = 10)
    private Double latitude;

    @Column(precision = 11)
    private Double longitude;

   // @OneToMany(mappedBy = "citoyen", cascade = CascadeType.ALL)
   // private List<Commentaire> commentaires = new ArrayList<>();

    //v@OneToMany(mappedBy = "citoyen", cascade = CascadeType.ALL)
    //vprivate List<AlertePersonnalisee> alertesPersonnalisees = new ArrayList<>();

    @Override
    public String getRole() {
        return "CITOYEN";
    }

    @Override
    public boolean peutCreerCapteur() {
        return false;
    }

    @Override
    public boolean peutVoirDonneesBrutes() {
        return false;
    }

    @Override
    public boolean peutGenererRapport() {
        return true;
    }

    @Override
    public boolean peutGererUtilisateurs() {
        return false;
    }

    @OneToMany(mappedBy = "citoyen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Commentaire> commentaires = new ArrayList<>();

    // Méthodes utilitaires pour la gestion des commentaires
    public void ajouterCommentaire(Commentaire commentaire) {
        commentaires.add(commentaire);
        commentaire.setCitoyen(this);
    }

    public void retirerCommentaire(Commentaire commentaire) {
        commentaires.remove(commentaire);
        commentaire.setCitoyen(null);
    }

    public int getNombreCommentaires() {
        return commentaires != null ? commentaires.size() : 0;
    }

    public int getNombreCommentairesActifs() {
        return commentaires != null ?
                (int) commentaires.stream().filter(Commentaire::isActif).count() : 0;
    }


}
