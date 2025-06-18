package com.ensitech.smart_city_iot.controller;

import com.ensitech.smart_city_iot.dto.commentaireDTO.CreateCommentaireDTO;
import com.ensitech.smart_city_iot.dto.commentaireDTO.ResponseCommentaireDTO;
import com.ensitech.smart_city_iot.dto.commentaireDTO.UpdateCommentaireDTO;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.service.CommentaireService;
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
public class CommentaireController {
    @Autowired
    private CommentaireService commentaireService;

    @PostMapping("/commentaires")
    public ResponseEntity<?> createCommentaire(@Valid @RequestBody CreateCommentaireDTO createDto) {
        try {
            log.info("Création d'un nouveau commentaire par le citoyen: {}", createDto.getIdCitoyen());
            ResponseCommentaireDTO response = commentaireService.createCommentaire(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessException e) {
            log.error("Erreur business lors de la création du commentaire: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.error("Citoyen non trouvé: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la création du commentaire", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/commentaires/{id}")
    public ResponseEntity<?> getCommentaireById(@PathVariable Long id) {
        try {
            log.debug("Demande commentaire ID: {}", id);
            ResponseCommentaireDTO response = commentaireService.getCommentaireById(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Citoyen non trouvé ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Commentaire non trouvé avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des commentaires pour le citoyen ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/commentaires/sujet/{sujet}")
    public ResponseEntity<?> getCommentairesBySujet(@PathVariable String sujet) {
        try {
            log.debug("Demande commentaires pour le sujet: {}", sujet);
            List<ResponseCommentaireDTO> commentaires = commentaireService.getCommentairesBySujet(sujet);
            return ResponseEntity.ok(Map.of(
                    "commentaires", commentaires,
                    "total", commentaires.size(),
                    "sujet", sujet
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des commentaires pour le sujet: {}", sujet, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/commentaires/recherche")
    public ResponseEntity<?> rechercherCommentaires(@RequestParam String q) {
        try {
            log.debug("Recherche de commentaires avec le terme: {}", q);
            List<ResponseCommentaireDTO> commentaires = commentaireService.rechercherCommentaires(q);
            return ResponseEntity.ok(Map.of(
                    "commentaires", commentaires,
                    "total", commentaires.size(),
                    "terme_recherche", q
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de commentaires", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }


    @GetMapping("/commentaires/populaires")
    public ResponseEntity<?> getCommentairesPopulaires(@RequestParam(defaultValue = "10") int limite) {
        try {
            log.debug("Demande des commentaires populaires, limite: {}", limite);
            List<ResponseCommentaireDTO> commentaires = commentaireService.getCommentairesPopulaires(limite);
            return ResponseEntity.ok(Map.of(
                    "commentaires", commentaires,
                    "total", commentaires.size(),
                    "limite", limite
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des commentaires populaires", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/commentaires/recents")
    public ResponseEntity<?> getCommentairesRecents(@RequestParam(defaultValue = "7") int jours) {
        try {
            log.debug("Demande des commentaires récents, derniers {} jours", jours);
            List<ResponseCommentaireDTO> commentaires = commentaireService.getCommentairesRecents(jours);
            return ResponseEntity.ok(Map.of(
                    "commentaires", commentaires,
                    "total", commentaires.size(),
                    "periode_jours", jours
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des commentaires récents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PutMapping("/commentaires/{id}")
    public ResponseEntity<?> updateCommentaire(@PathVariable Long id, @Valid @RequestBody UpdateCommentaireDTO updateDto) {
        try {
            log.info("Mise à jour du commentaire ID: {}", id);
            ResponseCommentaireDTO response = commentaireService.updateCommentaire(id, updateDto);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Commentaire non trouvé pour mise à jour ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Commentaire non trouvé avec l'ID: " + id));
        } catch (BusinessException e) {
            log.error("Erreur business lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du commentaire ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @DeleteMapping("/commentaires/{id}")
    public ResponseEntity<?> deleteCommentaire(@PathVariable Long id) {
        try {
            log.info("Suppression du commentaire ID: {}", id);
            commentaireService.deleteCommentaire(id);
            return ResponseEntity.ok(Map.of("message", "Commentaire supprimé avec succès"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative de suppression d'un commentaire inexistant ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Commentaire non trouvé avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du commentaire ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PatchMapping("/commentaires/{id}/desactiver")
    public ResponseEntity<?> softDeleteCommentaire(@PathVariable Long id) {
        try {
            log.info("Désactivation du commentaire ID: {}", id);
            commentaireService.softDeleteCommentaire(id);
            return ResponseEntity.ok(Map.of("message", "Commentaire désactivé avec succès"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative de désactivation d'un commentaire inexistant ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Commentaire non trouvé avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la désactivation du commentaire ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    // Gestion des likes/dislikes
    @PostMapping("/commentaires/{id}/like")
    public ResponseEntity<?> ajouterLike(@PathVariable Long id) {
        try {
            commentaireService.ajouterLike(id);
            return ResponseEntity.ok(Map.of("message", "Like ajouté avec succès"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Commentaire non trouvé"));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de l'ajout du like", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @DeleteMapping("/commentaires/{id}/like")
    public ResponseEntity<?> retirerLike(@PathVariable Long id) {
        try {
            commentaireService.retirerLike(id);
            return ResponseEntity.ok(Map.of("message", "Like retiré avec succès"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Commentaire non trouvé"));
        } catch (Exception e) {
            log.error("Erreur lors du retrait du like", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PostMapping("/commentaires/{id}/dislike")
    public ResponseEntity<?> ajouterDislike(@PathVariable Long id) {
        try {
            commentaireService.ajouterDislike(id);
            return ResponseEntity.ok(Map.of("message", "Dislike ajouté avec succès"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Commentaire non trouvé"));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de l'ajout du dislike", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @DeleteMapping("/commentaires/{id}/dislike")
    public ResponseEntity<?> retirerDislike(@PathVariable Long id) {
        try {
            commentaireService.retirerDislike(id);
            return ResponseEntity.ok(Map.of("message", "Dislike retiré avec succès"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Commentaire non trouvé"));
        } catch (Exception e) {
            log.error("Erreur lors du retrait du dislike", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }
}
