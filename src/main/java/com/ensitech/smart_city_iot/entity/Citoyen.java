package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    public boolean peutCommenter() {
        return true;
    }

    public boolean peutCreerAlertePersonnalisee() {
        return true;
    }
}
