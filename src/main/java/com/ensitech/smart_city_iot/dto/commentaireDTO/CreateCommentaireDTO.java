package com.ensitech.smart_city_iot.dto.commentaireDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentaireDTO {
    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    private String titre;

    @NotBlank(message = "Le contenu est obligatoire")
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

    @NotNull(message = "L'ID du citoyen est obligatoire")
    private Long idCitoyen;
}
