package com.ensitech.smart_city_iot.dto.alerteDTO;

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
public class UpdateAlerteDTO {

    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    private String titre;

    @Size(max = 255, message = "La description ne peut pas dépasser 255 caractères")
    private String description;

    private Double seuilValeur;

    @Pattern(regexp = "SUPERIEUR|INFERIEUR|EGAL", message = "Type de condition invalide")
    private String typeCondition;

    @Pattern(regexp = "FAIBLE|MOYENNE|HAUTE|CRITIQUE", message = "Priorité invalide")
    private String priorite;

    private Boolean active;
}
