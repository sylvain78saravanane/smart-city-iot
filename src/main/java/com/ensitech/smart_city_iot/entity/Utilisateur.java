package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "mot_de_passe", length = 255, nullable = false)
    private String motDePasse;

    @Column(name="date_naissance")
    private LocalDate dateNaissance;

    @Column(length = 14)
    private String telephone;

    @Column(length = 5)
    private String numeroRue;

    @Column(length = 100)
    private String adresse;

    @Column(name = "code_postal", length = 7)
    private String codePostal;

    @Column(nullable = false)
    private Boolean actif = true;

    @Column(name = "notification_active")
    private Boolean notificationActive = true;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "utilisateur_permission",
            joinColumns = @JoinColumn(name = "id_utilisateur"),
            inverseJoinColumns = @JoinColumn(name = "id_permission")
    )
    private List<Permission> permissions = new ArrayList<>();

    // Méthodes communes pour tout utilisateur
    public boolean isEmailValide() {
        return email != null && email.contains("@");
    }

    public boolean isActif() {
        return actif != null && actif;
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean hasPermission(String module, String action){
        return permissions.stream()
                .filter(p -> p.getActive() && !p.isExpired())
                .filter(p -> p.getModuleConcerne().equalsIgnoreCase(module))
                .anyMatch(p -> p.hasPermissionFor(action));
    }

    public List<Permission> getActivePermissions() {
        return permissions.stream()
                .filter(p -> p.getActive() && !p.isExpired())
                .collect(Collectors.toList());
    }

    public Permission getPermissionForModule(String module) {
        return permissions.stream()
                .filter(p -> p.getActive() && !p.isExpired())
                .filter(p -> p.getModuleConcerne().equalsIgnoreCase(module))
                .findFirst()
                .orElse(null);
    }

    public boolean canAccessModule(String module) {
        return hasPermission(module, "READ");
    }

    public boolean canModifyModule(String module) {
        return hasPermission(module, "MODIFIER");
    }



    // TODO : Méthodes abstraites à implémenter dans les classes filles
    public abstract String getRole();
    public abstract boolean peutCreerCapteur();
    public abstract boolean peutVoirDonneesBrutes();
    public abstract boolean peutGenererRapport();
    public abstract boolean peutGererUtilisateurs();

}
