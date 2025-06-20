package com.ensitech.smart_city_iot.dto.DonneIotDTO;

import com.ensitech.smart_city_iot.entity.DonneeIoT;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResponseDonneeIoTDTO {

    private Long idDonneeIoT;

    // Informations du capteur
    private Long idCapteur;
    private String nomCapteur;
    private String typeCapteur;

    // Données de localisation
    private String villeNom;
    private String region;
    private String pays;
    private Double latitude;
    private Double longitude;
    private String localisation;
    private String coordonnees;
    private LocalDateTime heureLocale;

    // Données météorologiques
    private Double temperatureCelsius;
    private Double temperatureFahrenheit;
    private Double vitesseVentKph;
    private Double precipitationMm;
    private Double humidite;
    private Double nuageux;
    private Double indiceUv;

    // Données qualité de l'air
    private Double co;
    private Double no2;
    private Double o3;
    private Double so2;
    private Double pm10;

    // Métadonnées
    private LocalDateTime timestampCollecte;
    private String statutDonnee;
    private String sourceApi;

    // Informations calculées
    private boolean donneeValide;
    private String qualiteAirResume;
    private String conditionsMeteo;

    public static ResponseDonneeIoTDTO fromEntity(DonneeIoT donnee) {
        ResponseDonneeIoTDTOBuilder builder = ResponseDonneeIoTDTO.builder()
                .idDonneeIoT(donnee.getIdDonneeIoT())
                .villeNom(donnee.getVilleNom())
                .region(donnee.getRegion())
                .pays(donnee.getPays())
                .latitude(donnee.getLatitude())
                .longitude(donnee.getLongitude())
                .localisation(donnee.getLocalisation())
                .coordonnees(donnee.getCoordonnees())
                .heureLocale(donnee.getHeureLocale())
                .temperatureCelsius(donnee.getTemperatureCelsius())
                .temperatureFahrenheit(donnee.getTemperatureFahrenheit())
                .vitesseVentKph(donnee.getVitesseVentKph())
                .precipitationMm(donnee.getPrecipitationMm())
                .humidite(donnee.getHumidite())
                .nuageux(donnee.getNuageux())
                .indiceUv(donnee.getIndiceUv())
                .co(donnee.getCo())
                .no2(donnee.getNo2())
                .o3(donnee.getO3())
                .so2(donnee.getSo2())
                .pm10(donnee.getPm10())
                .timestampCollecte(donnee.getTimestampCollecte())
                .statutDonnee(donnee.getStatutDonnee())
                .sourceApi(donnee.getSourceApi())
                .donneeValide(donnee.isDonneeValide());

        // Informations du capteur
        if (donnee.getCapteur() != null) {
            builder.idCapteur(donnee.getCapteur().getIdCapteur())
                    .nomCapteur(donnee.getCapteur().getNomCapteur())
                    .typeCapteur(donnee.getCapteur().getTypeCapteur());
        }

        // Calculs et résumés
        builder.qualiteAirResume(calculerQualiteAirResume(donnee))
                .conditionsMeteo(calculerConditionsMeteo(donnee));

        return builder.build();
    }

    private static String calculerQualiteAirResume(DonneeIoT donnee) {
        if (donnee.getPm10() == null) return "Non disponible";

        // Classification basée sur PM10 (seuils européens)
        double pm10 = donnee.getPm10();
        if (pm10 <= 20) return "Bonne";
        else if (pm10 <= 40) return "Moyenne";
        else if (pm10 <= 50) return "Dégradée";
        else if (pm10 <= 100) return "Mauvaise";
        else return "Très mauvaise";
    }

    private static String calculerConditionsMeteo(DonneeIoT donnee) {
        StringBuilder conditions = new StringBuilder();

        if (donnee.getTemperatureCelsius() != null) {
            double temp = donnee.getTemperatureCelsius();
            if (temp < 0) conditions.append("Très froid");
            else if (temp < 10) conditions.append("Froid");
            else if (temp < 20) conditions.append("Frais");
            else if (temp < 25) conditions.append("Agréable");
            else if (temp < 30) conditions.append("Chaud");
            else conditions.append("Très chaud");
        }

        if (donnee.getPrecipitationMm() != null && donnee.getPrecipitationMm() > 0) {
            if (conditions.length() > 0) conditions.append(", ");
            if (donnee.getPrecipitationMm() < 1) conditions.append("Légères précipitations");
            else if (donnee.getPrecipitationMm() < 5) conditions.append("Précipitations modérées");
            else conditions.append("Fortes précipitations");
        }

        if (donnee.getVitesseVentKph() != null && donnee.getVitesseVentKph() > 20) {
            if (conditions.length() > 0) conditions.append(", ");
            conditions.append("Venteux");
        }

        return conditions.length() > 0 ? conditions.toString() : "Conditions normales";
    }

}
