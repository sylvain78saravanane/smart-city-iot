package com.ensitech.smart_city_iot.dto.commentaireDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class UpdateCommentaireDTO {

    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    private String titre;

    @Size(max = 2000, message = "Le contenu ne peut pas dépasser 2000 caractères")
    private String contenu;

    @Min(value = 1, message = "La note doit être comprise entre 1 et 5")
    @Max(value = 5, message = "La note doit être comprise entre 1 et 5")
    private Integer noteEvaluation;

    @Pattern(regexp = "GENERAL|POLLUTION|TRAFIC|BRUIT|LUMINOSITE|INFRASTRUCTURE|SECURITE|ENVIRONNEMENT|SUGGESTIONS",
            message = "Sujet invalide")
    private String sujet;

    @Size(max = 200, message = "La localisation ne peut pas dépasser 200 caractères")
    private String localisation;

    private Boolean actif;
}
