package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long idUtilisateur;

    private String Nom;

    @Column(name = "prénom")
    private String Prenom;

    @Column(nullable = false)
    private String email;

    private String motDePasse;

    @Column(name="date_naissance")
    private LocalDate dateNaissance;

    @Column(length = 14)
    private String telephone;

    private String numeroRue;

    private String adresse;

    @Column(name = "code_postal", length = 7)
    private String codePostal;

    private Boolean actif = true;

    @Column(name = "notification_active")
    private Boolean notificationActive = true;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "utilisateur_permission",
            joinColumns = @JoinColumn(name = "id_utilsateur"),
            inverseJoinColumns = @JoinColumn(name = "id_permission")
    )
    private List<Permission> permissions = new ArrayList<>();

    // Méthodes communes pour tout utilisateur
    public boolean isEmailValide() {
        return email != null && email.contains("@");
    }

    // TODO : Méthodes abstraites à implémenter dans les classes filles
    public abstract String getRole();
    public abstract boolean peutCreerCapteur();
    public abstract boolean peutVoirDonneesBrutes();
    public abstract boolean peutGenererRapport();
    public abstract boolean peutGererUtilisateurs();


}
