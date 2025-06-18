package com.ensitech.smart_city_iot.controller;

import com.ensitech.smart_city_iot.dto.capteurDTO.CreateCapteurDTO;
import com.ensitech.smart_city_iot.dto.capteurDTO.ResponseCapteurDTO;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.service.CapteurService;
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
public class CapteurController {

    @Autowired
    private CapteurService capteurService;

    @PostMapping("/capteurs")
    public ResponseEntity<?> createCapteur(@Valid @RequestBody CreateCapteurDTO createDto) {
        try {
            log.info("Création d'un nouveau capteur: {}", createDto.getNomCapteur());
            ResponseCapteurDTO response = capteurService.createCapteur(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessException e) {
            log.error("Erreur business lors de la création du capteur: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.error("Gestionnaire non trouvé: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la création du capteur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/capteurs/{id}")
    public ResponseEntity<?> getCapteurById(@PathVariable Long id) {
        try {
            log.debug("Demande capteur ID: {}", id);
            ResponseCapteurDTO response = capteurService.getCapteurById(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Capteur non trouvé ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Capteur non trouvé avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du capteur ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/capteurs")
    public ResponseEntity<?> getAllCapteurs() {
        try {
            log.debug("Demande de tous les capteurs");
            List<ResponseCapteurDTO> capteurs = capteurService.getAllCapteurs();
            return ResponseEntity.ok(Map.of(
                    "capteurs", capteurs,
                    "total", capteurs.size()
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des capteurs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/gestionnaires/{idGestionnaire}/capteurs")
    public ResponseEntity<?> getCapteursByGestionnaire(@PathVariable Long idGestionnaire) {
        try {
            log.debug("Demande capteurs pour gestionnaire ID: {}", idGestionnaire);
            List<ResponseCapteurDTO> capteurs = capteurService.getCapteursByGestionnaire(idGestionnaire);
            return ResponseEntity.ok(Map.of(
                    "capteurs", capteurs,
                    "total", capteurs.size(),
                    "gestionnaire_id", idGestionnaire
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des capteurs pour le gestionnaire ID: {}", idGestionnaire, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @DeleteMapping("/capteurs/{id}")
    public ResponseEntity<?> deleteCapteur(@PathVariable Long id) {
        try {
            log.info("Suppression du capteur ID: {}", id);
            capteurService.deleteCapteur(id);
            return ResponseEntity.ok(Map.of("message", "Capteur supprimé avec succès"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative de suppression d'un capteur inexistant ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Capteur non trouvé avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du capteur ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }
}
