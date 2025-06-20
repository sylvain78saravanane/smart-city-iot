package com.ensitech.smart_city_iot.controller;

import com.ensitech.smart_city_iot.dto.utilisateurDTO.CreateUtilisateurDTO;
import com.ensitech.smart_city_iot.dto.utilisateurDTO.ResponseUtilisateurDTO;
import com.ensitech.smart_city_iot.entity.Administrateur;
import com.ensitech.smart_city_iot.entity.Utilisateur;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/v1")
@Validated
@Slf4j
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @PostMapping("/utilisateurs")
    public ResponseEntity<?> createUtilisateur(@Valid @RequestBody CreateUtilisateurDTO createUtilisateurDTO){
        try {
            log.info("Création d'un nouvel utilisateur: {}", createUtilisateurDTO.getEmail());
            ResponseUtilisateurDTO response = utilisateurService.createUtilisateur(createUtilisateurDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessException e) {
            log.error("Erreur business lors de la création: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'utilisateur", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Map<String, String> loginUtilisateur) throws Exception {
        try {
            String email = loginUtilisateur.get("email");
            String motDePasse = loginUtilisateur.get("mot_de_passe");

            log.info("Tentative de connexion pour: {}", email);

            if (email == null || motDePasse == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Email et mot de passe requis"));
            }

            Utilisateur utilisateur = utilisateurService.login(email, motDePasse);

            if (utilisateur != null) {
                log.info("Connexion réussie pour: {}", email);
                ResponseUtilisateurDTO response = ResponseUtilisateurDTO.fromEntity(utilisateur);
                return ResponseEntity.ok(response);
            } else {
                log.warn("Échec de connexion pour: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Email ou mot de passe invalide"));
            }

        } catch (Exception e) {
            log.error("Erreur lors de la connexion: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur", "details", e.getMessage()));
        }
    }

    @GetMapping("utilisateurs/{id}")
    public ResponseEntity<?> getUtilisateurById(@Valid @PathVariable Long id){
        try{
            log.debug("Demande utilisateur ID: {}", id);

            ResponseUtilisateurDTO response = utilisateurService.getUtilisateurById(id);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException entityNotFoundException) {
            log.warn("Utilisateur non trouvé ID: {}",id,entityNotFoundException);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur non trouvé avec l'ID: " + id));

        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur ID: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@Valid @RequestBody Map<String, String> loginData) throws Exception {
        try {
            String email = loginData.get("email");
            String motDePasse = loginData.get("mot_de_passe");
            String codeAdmin = loginData.get("code_admin");

            log.info("Tentative de connexion administrateur pour: {}", email);

            if (email == null || motDePasse == null || codeAdmin == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Email, mot de passe et code administrateur requis"));
            }

            // Vérification du format du code admin (4 chiffres)
            if (!codeAdmin.matches("\\d{4}")) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(Map.of("error", "Le code administrateur doit contenir exactement 4 chiffres"));
            }

            // Authentification de base
            Utilisateur utilisateur = utilisateurService.login(email, motDePasse);

            if (utilisateur != null && utilisateur instanceof Administrateur) {
                Administrateur admin = (Administrateur) utilisateur;

                // Vérification du code administrateur
                if (!codeAdmin.equals(admin.getCodeAdmin())) {
                    log.warn("Code administrateur incorrect pour: {}", email);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "Code administrateur incorrect"));
                }

                log.info("Connexion administrateur réussie pour: {}", email);
                ResponseUtilisateurDTO response = ResponseUtilisateurDTO.fromEntity(admin);
                return ResponseEntity.ok(response);
            } else {
                log.warn("Échec de connexion administrateur pour: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Identifiants administrateur invalides"));
            }

        } catch (Exception e) {
            log.error("Erreur lors de la connexion administrateur: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur", "details", e.getMessage()));
        }
    }
}
