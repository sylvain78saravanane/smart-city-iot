package com.ensitech.smart_city_iot.controller;

import com.ensitech.smart_city_iot.dto.utilisateurDTO.CreateUtilisateurDTO;
import com.ensitech.smart_city_iot.dto.utilisateurDTO.LoginResponseDTO;
import com.ensitech.smart_city_iot.dto.utilisateurDTO.ResponseUtilisateurDTO;
import com.ensitech.smart_city_iot.entity.Administrateur;
import com.ensitech.smart_city_iot.entity.Utilisateur;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.service.UtilisateurService;
import com.ensitech.smart_city_iot.config.jwt.JWTService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1")
@Validated
@Slf4j
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private JWTService jwtService;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

            Utilisateur utilisateur = utilisateurService.findByEmail(email);

            // Vérifier le mot de passe
            if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
                log.warn("Mot de passe incorrect pour: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Email ou mot de passe incorrect"));
            }

            // Vérifier que le compte est actif
            if (!utilisateur.isActif()) {
                log.warn("Compte désactivé pour: {}", email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Votre compte est désactivé"));
            }

            // Générer le token JWT
            String token = jwtService.generateToken(
                    utilisateur.getEmail(),
                    utilisateur.getRole(),
                    utilisateur.getIdUtilisateur()
            );

            // Créer la réponse
            LoginResponseDTO response = LoginResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .utilisateur(ResponseUtilisateurDTO.fromEntity(utilisateur))
                    .expiresIn(jwtExpiration)
                    .build();

            log.info("Connexion réussie pour: {}", email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la connexion: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
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
            Utilisateur utilisateur = utilisateurService.findByEmail(email);

            // Vérifier le mot de passe
            if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
                log.warn("Mot de passe incorrect pour: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Email ou mot de passe incorrect"));
            }

            // Vérifier que c'est un administrateur
            if (!(utilisateur instanceof Administrateur)) {
                log.warn("Tentative d'accès admin avec un compte non-admin: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Identifiants administrateur invalides"));
            }

            Administrateur admin = (Administrateur) utilisateur;

            // Vérification du code administrateur
            if (!codeAdmin.equals(admin.getCodeAdmin())) {
                log.warn("Code administrateur incorrect pour: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Code administrateur incorrect"));
            }

            // Vérifier que le compte est actif
            if (!admin.isActif()) {
                log.warn("Compte administrateur désactivé pour: {}", email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Votre compte administrateur est désactivé"));
            }

            // Générer le token JWT pour l'admin
            String token = jwtService.generateToken(
                    admin.getEmail(),
                    admin.getRole(),
                    admin.getIdUtilisateur()
            );

            // Créer la réponse avec token (comme pour login normal)
            LoginResponseDTO response = LoginResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .utilisateur(ResponseUtilisateurDTO.fromEntity(admin))
                    .expiresIn(jwtExpiration)
                    .build();

            log.info("Connexion administrateur réussie pour: {}", email);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.warn("Utilisateur administrateur non trouvé: {}", loginData.get("email"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Identifiants administrateur invalides"));
        } catch (Exception e) {
            log.error("Erreur lors de la connexion administrateur: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }
}
