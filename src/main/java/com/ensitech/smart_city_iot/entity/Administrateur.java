package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "administrateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur extends Utilisateur {

    @Column(name = "code_admin")
    private String codeAdmin;

    @Column(precision = 15, scale = 2)
    private BigDecimal salaire;


    // @OneToMany(mappedBy = "administrateur", fetch = FetchType.LAZY)
    // private List<Capteur> capteursGeres = new ArrayList<>();

    // Implémentation des méthodes abstraites
    @Override
    public String getRole() {
        return "ADMINISTRATEUR";
    }

    @Override
    public boolean peutCreerCapteur() {
        return true;
    }

    @Override
    public boolean peutVoirDonneesBrutes() {
        return true;
    }

    @Override
    public boolean peutGenererRapport() {
        return true;
    }

    @Override
    public boolean peutGererUtilisateurs() {
        return true;
    }

    // Méthodes spécifiques à l'administrateur
    public String getNomComplet() {
        return "Admin " + super.getNomComplet();
    }

    public boolean hasFullAccess() {
        return true;
    }
}
