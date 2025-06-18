package com.ensitech.smart_city_iot.dto.alerteDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateAlerteDTO {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    private String titre;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    @NotNull(message = "Le seuil de valeur est obligatoire")
    private Double seuilValeur;

    @NotBlank(message = "Le type de condition est obligatoire")
    @Pattern(regexp = "SUPERIEUR|INFERIEUR|EGAL", message = "Type de condition invalide")
    private String typeCondition;

    @Pattern(regexp = "FAIBLE|MOYENNE|HAUTE|CRITIQUE", message = "Priorité invalide")
    private String priorite = "MOYENNE";

    private Boolean active = true;

    @NotNull(message = "L'ID du capteur est obligatoire")
    private Long idCapteur;
}
