package com.ensitech.smart_city_iot.dto.utilisateurDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Builder
@Data @AllArgsConstructor @NoArgsConstructor
public class UpdateUtilisateurDTO {
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String nom;

    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String prenom;

    @Email(message = "Format d'email invalide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @Pattern(regexp = "^[0-9+\\-\\s]{10,14}$", message = "Format de téléphone invalide")
    private String telephone;

    @Size(max = 100, message = "L'adresse ne peut pas dépasser 100 caractères")
    private String adresse;

    @Size(max = 7, message = "Le code postal ne peut pas dépasser 7 caractères")
    private String codePostal;

    private Boolean actif;

    private Boolean notificationActive;

    @Builder.Default
    private Map<String, Object> donneesSpecifiques = new HashMap<>();
}
