package com.ensitech.smart_city_iot.dto.utilisateurDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String type = "Bearer";
    private ResponseUtilisateurDTO utilisateur;
    private long expiresIn;
}
