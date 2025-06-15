package com.ensitech.smart_city_iot.dto.utilisateurDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUtilisateurDTO {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @Pattern(regexp = "^[0-9+\\-\\s]{10,14}$", message = "Format de téléphone invalide")
    private String telephone;

    @Size(max = 100, message = "L'adresse ne peut pas dépasser 100 caractères")
    private String adresse;

    @Size(max = 7, message = "Le code postal ne peut pas dépasser 7 caractères")
    private String codePostal;

    @NotBlank(message = "Le type d'utilisateur est obligatoire")
    @Pattern(regexp = "ADMINISTRATEUR|GESTIONNAIRE_VILLE|CHERCHEUR|CITOYEN",
            message = "Type d'utilisateur invalide")
    private String typeUtilisateur;

    // Champs spécifiques selon le type
    @Builder.Default
    private Map<String, Object> donneesSpecifiques = new HashMap<>();

    // Méthodes utilitaires pour récupérer les données spécifiques
    public Integer getCodeAdmin() {
        return (Integer) donneesSpecifiques.get("codeAdmin");
    }

    public Integer getCodeGV() {
        return (Integer) donneesSpecifiques.get("codeGV");
    }

    public BigDecimal getSalaire() {
        Object salaire = donneesSpecifiques.get("salaire");
        if (salaire instanceof Number) {
            return BigDecimal.valueOf(((Number) salaire).doubleValue());
        }
        return (BigDecimal) salaire;
    }

    public String getNomDepartement() {
        return (String) donneesSpecifiques.get("nomDepartement");
    }

    public String getInstitut() {
        return (String) donneesSpecifiques.get("institut");
    }

    public String getDomaineRecherche() {
        return (String) donneesSpecifiques.get("domaineRecherche");
    }

    public Double getLatitude() {
        Object lat = donneesSpecifiques.get("latitude");
        return lat instanceof Number ? ((Number) lat).doubleValue() : (Double) lat;
    }

    public Double getLongitude() {
        Object lon = donneesSpecifiques.get("longitude");
        return lon instanceof Number ? ((Number) lon).doubleValue() : (Double) lon;
    }
}
