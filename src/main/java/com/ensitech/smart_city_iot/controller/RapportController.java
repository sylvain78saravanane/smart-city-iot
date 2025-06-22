package com.ensitech.smart_city_iot.controller;

import com.ensitech.smart_city_iot.dto.rapportDTO.CreateRapportDTO;
import com.ensitech.smart_city_iot.dto.rapportDTO.ResponseRapportDTO;
import com.ensitech.smart_city_iot.dto.rapportDTO.UpdateRapportDTO;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.service.RapportService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/v1")
@Validated
@Slf4j
public class RapportController {

    @Autowired
    private RapportService rapportService;

    @PostMapping("/rapports")
    public ResponseEntity<?> createRapport(@Valid @RequestBody CreateRapportDTO createDto) {
        try {
            log.info("Création d'un nouveau rapport: {}", createDto.getNomRapport());
            ResponseRapportDTO response = rapportService.createRapport(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessException e) {
            log.error("Erreur business lors de la création du rapport: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.error("Chercheur non trouvé: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la création du rapport", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/rapports/{id}")
    public ResponseEntity<?> getRapportById(@PathVariable Long id) {
        try {
            log.debug("Demande rapport ID: {}", id);
            ResponseRapportDTO response = rapportService.getRapportById(id);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Rapport non trouvé ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Rapport non trouvé avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du rapport ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/rapports")
    public ResponseEntity<?> getAllRapports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            log.debug("Demande de tous les rapports");

            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() :
                    Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ResponseRapportDTO> rapportsPage = rapportService.getAllRapports(pageable);

            return ResponseEntity.ok(Map.of(
                    "rapports", rapportsPage.getContent(),
                    "totalElements", rapportsPage.getTotalElements(),
                    "totalPages", rapportsPage.getTotalPages(),
                    "currentPage", rapportsPage.getNumber(),
                    "size", rapportsPage.getSize()
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des rapports", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/chercheurs/{idChercheur}/rapports")
    public ResponseEntity<?> getRapportsByChercheur(@PathVariable Long idChercheur) {
        try {
            log.debug("Demande rapports pour chercheur ID: {}", idChercheur);
            List<ResponseRapportDTO> rapports = rapportService.getRapportsByChercheur(idChercheur);
            return ResponseEntity.ok(Map.of(
                    "rapports", rapports,
                    "total", rapports.size(),
                    "chercheur_id", idChercheur
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des rapports pour le chercheur ID: {}", idChercheur, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/rapports/statut/{statut}")
    public ResponseEntity<?> getRapportsByStatut(@PathVariable String statut) {
        try {
            log.debug("Demande rapports avec statut: {}", statut);
            List<ResponseRapportDTO> rapports = rapportService.getRapportsByStatut(statut);
            return ResponseEntity.ok(Map.of(
                    "rapports", rapports,
                    "total", rapports.size(),
                    "statut", statut
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des rapports par statut: {}", statut, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/rapports/type/{typeRapport}")
    public ResponseEntity<?> getRapportsByType(@PathVariable String typeRapport) {
        try {
            log.debug("Demande rapports de type: {}", typeRapport);
            List<ResponseRapportDTO> rapports = rapportService.getRapportsByType(typeRapport);
            return ResponseEntity.ok(Map.of(
                    "rapports", rapports,
                    "total", rapports.size(),
                    "type", typeRapport
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des rapports par type: {}", typeRapport, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/rapports/recherche")
    public ResponseEntity<?> rechercherRapports(@RequestParam String q) {
        try {
            log.debug("Recherche de rapports avec le terme: {}", q);
            List<ResponseRapportDTO> rapports = rapportService.rechercherRapports(q);
            return ResponseEntity.ok(Map.of(
                    "rapports", rapports,
                    "total", rapports.size(),
                    "terme_recherche", q
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de rapports", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/rapports/recents")
    public ResponseEntity<?> getRapportsRecents(@RequestParam(defaultValue = "30") int jours) {
        try {
            log.debug("Demande des rapports récents, derniers {} jours", jours);
            List<ResponseRapportDTO> rapports = rapportService.getRapportsRecents(jours);
            return ResponseEntity.ok(Map.of(
                    "rapports", rapports,
                    "total", rapports.size(),
                    "periode_jours", jours
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des rapports récents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/rapports/periode")
    public ResponseEntity<?> getRapportsByPeriodeAnalyse(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        try {
            log.debug("Demande rapports pour période d'analyse: {} à {}", debut, fin);
            List<ResponseRapportDTO> rapports = rapportService.getRapportsByPeriodeAnalyse(debut, fin);
            return ResponseEntity.ok(Map.of(
                    "rapports", rapports,
                    "total", rapports.size(),
                    "periode_debut", debut,
                    "periode_fin", fin
            ));
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des rapports par période", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PutMapping("/rapports/{id}")
    public ResponseEntity<?> updateRapport(@PathVariable Long id, @Valid @RequestBody UpdateRapportDTO updateDto) {
        try {
            log.info("Mise à jour du rapport ID: {}", id);
            ResponseRapportDTO response = rapportService.updateRapport(id, updateDto);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.warn("Rapport non trouvé pour mise à jour ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Rapport non trouvé avec l'ID: " + id));
        } catch (BusinessException e) {
            log.error("Erreur business lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du rapport ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @DeleteMapping("/rapports/{id}")
    public ResponseEntity<?> deleteRapport(@PathVariable Long id) {
        try {
            log.info("Suppression du rapport ID: {}", id);
            rapportService.deleteRapport(id);
            return ResponseEntity.ok(Map.of("message", "Rapport supprimé avec succès"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative de suppression d'un rapport inexistant ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Rapport non trouvé avec l'ID: " + id));
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du rapport ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @PostMapping("/rapports/{id}/generer")
    public ResponseEntity<?> genererContenuRapport(@PathVariable Long id) {
        try {
            log.info("Génération du contenu pour le rapport ID: {}", id);
            rapportService.genererContenuRapport(id);
            return ResponseEntity.ok(Map.of("message", "Génération du rapport démarrée avec succès"));
        } catch (EntityNotFoundException e) {
            log.warn("Tentative de génération pour un rapport inexistant ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Rapport non trouvé avec l'ID: " + id));
        } catch (BusinessException e) {
            log.error("Erreur business lors de la génération: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    @GetMapping("/rapports/{id}/telecharger")
    public ResponseEntity<?> telechargerRapport(@PathVariable Long id) {
        try {
            log.info("Téléchargement du rapport ID: {}", id);

            // Récupérer les informations du rapport pour le nom de fichier
            ResponseRapportDTO rapport = rapportService.getRapportById(id);
            byte[] contenu = rapportService.telechargerRapport(id);

            // Déterminer le type de contenu selon le format
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            String extension = ".txt";

            switch (rapport.getFormatFichier()) {
                case "PDF":
                    mediaType = MediaType.APPLICATION_PDF;
                    extension = ".pdf";
                    break;
                case "CSV":
                    mediaType = MediaType.parseMediaType("text/csv");
                    extension = ".csv";
                    break;
                case "JSON":
                    mediaType = MediaType.APPLICATION_JSON;
                    extension = ".json";
                    break;
            }

            String filename = rapport.getNomRapport().replaceAll("[^a-zA-Z0-9.-]", "_") + extension;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(contenu);

        } catch (EntityNotFoundException e) {
            log.warn("Tentative de téléchargement d'un rapport inexistant ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Rapport non trouvé avec l'ID: " + id));
        } catch (BusinessException e) {
            log.error("Erreur business lors du téléchargement: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors du téléchargement du rapport ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    // Endpoints de statistiques
    @GetMapping("/chercheurs/{idChercheur}/rapports/statistiques")
    public ResponseEntity<?> getStatistiquesRapportsChercheur(@PathVariable Long idChercheur) {
        try {
            log.debug("Statistiques des rapports pour le chercheur ID: {}", idChercheur);

            Long nombreTotal = rapportService.getNombreRapportsByChercheur(idChercheur);
            Long tailleTotal = rapportService.getTailleTotaleByChercheur(idChercheur);

            return ResponseEntity.ok(Map.of(
                    "chercheur_id", idChercheur,
                    "nombre_rapports", nombreTotal,
                    "taille_totale_bytes", tailleTotal,
                    "taille_totale_formatee", formatTaille(tailleTotal)
            ));
        } catch (Exception e) {
            log.error("Erreur lors du calcul des statistiques pour le chercheur ID: {}", idChercheur, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur"));
        }
    }

    // Méthode utilitaire pour formater la taille
    private String formatTaille(Long taille) {
        if (taille == null || taille == 0) return "0 KB";

        if (taille < 1024) {
            return taille + " B";
        } else if (taille < 1024 * 1024) {
            return String.format("%.1f KB", taille / 1024.0);
        } else {
            return String.format("%.1f MB", taille / (1024.0 * 1024.0));
        }
    }
}
