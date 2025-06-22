package com.ensitech.smart_city_iot.dto.rapportDTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRapportDTO {

    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nomRapport;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @Pattern(regexp = "EN_COURS|TERMINE|ERREUR", message = "Statut invalide")
    private String statut;

    private String contenu;

    private Long tailleFichier;

    private String cheminFichier;

    private Long nombreDonnees;
}
