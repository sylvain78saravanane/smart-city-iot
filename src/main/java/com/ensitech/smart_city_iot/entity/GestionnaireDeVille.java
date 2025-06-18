package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gestionnaire_de_ville")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestionnaireDeVille extends Utilisateur{

    @Column(name = "code_gv")
    private String codeGV;

    @Column(name = "nom_departement", length = 50)
    private String nomDepartement;

    @Column(precision = 15, scale = 2)
    private BigDecimal salaire;

    @OneToMany(mappedBy = "gestionnaireDeVille", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Capteur> capteursGeres = new ArrayList<>();

    @Override
    public String getRole() {
        return "GESTIONNAIRE_VILLE";
    }

    @Override
    public boolean peutCreerCapteur() {
        return true;
    }

    @Override
    public boolean peutVoirDonneesBrutes() {
        return true; // Pour sa zone de compétence
    }

    @Override
    public boolean peutGenererRapport() {
        return true;
    }

    @Override
    public boolean peutGererUtilisateurs() {
        return false; // Limité par rapport à l'admin
    }

    public String getNomComplet() {
        return "Gestionnaire " + super.getNomComplet();
    }

    public void ajouterCapteur(Capteur capteur) {
        capteursGeres.add(capteur);
        capteur.setGestionnaireDeVille(this);
    }

    public void retirerCapteur(Capteur capteur) {
        capteursGeres.remove(capteur);
        capteur.setGestionnaireDeVille(null);
    }
}
