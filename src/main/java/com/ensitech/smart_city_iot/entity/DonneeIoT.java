package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "donnee_iot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonneeIoT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_donnee_iot")
    private Long idDonneeIoT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_capteur", nullable = false)
    private Capteur capteur;

    // Données de localisation
    @Column(name = "ville_nom", length = 100)
    private String villeNom;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "pays", length = 100)
    private String pays;

    @Column(name = "latitude", precision = 10)
    private Double latitude;

    @Column(name = "longitude", precision = 10)
    private Double longitude;

    @Column(name = "heure_locale")
    private LocalDateTime heureLocale;

    // Données météorologiques
    @Column(name = "temperature_celsius", precision = 5)
    private Double temperatureCelsius;

    @Column(name = "temperature_fahrenheit", precision = 5)
    private Double temperatureFahrenheit;

    @Column(name = "vitesse_vent_kph", precision = 5)
    private Double vitesseVentKph;

    @Column(name = "precipitation_mm", precision = 5)
    private Double precipitationMm;

    @Column(name = "humidite", precision = 3)
    private Double humidite;

    @Column(name = "nuageux", precision = 3)
    private Double nuageux;

    @Column(name = "indice_uv", precision = 4)
    private Double indiceUv;

    // Données qualité de l'air
    @Column(name = "co", precision = 8)
    private Double co;

    @Column(name = "no2", precision = 8)
    private Double no2;

    @Column(name = "o3", precision = 8)
    private Double o3;

    @Column(name = "so2", precision = 8)
    private Double so2;

    @Column(name = "pm10", precision = 8)
    private Double pm10;


    @Column(name = "timestamp_collecte", nullable = false)
    private LocalDateTime timestampCollecte;

    @Column(name = "statut_donnee", length = 20)
    private String statutDonnee = "VALIDE"; // VALIDE, INVALIDE, EN_ATTENTE

    @Column(name = "source_api", length = 50)
    private String sourceApi = "WeatherAPI";

    @PrePersist
    public void prePersist() {
        this.timestampCollecte = LocalDateTime.now();
        if (this.statutDonnee == null) {
            this.statutDonnee = "VALIDE";
        }
    }

    // Méthodes utilitaires
    public boolean isDonneeValide() {
        return "VALIDE".equals(this.statutDonnee);
    }

    public String getCoordonnees() {
        return latitude + ", " + longitude;
    }

    public String getLocalisation() {
        return villeNom + ", " + region + ", " + pays;
    }

}
