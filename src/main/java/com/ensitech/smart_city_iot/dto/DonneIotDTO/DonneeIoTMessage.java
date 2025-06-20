package com.ensitech.smart_city_iot.dto.DonneIotDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonneeIoTMessage {

    private Long idCapteur;
    private String villeNom;
    private String region;
    private String pays;
    private Double latitude;
    private Double longitude;
    private LocalDateTime heureLocale;
    private Double temperatureCelsius;
    private Double temperatureFahrenheit;
    private Double vitesseVentKph;
    private Double precipitationMm;
    private Double humidite;
    private Double nuageux;
    private Double indiceUv;
    private Double co;
    private Double no2;
    private Double o3;
    private Double so2;
    private Double pm10;
    private LocalDateTime timestampCollecte;
    private String sourceApi;
}
