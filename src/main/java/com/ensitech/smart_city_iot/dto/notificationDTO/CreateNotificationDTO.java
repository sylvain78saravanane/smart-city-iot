package com.ensitech.smart_city_iot.dto.notificationDTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationDTO {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 100, message = "Le titre ne peut pas dépasser 100 caractères")
    private String titre;

    @NotBlank(message = "Le message est obligatoire")
    @Size(max = 1000, message = "Le message ne peut pas dépasser 1000 caractères")
    private String message;

    @Pattern(regexp = "EMAIL|SMS|SYSTEME", message = "Type de notification invalide")
    private String typeNotification = "SYSTEME";

    @NotNull(message = "L'ID de l'alerte est obligatoire")
    private Long idAlerte;

    @NotEmpty(message = "Au moins un utilisateur doit être spécifié")
    private List<Long> idUtilisateurs;

}
