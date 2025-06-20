package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.DonneIotDTO.WeatherApiResponseDTO;
import com.ensitech.smart_city_iot.entity.Capteur;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class WeatherApiServiceImpl implements WeatherApiService{

    @Autowired
    private RestTemplate restTemplate;

    @Value("${weather.api.key:YOUR_API_KEY}")
    private String apiKey;

    @Value("${weather.api.url:http://api.weatherapi.com/v1/current.json}")
    private String apiUrl;

    // Mapping des villes françaises pour la simulation "Tech City"
    private final Map<String, String> villesFrancaises = Map.of(
            "PARIS", "Paris",
            "LYON", "Lyon",
            "MARSEILLE", "Marseille",
            "TOULOUSE", "Toulouse",
            "NICE", "Nice",
            "NANTES", "Nantes",
            "STRASBOURG", "Strasbourg",
            "MONTPELLIER", "Montpellier",
            "BORDEAUX", "Bordeaux",
            "LILLE", "Lille"
    );

    public WeatherApiResponseDTO getWeatherData(Capteur capteur) throws Exception {
        try {
            // Déterminer la ville à utiliser pour la simulation
            String ville = determinerVillePourCapteur(capteur);

            String url = String.format("%s?key=%s&q=%s&aqi=yes",
                    apiUrl, apiKey, ville);

            log.info("Récupération des données météo pour le capteur {} en simulant la ville: {}",
                    capteur.getNomCapteur(), ville);

            ResponseEntity<WeatherApiResponseDTO> response = restTemplate.getForEntity(url, WeatherApiResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.debug("Données météo récupérées avec succès pour {}", ville);
                return response.getBody();
            } else {
                log.error("Erreur lors de la récupération des données météo: {}", response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'appel à l'API Weather: {}", e.getMessage(), e);
            return null;
        }
    }

    private String determinerVillePourCapteur(Capteur capteur) {
        // Si le capteur a des coordonnées GPS, utiliser Paris par défaut pour "Tech City"
        if (capteur.getLatitude() != null && capteur.getLongitude() != null) {
            // Pour la simulation, on peut mapper les coordonnées à différentes villes
            return mapperCoordonneeVersVille(capteur.getLatitude(), capteur.getLongitude());
        }

        // Si pas de coordonnées, utiliser Paris par défaut
        return "Paris";
    }

    private String mapperCoordonneeVersVille(Double latitude, Double longitude) {
        // Mapping simple basé sur les coordonnées pour simuler différentes zones de "Tech City"

        // Zone Nord (Lille/Nord de la France)
        if (latitude > 49.0) {
            return "Lille";
        }
        // Zone Sud (Nice/Côte d'Azur)
        else if (latitude < 44.0) {
            return "Nice";
        }
        // Zone Ouest (Nantes/Atlantique)
        else if (longitude < 0.0) {
            return "Nantes";
        }
        // Zone Est (Strasbourg/Est)
        else if (longitude > 5.0) {
            return "Strasbourg";
        }
        // Zone Centre-Sud (Toulouse)
        else if (latitude < 46.0) {
            return "Toulouse";
        }
        // Zone Lyon
        else if (latitude < 47.0 && longitude > 3.0) {
            return "Lyon";
        }
        // Par défaut Paris (centre de "Tech City")
        else {
            return "Paris";
        }
    }

    public Map<String, String> getVillesFrancaisesDisponibles() {
        return villesFrancaises;
    }
}
