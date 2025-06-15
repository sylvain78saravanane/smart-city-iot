package com.ensitech.smart_city_iot.controller;

import com.ensitech.smart_city_iot.dto.utilisateurDTO.ResponseUtilisateurDTO;
import com.ensitech.smart_city_iot.entity.Utilisateur;
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

    @PostMapping("/utilisateurs/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginUtilisateur) throws Exception {
        String email = loginUtilisateur.get("email");
        String motDePasse = loginUtilisateur.get("mot_de_passe");
        Utilisateur utilisateur = utilisateurService.login(email, motDePasse);
        if(utilisateur != null){
            return ResponseEntity.ok(utilisateurService);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe ou email invalide");
    }

    @GetMapping("users/{id}")
    public ResponseEntity<ResponseUtilisateurDTO> getUtilisateurById(@Valid @PathVariable Long id){
        try{
            log.debug("Demande utilisateur ID: {}", id);
            ResponseUtilisateurDTO response = utilisateurService.getUtilisateurById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
