package com.ensitech.smart_city_iot.controller;

import com.ensitech.smart_city_iot.dto.alerteDTO.CreateAlerteDTO;
import com.ensitech.smart_city_iot.dto.alerteDTO.ResponseAlerteDTO;
import com.ensitech.smart_city_iot.dto.alerteDTO.UpdateAlerteDTO;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.service.AlerteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/v1")
@Validated
@Slf4j
public class AlerteController {
    @Autowired
    private AlerteService alerteService;

    @PostMapping("/alertes")
    public ResponseEntity<?> createAlerte(@Valid @RequestBody CreateAlerteDTO createDto) {
        try {
            log.info("Création d'une nouvelle alerte: {}", createDto.getTitre());
            ResponseAlerteDTO response = alerteService.createAlerte(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessException e) {
            log.error("Erreur business lors de la création de l'alerte: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.error("Capteur non trouvé: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'alerte", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/alertes/{id}")
    public ResponseEntity<?> getAlerteById(@PathVariable Long id) {
        try {
            log.debug("Demande alerte ID: {}", id);
            ResponseAlerteDTO response = alerteService.getAlerteById(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Alerte non trouvée ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Alerte non trouvée avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'alerte ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/alertes")
    public ResponseEntity<?> getAllAlertes() {
        try {
            log.debug("Demande de toutes les alertes");
            List<ResponseAlerteDTO> alertes = alerteService.getAllAlertes();
            return ResponseEntity.ok(Map.of(
                    "alertes", alertes,
                    "total", alertes.size()
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des alertes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/alertes/actives")
    public ResponseEntity<?> getAlertesActives() {
        try {
            log.debug("Demande des alertes actives");
            List<ResponseAlerteDTO> alertes = alerteService.getAlertesActives();
            return ResponseEntity.ok(Map.of(
                    "alertes", alertes,
                    "total", alertes.size()
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des alertes actives", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/capteurs/{idCapteur}/alertes")
    public ResponseEntity<?> getAlertesByCapteur(@PathVariable Long idCapteur) {
        try {
            log.debug("Demande alertes pour capteur ID: {}", idCapteur);
            List<ResponseAlerteDTO> alertes = alerteService.getAlertesByCapteur(idCapteur);
            return ResponseEntity.ok(Map.of(
                    "alertes", alertes,
                    "total", alertes.size(),
                    "capteur_id", idCapteur
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des alertes pour le capteur ID: {}", idCapteur, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PutMapping("/alertes/{id}")
    public ResponseEntity<?> updateAlerte(@PathVariable Long id, @Valid @RequestBody UpdateAlerteDTO updateDto) {
        try {
            log.info("Mise à jour de l'alerte ID: {}", id);
            ResponseAlerteDTO response = alerteService.updateAlerte(id, updateDto);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Alerte non trouvée pour mise à jour ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Alerte non trouvée avec l'ID: " + id));
        } catch (BusinessException e) {
            log.error("Erreur business lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de l'alerte ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @DeleteMapping("/alertes/{id}")
    public ResponseEntity<?> deleteAlerte(@PathVariable Long id) {
        try {
            log.info("Suppression de l'alerte ID: {}", id);
            alerteService.deleteAlerte(id);
            return ResponseEntity.ok(Map.of("message", "Alerte supprimée avec succès"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative de suppression d'une alerte inexistante ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Alerte non trouvée avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'alerte ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PatchMapping("/alertes/{id}/activer")
    public ResponseEntity<?> activerAlerte(@PathVariable Long id) {
        try {
            log.info("Activation de l'alerte ID: {}", id);
            alerteService.activerAlerte(id);
            return ResponseEntity.ok(Map.of("message", "Alerte activée avec succès"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative d'activation d'une alerte inexistante ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Alerte non trouvée avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de l'activation de l'alerte ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PatchMapping("/alertes/{id}/desactiver")
    public ResponseEntity<?> desactiverAlerte(@PathVariable Long id) {
        try {
            log.info("Désactivation de l'alerte ID: {}", id);
            alerteService.desactiverAlerte(id);
            return ResponseEntity.ok(Map.of("message", "Alerte désactivée avec succès"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative de désactivation d'une alerte inexistante ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Alerte non trouvée avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la désactivation de l'alerte ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }
}
