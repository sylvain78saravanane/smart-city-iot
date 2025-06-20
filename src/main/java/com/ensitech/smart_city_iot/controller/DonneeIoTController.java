package com.ensitech.smart_city_iot.controller;

import com.ensitech.smart_city_iot.dto.DonneIotDTO.ResponseDonneeIoTDTO;
import com.ensitech.smart_city_iot.entity.DonneeIoT;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.repository.DonneeIoTRepository;
import com.ensitech.smart_city_iot.service.DonneeIoTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/v1")
@Validated
@Slf4j
public class DonneeIoTController {

    @Autowired
    private DonneeIoTService donneeIoTService;

    @Autowired
    private DonneeIoTRepository donneeIoTRepository;

    /**
     * Déclencher une collecte manuelle pour un capteur spécifique
     */
    @PostMapping("/capteurs/{idCapteur}/collecter")
    public ResponseEntity<?> collecterDonneesManuellement(@PathVariable Long idCapteur) {
        try {
            log.info("Collecte manuelle demandée pour le capteur ID: {}", idCapteur);
            donneeIoTService.collecterManuellement(idCapteur);
            return ResponseEntity.ok(Map.of("message", "Collecte démarrée pour le capteur " + idCapteur));
        } catch (Exception e) {
            log.error("Erreur lors de la collecte manuelle pour le capteur {}: {}", idCapteur, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la collecte: " + e.getMessage()));
        }
    }

    /**
     * Obtenir toutes les données IoT d'un capteur avec pagination
     */
    @GetMapping("/capteurs/{idCapteur}/donnees")
    public ResponseEntity<?> getDonneesByCapteur(
            @PathVariable Long idCapteur,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestampCollecte") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            log.debug("Récupération des données pour le capteur ID: {}", idCapteur);

            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<DonneeIoT> donneesPage = donneeIoTRepository.findByCapteurIdCapteur(idCapteur, pageable);

            Page<ResponseDonneeIoTDTO> responsePage = donneesPage.map(ResponseDonneeIoTDTO::fromEntity);

            return ResponseEntity.ok(Map.of(
                    "donnees", responsePage.getContent(),
                    "totalElements", responsePage.getTotalElements(),
                    "totalPages", responsePage.getTotalPages(),
                    "currentPage", responsePage.getNumber(),
                    "size", responsePage.getSize(),
                    "capteur_id", idCapteur
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des données pour le capteur {}: {}", idCapteur, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Obtenir les dernières données d'un capteur
     */
    @GetMapping("/capteurs/{idCapteur}/donnees/latest")
    public ResponseEntity<?> getLatestDonneesByCapteur(
            @PathVariable Long idCapteur,
            @RequestParam(defaultValue = "10") int limite) {
        try {
            log.debug("Récupération des {} dernières données pour le capteur ID: {}", limite, idCapteur);

            Pageable pageable = PageRequest.of(0, limite);
            List<DonneeIoT> donnees = donneeIoTRepository.findLatestByCapteur(idCapteur, pageable);

            List<ResponseDonneeIoTDTO> response = donnees.stream()
                    .map(ResponseDonneeIoTDTO::fromEntity)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "donnees", response,
                    "total", response.size(),
                    "capteur_id", idCapteur,
                    "limite", limite
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des dernières données pour le capteur {}: {}", idCapteur, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Obtenir les données d'un capteur pour une période donnée
     */
    @GetMapping("/capteurs/{idCapteur}/donnees/periode")
    public ResponseEntity<?> getDonneesByCapteurAndPeriode(
            @PathVariable Long idCapteur,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            log.debug("Récupération des données pour le capteur {} entre {} et {}", idCapteur, dateDebut, dateFin);

            List<DonneeIoT> donnees = donneeIoTRepository.findByCapteurAndPeriode(idCapteur, dateDebut, dateFin);

            List<ResponseDonneeIoTDTO> response = donnees.stream()
                    .map(ResponseDonneeIoTDTO::fromEntity)
                    .toList();

            return ResponseEntity.ok(Map.of(
                    "donnees", response,
                    "total", response.size(),
                    "capteur_id", idCapteur,
                    "periode_debut", dateDebut,
                    "periode_fin", dateFin
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des données par période: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Obtenir une donnée IoT spécifique par ID
     */
    @GetMapping("/donnees/{id}")
    public ResponseEntity<?> getDonneeById(@PathVariable Long id) {
        try {
            log.debug("Récupération de la donnée ID: {}", id);

            DonneeIoT donnee = donneeIoTRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Donnée IoT non trouvée avec l'ID: " + id));

            ResponseDonneeIoTDTO response = ResponseDonneeIoTDTO.fromEntity(donnee);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("Donnée IoT non trouvée ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Donnée IoT non trouvée avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la donnée ID: {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Obtenir les statistiques de température pour un capteur
     */
    @GetMapping("/capteurs/{idCapteur}/statistiques/temperature")
    public ResponseEntity<?> getStatistiquesTemperature(
            @PathVariable Long idCapteur,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            log.debug("Calcul des statistiques de température pour le capteur {}", idCapteur);

            Double moyenne = donneeIoTRepository.getAverageTemperatureByCapteurAndPeriode(idCapteur, dateDebut, dateFin);
            Double maximum = donneeIoTRepository.getMaxTemperatureByCapteurAndPeriode(idCapteur, dateDebut, dateFin);
            Double minimum = donneeIoTRepository.getMinTemperatureByCapteurAndPeriode(idCapteur, dateDebut, dateFin);

            return ResponseEntity.ok(Map.of(
                    "capteur_id", idCapteur,
                    "periode_debut", dateDebut,
                    "periode_fin", dateFin,
                    "temperature_moyenne", moyenne != null ? moyenne : 0.0,
                    "temperature_maximum", maximum != null ? maximum : 0.0,
                    "temperature_minimum", minimum != null ? minimum : 0.0
            ));
        } catch (Exception e) {
            log.error("Erreur lors du calcul des statistiques de température: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Obtenir les statistiques de qualité de l'air pour un capteur
     */
    @GetMapping("/capteurs/{idCapteur}/statistiques/air")
    public ResponseEntity<?> getStatistiquesQualiteAir(
            @PathVariable Long idCapteur,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {
        try {
            log.debug("Calcul des statistiques de qualité de l'air pour le capteur {}", idCapteur);

            Double moyenneCO = donneeIoTRepository.getAverageCOByCapteurAndPeriode(idCapteur, dateDebut, dateFin);

            return ResponseEntity.ok(Map.of(
                    "capteur_id", idCapteur,
                    "periode_debut", dateDebut,
                    "periode_fin", dateFin,
                    "co_moyenne", moyenneCO != null ? moyenneCO : 0.0
            ));
        } catch (Exception e) {
            log.error("Erreur lors du calcul des statistiques de qualité de l'air: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Obtenir les données récentes (dernières 24h par défaut)
     */
    @GetMapping("/donnees/recentes")
    public ResponseEntity<?> getDonneesRecentes(@RequestParam(defaultValue = "24") int heures) {
        try {
            log.debug("Récupération des données des dernières {} heures", heures);

            LocalDateTime dateDebut = LocalDateTime.now().minusHours(heures);
            List<DonneeIoT> donnees = donneeIoTRepository.findRecentData(dateDebut);

            List<ResponseDonneeIoTDTO> response = donnees.stream()
                    .map(ResponseDonneeIoTDTO::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "donnees", response,
                    "total", response.size(),
                    "periode_heures", heures
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des données récentes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Obtenir les données par ville (pour la simulation Tech City)
     */
    @GetMapping("/donnees/ville/{nomVille}")
    public ResponseEntity<?> getDonneesByVille(@PathVariable String nomVille) {
        try {
            log.debug("Récupération des données pour la ville: {}", nomVille);

            List<DonneeIoT> donnees = donneeIoTRepository.findByVilleNom(nomVille);

            List<ResponseDonneeIoTDTO> response = donnees.stream()
                    .map(ResponseDonneeIoTDTO::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                    "donnees", response,
                    "total", response.size(),
                    "ville", nomVille
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des données pour la ville {}: {}", nomVille, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    /**
     * Obtenir le nombre total de données collectées par capteur
     */
    @GetMapping("/capteurs/{idCapteur}/statistiques/count")
    public ResponseEntity<?> getCountDonneesByCapteur(@PathVariable Long idCapteur) {
        try {
            log.debug("Comptage des données pour le capteur ID: {}", idCapteur);

            Long count = donneeIoTRepository.countByCapteur(idCapteur);

            return ResponseEntity.ok(Map.of(
                    "capteur_id", idCapteur,
                    "nombre_donnees", count
            ));
        } catch (Exception e) {
            log.error("Erreur lors du comptage des données pour le capteur {}: {}", idCapteur, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }
}
