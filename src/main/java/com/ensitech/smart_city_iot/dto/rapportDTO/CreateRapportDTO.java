package com.ensitech.smart_city_iot.dto.rapportDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRapportDTO {
    @NotBlank(message = "Le nom du rapport est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nomRapport;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    @NotNull(message = "La période de début est obligatoire")
    private LocalDateTime periodeDebut;

    @NotNull(message = "La période de fin est obligatoire")
    private LocalDateTime periodeFin;

    @Pattern(regexp = "TEMPERATURE|POLLUTION|TRAFIC|BRUIT|LUMINOSITE|GLOBAL",
            message = "Type de rapport invalide")
    private String typeRapport = "GLOBAL";

    @Pattern(regexp = "PDF|CSV|JSON",
            message = "Format de fichier invalide")
    private String formatFichier = "PDF";

    @NotNull(message = "L'ID du chercheur est obligatoire")
    private Long idChercheur;

    // Validation personnalisée
    public boolean isPeriodeValide() {
        if (periodeDebut != null && periodeFin != null) {
            return periodeDebut.isBefore(periodeFin);
        }
        return true;
    }
}
