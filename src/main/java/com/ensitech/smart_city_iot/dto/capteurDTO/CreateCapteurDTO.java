package com.ensitech.smart_city_iot.dto.capteurDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCapteurDTO {

    @NotBlank(message = "Le nom du capteur est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nomCapteur;

    @NotBlank(message = "Le type de capteur est obligatoire")
    @Pattern(regexp = "TEMPERATURE|HUMIDITE|POLLUTION|TRAFIC|BRUIT|LUMINOSITE|PRESSION|VENT|PLUIE",
            message = "Type de capteur invalide")
    private String typeCapteur;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    @DecimalMin(value = "-90.0", message = "La latitude doit être comprise entre -90 et 90")
    @DecimalMax(value = "90.0", message = "La latitude doit être comprise entre -90 et 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "La longitude doit être comprise entre -180 et 180")
    @DecimalMax(value = "180.0", message = "La longitude doit être comprise entre -180 et 180")
    private Double longitude;

    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String adresseInstallation;

    @Pattern(regexp = "ACTIF|INACTIF|MAINTENANCE|DEFAILLANT",
            message = "Statut invalide")
    private String statut = "ACTIF";

    private LocalDateTime dateInstallation;

    @Min(value = 1, message = "La fréquence de mesure doit être au moins de 1 minute")
    @Max(value = 1440, message = "La fréquence de mesure ne peut pas dépasser 1440 minutes (24h)")
    private Integer frequenceMesure;

    @Size(max = 20, message = "L'unité de mesure ne peut pas dépasser 20 caractères")
    private String uniteMesure;

    private Double valeurMin;

    private Double valeurMax;

    @Size(max = 50, message = "Le numéro de série ne peut pas dépasser 50 caractères")
    private String numeroSerie;

    @Size(max = 50, message = "Le modèle ne peut pas dépasser 50 caractères")
    private String modele;

    @Size(max = 50, message = "Le fabricant ne peut pas dépasser 50 caractères")
    private String fabricant;

    // ID du gestionnaire responsable (Administrateur OU GestionnaireDeVille)
    @NotNull(message = "L'ID du gestionnaire responsable est obligatoire")
    private Long idGestionnaireResponsable;

    @NotBlank(message = "Le type de gestionnaire est obligatoire")
    @Pattern(regexp = "ADMINISTRATEUR|GESTIONNAIRE_VILLE",
            message = "Type de gestionnaire invalide")
    private String typeGestionnaire;

    // Validation personnalisée
    public boolean isValeurMinMaxValid() {
        if (valeurMin != null && valeurMax != null) {
            return valeurMin <= valeurMax;
        }
        return true;
    }
}
