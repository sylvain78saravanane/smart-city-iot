package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "gestionnaire_de_ville")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestionnaireDeVille extends Utilisateur{

    @Column(name = "code_gv")
    private Integer codeGV;

    @Column(name = "nom_departement", length = 50)
    private String nomDepartement;

    @Column(precision = 15, scale = 2)
    private BigDecimal salaire;

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
}
