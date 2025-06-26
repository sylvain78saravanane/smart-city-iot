package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.DonneIotDTO.DonneeIoTMessage;
import com.ensitech.smart_city_iot.dto.DonneIotDTO.WeatherApiResponseDTO;
import com.ensitech.smart_city_iot.entity.Capteur;
import com.ensitech.smart_city_iot.entity.DonneeIoT;
import com.ensitech.smart_city_iot.repository.CapteurRepository;
import com.ensitech.smart_city_iot.repository.DonneeIoTRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@Transactional
public class DonneeIoTServiceImpl implements DonneeIoTService {

    @Autowired
    private DonneeIoTRepository donneeIoTRepository;

    @Autowired
    private CapteurRepository capteurRepository;

    @Autowired
    private WeatherApiService weatherApiService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;
    private final String TOPIC_NAME = "smart-city-iot";

    public DonneeIoTServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Collecte automatique des données toutes les 5 minutes pour tous les capteurs actifs
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void collecterDonneesAutomatiquement() {
        log.info("Début de la collecte automatique des données IoT");

        List<Capteur> capteursActifs = capteurRepository.findByStatut("ACTIF");

        for (Capteur capteur : capteursActifs) {
            try {
                collecterDonneesPourCapteur(capteur);
            } catch (Exception e) {
                log.error("Erreur lors de la collecte pour le capteur {}: {}",
                        capteur.getNomCapteur(), e.getMessage());
            }
        }

        log.info("Collecte automatique terminée pour {} capteurs", capteursActifs.size());
    }

    /**
     * Collecte des données pour un capteur spécifique
     */
    public void collecterDonneesPourCapteur(Capteur capteur) throws Exception {
        log.debug("Collecte des données pour le capteur: {}", capteur.getNomCapteur());

        WeatherApiResponseDTO weatherData = weatherApiService.getWeatherData(capteur);

        if (weatherData != null) {
            DonneeIoTMessage message = creerMessageKafka(capteur, weatherData);
            envoyerVersKafka(message);
        } else {
            log.warn("Aucune donnée récupérée pour le capteur: {}", capteur.getNomCapteur());
        }
    }

    /**
     * Création du message Kafka à partir des données météo
     */
    private DonneeIoTMessage creerMessageKafka(Capteur capteur, WeatherApiResponseDTO weatherData) {
        LocalDateTime heureLocale = null;
        try {
            // Parser la date locale de l'API (format: "2025-06-19 11:40")
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            heureLocale = LocalDateTime.parse(weatherData.getLocation().getLocaltime(), formatter);
        } catch (Exception e) {
            log.warn("Erreur lors du parsing de la date: {}, utilisation de l'heure actuelle",
                    weatherData.getLocation().getLocaltime());
            heureLocale = LocalDateTime.now();
        }

        return DonneeIoTMessage.builder()
                .idCapteur(capteur.getIdCapteur())
                .villeNom(weatherData.getLocation().getName())
                .region(weatherData.getLocation().getRegion())
                .pays(weatherData.getLocation().getCountry())
                .latitude(weatherData.getLocation().getLat())
                .longitude(weatherData.getLocation().getLon())
                .heureLocale(heureLocale)
                .temperatureCelsius(weatherData.getCurrent().getTempC())
                .temperatureFahrenheit(weatherData.getCurrent().getTempF())
                .vitesseVentKph(weatherData.getCurrent().getWindKph())
                .precipitationMm(weatherData.getCurrent().getPrecipMm())
                .humidite(weatherData.getCurrent().getHumidity())
                .nuageux(weatherData.getCurrent().getCloud())
                .indiceUv(weatherData.getCurrent().getUv())
                .co(weatherData.getCurrent().getAirQuality().getCo())
                .no2(weatherData.getCurrent().getAirQuality().getNo2())
                .o3(weatherData.getCurrent().getAirQuality().getO3())
                .so2(weatherData.getCurrent().getAirQuality().getSo2())
                .pm10(weatherData.getCurrent().getAirQuality().getPm10())
                .timestampCollecte(LocalDateTime.now())
                .sourceApi("WeatherAPI")
                .build();
    }

    /**
     * Envoi du message vers Kafka
     */
    private void envoyerVersKafka(DonneeIoTMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC_NAME, String.valueOf(message.getIdCapteur()), messageJson);

            log.debug("Message envoyé vers Kafka pour le capteur: {}", message.getIdCapteur());
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la sérialisation du message Kafka: {}", e.getMessage());
        }
    }

    /**
     * Consumer Kafka - Traitement des messages reçus
     */
    @KafkaListener(topics = "smart-city-iot", groupId = "smart-city-group")
    public void traiterMessageKafka(String message) {
        try {
            log.debug("Message Kafka reçu: {}", message);

            DonneeIoTMessage donneeMessage = objectMapper.readValue(message, DonneeIoTMessage.class);
            sauvegarderDonneeIoT(donneeMessage);

        } catch (Exception e) {
            log.error("Erreur lors du traitement du message Kafka: {}", e.getMessage(), e);
        }
    }

    /**
     * Sauvegarde des données IoT en base
     */
    private void sauvegarderDonneeIoT(DonneeIoTMessage message) {
        Capteur capteur = capteurRepository.findById(message.getIdCapteur())
                .orElse(null);

        if (capteur == null) {
            log.error("Capteur non trouvé avec l'ID: {}", message.getIdCapteur());
            return;
        }

        DonneeIoT donneeIoT = DonneeIoT.builder()
                .capteur(capteur)
                .villeNom(message.getVilleNom())
                .region(message.getRegion())
                .pays(message.getPays())
                .latitude(message.getLatitude())
                .longitude(message.getLongitude())
                .heureLocale(message.getHeureLocale())
                .temperatureCelsius(message.getTemperatureCelsius())
                .temperatureFahrenheit(message.getTemperatureFahrenheit())
                .vitesseVentKph(message.getVitesseVentKph())
                .precipitationMm(message.getPrecipitationMm())
                .humidite(message.getHumidite())
                .nuageux(message.getNuageux())
                .indiceUv(message.getIndiceUv())
                .co(message.getCo())
                .no2(message.getNo2())
                .o3(message.getO3())
                .so2(message.getSo2())
                .pm10(message.getPm10())
                .timestampCollecte(message.getTimestampCollecte())
                .sourceApi(message.getSourceApi())
                .statutDonnee("VALIDE")
                .build();

        donneeIoTRepository.save(donneeIoT);

        log.info("Données IoT sauvegardées pour le capteur: {} (ville: {})",
                capteur.getNomCapteur(), message.getVilleNom());
    }

    /**
     * Collecte manuelle pour un capteur spécifique
     */
    public void collecterManuellement(Long idCapteur) throws Exception {
        Capteur capteur = capteurRepository.findById(idCapteur)
                .orElseThrow(() -> new RuntimeException("Capteur non trouvé: " + idCapteur));

        collecterDonneesPourCapteur(capteur);
    }
}
