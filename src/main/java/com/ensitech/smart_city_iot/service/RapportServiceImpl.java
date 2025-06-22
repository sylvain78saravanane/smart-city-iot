package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.rapportDTO.CreateRapportDTO;
import com.ensitech.smart_city_iot.dto.rapportDTO.ResponseRapportDTO;
import com.ensitech.smart_city_iot.dto.rapportDTO.UpdateRapportDTO;
import com.ensitech.smart_city_iot.entity.Chercheur;
import com.ensitech.smart_city_iot.entity.DonneeIoT;
import com.ensitech.smart_city_iot.entity.Rapport;
import com.ensitech.smart_city_iot.entity.Utilisateur;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.repository.DonneeIoTRepository;
import com.ensitech.smart_city_iot.repository.RapportRepository;
import com.ensitech.smart_city_iot.repository.UtilisateurRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class RapportServiceImpl implements RapportService{

    @Autowired
    private RapportRepository rapportRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private DonneeIoTRepository donneeIoTRepository;

    @Override
    public ResponseRapportDTO createRapport(CreateRapportDTO dto) throws Exception {
        log.info("Création d'un nouveau rapport: {}", dto.getNomRapport());

        // Vérification que l'utilisateur existe et est bien un chercheur
        Utilisateur utilisateur = utilisateurRepository.findById(dto.getIdChercheur())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + dto.getIdChercheur()));

        if (!(utilisateur instanceof Chercheur)) {
            throw new BusinessException("Seuls les chercheurs peuvent créer des rapports");
        }

        Chercheur chercheur = (Chercheur) utilisateur;

        // Vérification que le chercheur est actif
        if (!chercheur.isActif()) {
            throw new BusinessException("Votre compte n'est pas actif");
        }

        // Validation des données
        validateRapportData(dto, chercheur);

        // Création du rapport
        Rapport rapport = buildRapportFromDTO(dto, chercheur);
        rapport = rapportRepository.save(rapport);

        log.info("Rapport créé avec succès: ID {}", rapport.getIdRapport());

        // Génération automatique du contenu immédiatement
        try {
            genererContenuRapport(rapport.getIdRapport());
            log.info("Contenu généré automatiquement pour le rapport ID: {}", rapport.getIdRapport());
        } catch (Exception e) {
            log.error("Erreur lors de la génération automatique du contenu: {}", e.getMessage());
            // On met à jour le statut en erreur mais on ne fait pas échouer la création
            rapport.setStatut("ERREUR");
            rapportRepository.save(rapport);
        }

        // Recharger le rapport avec le contenu généré
        rapport = rapportRepository.findById(rapport.getIdRapport()).orElse(rapport);

        return ResponseRapportDTO.fromEntity(rapport);
    }

    @Override
    public ResponseRapportDTO getRapportById(Long id) throws Exception {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé avec l'ID: " + id));

        return ResponseRapportDTO.fromEntity(rapport);
    }

    @Override
    public List<ResponseRapportDTO> getAllRapports() throws Exception {
        List<Rapport> rapports = rapportRepository.findAll();
        return rapports.stream()
                .map(ResponseRapportDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ResponseRapportDTO> getAllRapports(Pageable pageable) throws Exception {
        Page<Rapport> rapports = rapportRepository.findAll(pageable);
        return rapports.map(ResponseRapportDTO::fromEntity);
    }

    @Override
    public List<ResponseRapportDTO> getRapportsByChercheur(Long idChercheur) throws Exception {
        List<Rapport> rapports = rapportRepository.findByChercheurIdUtilisateur(idChercheur);
        return rapports.stream()
                .map(ResponseRapportDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseRapportDTO> getRapportsByStatut(String statut) throws Exception {
        List<Rapport> rapports = rapportRepository.findByStatut(statut);
        return rapports.stream()
                .map(ResponseRapportDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseRapportDTO> getRapportsByType(String typeRapport) throws Exception {
        List<Rapport> rapports = rapportRepository.findByTypeRapport(typeRapport);
        return rapports.stream()
                .map(ResponseRapportDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseRapportDTO updateRapport(Long id, UpdateRapportDTO dto) throws Exception {
        log.info("Mise à jour du rapport ID: {}", id);

        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé avec l'ID: " + id));

        // Mise à jour des champs
        updateRapportFields(rapport, dto);
        rapport = rapportRepository.save(rapport);

        log.info("Rapport mis à jour avec succès: ID {}", id);
        return ResponseRapportDTO.fromEntity(rapport);
    }

    @Override
    public void deleteRapport(Long id) throws Exception {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé avec l'ID: " + id));

        rapportRepository.delete(rapport);
        log.info("Rapport supprimé: ID {}", id);
    }

    @Override
    public void genererContenuRapport(Long id) throws Exception {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé avec l'ID: " + id));

        log.info("Génération du contenu pour le rapport: {}", rapport.getNomRapport());

        try {
            // Récupération des données IoT pour la période spécifiée
            List<DonneeIoT> donnees = donneeIoTRepository.findByTimestampCollecteBetween(
                    rapport.getPeriodeDebut(), rapport.getPeriodeFin());

            log.info("Nombre de données IoT trouvées pour la période {}-{}: {}",
                    rapport.getPeriodeDebut(), rapport.getPeriodeFin(), donnees.size());

            // Génération du contenu selon le format demandé
            String contenu;
            if ("CSV".equals(rapport.getFormatFichier())) {
                contenu = genererContenuCSV(rapport, donnees);
            } else if ("JSON".equals(rapport.getFormatFichier())) {
                contenu = genererContenuJSON(rapport, donnees);
            } else {
                contenu = genererContenuTexte(rapport, donnees);
            }

            // Mise à jour du rapport
            rapport.setContenu(contenu);
            rapport.setNombreDonnees((long) donnees.size());
            rapport.setTailleFichier((long) contenu.getBytes().length);
            rapport.setStatut("TERMINE");

            rapportRepository.save(rapport);

            log.info("Contenu généré avec succès pour le rapport ID: {} - {} données analysées",
                    id, donnees.size());

        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport ID: {}", id, e);
            rapport.setStatut("ERREUR");
            rapportRepository.save(rapport);
            throw e;
        }
    }

    @Override
    public byte[] telechargerRapport(Long id) throws Exception {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapport non trouvé avec l'ID: " + id));

        if (!rapport.isTermine()) {
            throw new BusinessException("Le rapport n'est pas encore prêt pour le téléchargement");
        }

        if (rapport.getContenu() == null) {
            throw new BusinessException("Aucun contenu disponible pour ce rapport");
        }

        // Retourner le contenu en bytes
        return rapport.getContenu().getBytes("UTF-8");
    }

    @Override
    public List<ResponseRapportDTO> rechercherRapports(String texte) throws Exception {
        List<Rapport> rapports = rapportRepository.findByNomOrDescriptionContaining(texte);
        return rapports.stream()
                .map(ResponseRapportDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseRapportDTO> getRapportsRecents(int jours) throws Exception {
        LocalDateTime dateDebut = LocalDateTime.now().minusDays(jours);
        List<Rapport> rapports = rapportRepository.findRecentRapports(dateDebut);
        return rapports.stream()
                .map(ResponseRapportDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseRapportDTO> getRapportsByPeriodeAnalyse(LocalDateTime debut, LocalDateTime fin) throws Exception {
        List<Rapport> rapports = rapportRepository.findByPeriodeAnalyse(debut, fin);
        return rapports.stream()
                .map(ResponseRapportDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Long getNombreRapportsByChercheur(Long idChercheur) throws Exception {
        return rapportRepository.countByChercheur(idChercheur);
    }

    @Override
    public Long getTailleTotaleByChercheur(Long idChercheur) throws Exception {
        Long taille = rapportRepository.getTailleTotaleByChercheur(idChercheur);
        return taille != null ? taille : 0L;
    }

    // Méthodes privées pour la génération de contenu

    private String genererContenuCSV(Rapport rapport, List<DonneeIoT> donnees) {
        StringBuilder csv = new StringBuilder();

        // En-tête CSV
        csv.append("ID_Donnee,Date_Collecte,Ville,Region,Pays,Latitude,Longitude,")
                .append("Temperature_Celsius,Temperature_Fahrenheit,Humidite,Vitesse_Vent_KPH,Precipitation_MM,")
                .append("CO,NO2,O3,SO2,PM10,Indice_UV,Nuageux,Source_API,ID_Capteur,Nom_Capteur\n");

        // Données
        for (DonneeIoT donnee : donnees) {
            csv.append(donnee.getIdDonneeIoT()).append(",")
                    .append(donnee.getTimestampCollecte()).append(",")
                    .append(escapeCsv(donnee.getVilleNom())).append(",")
                    .append(escapeCsv(donnee.getRegion())).append(",")
                    .append(escapeCsv(donnee.getPays())).append(",")
                    .append(donnee.getLatitude()).append(",")
                    .append(donnee.getLongitude()).append(",")
                    .append(donnee.getTemperatureCelsius()).append(",")
                    .append(donnee.getTemperatureFahrenheit()).append(",")
                    .append(donnee.getHumidite()).append(",")
                    .append(donnee.getVitesseVentKph()).append(",")
                    .append(donnee.getPrecipitationMm()).append(",")
                    .append(donnee.getCo()).append(",")
                    .append(donnee.getNo2()).append(",")
                    .append(donnee.getO3()).append(",")
                    .append(donnee.getSo2()).append(",")
                    .append(donnee.getPm10()).append(",")
                    .append(donnee.getIndiceUv()).append(",")
                    .append(donnee.getNuageux()).append(",")
                    .append(escapeCsv(donnee.getSourceApi())).append(",")
                    .append(donnee.getCapteur() != null ? donnee.getCapteur().getIdCapteur() : "").append(",")
                    .append(donnee.getCapteur() != null ? escapeCsv(donnee.getCapteur().getNomCapteur()) : "").append("\n");
        }

        return csv.toString();
    }

    private String genererContenuJSON(Rapport rapport, List<DonneeIoT> donnees) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            Map<String, Object> jsonRapport = new HashMap<>();

            // Informations du rapport
            jsonRapport.put("rapport_info", Map.of(
                    "id", rapport.getIdRapport(),
                    "nom", rapport.getNomRapport(),
                    "description", rapport.getDescription() != null ? rapport.getDescription() : "",
                    "type", rapport.getTypeRapport(),
                    "periode_debut", rapport.getPeriodeDebut(),
                    "periode_fin", rapport.getPeriodeFin(),
                    "chercheur", rapport.getChercheur().getNomComplet(),
                    "institut", rapport.getChercheur().getInstitut() != null ? rapport.getChercheur().getInstitut() : "",
                    "date_generation", rapport.getDateCreation()
            ));

            // Statistiques générales
            jsonRapport.put("statistiques_generales", Map.of(
                    "nombre_total_donnees", donnees.size(),
                    "nombre_villes", donnees.stream().map(DonneeIoT::getVilleNom).distinct().count(),
                    "nombre_capteurs", donnees.stream().map(d -> d.getCapteur().getIdCapteur()).distinct().count(),
                    "premiere_donnee", donnees.isEmpty() ? null :
                            donnees.stream().map(DonneeIoT::getTimestampCollecte).min(LocalDateTime::compareTo),
                    "derniere_donnee", donnees.isEmpty() ? null :
                            donnees.stream().map(DonneeIoT::getTimestampCollecte).max(LocalDateTime::compareTo)
            ));

            // Statistiques détaillées
            jsonRapport.put("statistiques_detaillees", genererStatistiquesJSON(donnees));

            // Échantillon de données (limité à 1000 pour éviter des fichiers trop volumineux)
            jsonRapport.put("donnees_echantillon", donnees.stream()
                    .limit(1000)
                    .map(this::convertDonneeToMap)
                    .toList());

            jsonRapport.put("note", donnees.size() > 1000 ?
                    "Échantillon de 1000 données sur " + donnees.size() + " au total" :
                    "Toutes les données de la période");

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonRapport);

        } catch (Exception e) {
            log.error("Erreur lors de la génération JSON: {}", e.getMessage());
            return "{ \"erreur\": \"Impossible de générer le contenu JSON: " + e.getMessage() + "\" }";
        }
    }

    private String genererContenuTexte(Rapport rapport, List<DonneeIoT> donnees) {
        StringBuilder contenu = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // En-tête du rapport
        contenu.append("=== RAPPORT D'ANALYSE IoT ===\n\n");
        contenu.append("Nom du rapport: ").append(rapport.getNomRapport()).append("\n");
        contenu.append("Type: ").append(rapport.getTypeRapport()).append("\n");
        contenu.append("Période d'analyse: du ").append(rapport.getPeriodeDebut().format(formatter))
                .append(" au ").append(rapport.getPeriodeFin().format(formatter)).append("\n");
        contenu.append("Généré le: ").append(LocalDateTime.now().format(formatter)).append("\n");
        contenu.append("Chercheur: ").append(rapport.getChercheur().getNomComplet()).append("\n");
        if (rapport.getChercheur().getInstitut() != null) {
            contenu.append("Institut: ").append(rapport.getChercheur().getInstitut()).append("\n");
        }
        contenu.append("\n");

        // Statistiques générales
        contenu.append("=== STATISTIQUES GÉNÉRALES ===\n");
        contenu.append("Nombre total de données IoT analysées: ").append(donnees.size()).append("\n");

        if (donnees.isEmpty()) {
            contenu.append("\n⚠️  AUCUNE DONNÉE DISPONIBLE pour cette période.\n");
            contenu.append("Vérifiez que :\n");
            contenu.append("- Des capteurs étaient actifs pendant cette période\n");
            contenu.append("- La collecte de données fonctionnait correctement\n");
            contenu.append("- La période sélectionnée contient des données\n");
            return contenu.toString();
        }

        // Période effective des données
        LocalDateTime premiereDonnee = donnees.stream()
                .map(DonneeIoT::getTimestampCollecte)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime derniereDonnee = donnees.stream()
                .map(DonneeIoT::getTimestampCollecte)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        contenu.append("Première donnée: ").append(premiereDonnee != null ? premiereDonnee.format(formatter) : "N/A").append("\n");
        contenu.append("Dernière donnée: ").append(derniereDonnee != null ? derniereDonnee.format(formatter) : "N/A").append("\n");

        // Nombre de villes/capteurs uniques
        long nombreVilles = donnees.stream().map(DonneeIoT::getVilleNom).distinct().count();
        long nombreCapteurs = donnees.stream().map(d -> d.getCapteur().getIdCapteur()).distinct().count();

        contenu.append("Nombre de villes: ").append(nombreVilles).append("\n");
        contenu.append("Nombre de capteurs: ").append(nombreCapteurs).append("\n\n");

        // Analyse selon le type de rapport
        switch (rapport.getTypeRapport()) {
            case "TEMPERATURE":
                genererAnalyseTemperature(contenu, donnees);
                break;
            case "POLLUTION":
                genererAnalysePollution(contenu, donnees);
                break;
            case "GLOBAL":
                genererAnalyseGlobale(contenu, donnees);
                break;
            default:
                genererAnalyseBasique(contenu, donnees);
        }

        // Échantillon de données brutes
        contenu.append("=== ÉCHANTILLON DE DONNÉES (15 premières) ===\n");
        donnees.stream().limit(15).forEach(donnee -> {
            contenu.append("📊 ").append(donnee.getTimestampCollecte().format(formatter))
                    .append(" | ").append(donnee.getVilleNom())
                    .append(" | Temp: ").append(donnee.getTemperatureCelsius()).append("°C")
                    .append(" | Humidité: ").append(donnee.getHumidite()).append("%")
                    .append(" | PM10: ").append(donnee.getPm10())
                    .append(" | Capteur: ").append(donnee.getCapteur().getNomCapteur()).append("\n");
        });

        contenu.append("\n=== FIN DU RAPPORT ===\n");

        return contenu.toString();
    }

    private Map<String, Object> genererStatistiquesJSON(List<DonneeIoT> donnees) {
        Map<String, Object> stats = new HashMap<>();

        if (!donnees.isEmpty()) {
            // Statistiques température
            OptionalDouble tempMoyenne = donnees.stream()
                    .filter(d -> d.getTemperatureCelsius() != null)
                    .mapToDouble(DonneeIoT::getTemperatureCelsius)
                    .average();

            stats.put("temperature", Map.of(
                    "moyenne", Math.round(tempMoyenne.orElse(0.0) * 100.0) / 100.0,
                    "min", donnees.stream().filter(d -> d.getTemperatureCelsius() != null)
                            .mapToDouble(DonneeIoT::getTemperatureCelsius).min().orElse(0.0),
                    "max", donnees.stream().filter(d -> d.getTemperatureCelsius() != null)
                            .mapToDouble(DonneeIoT::getTemperatureCelsius).max().orElse(0.0)
            ));

            // Statistiques pollution
            OptionalDouble pm10Moyenne = donnees.stream()
                    .filter(d -> d.getPm10() != null)
                    .mapToDouble(DonneeIoT::getPm10)
                    .average();

            OptionalDouble coMoyenne = donnees.stream()
                    .filter(d -> d.getCo() != null)
                    .mapToDouble(DonneeIoT::getCo)
                    .average();

            stats.put("pollution", Map.of(
                    "pm10_moyenne", Math.round(pm10Moyenne.orElse(0.0) * 100.0) / 100.0,
                    "co_moyenne", Math.round(coMoyenne.orElse(0.0) * 100.0) / 100.0,
                    "no2_moyenne", donnees.stream().filter(d -> d.getNo2() != null)
                            .mapToDouble(DonneeIoT::getNo2).average().orElse(0.0),
                    "o3_moyenne", donnees.stream().filter(d -> d.getO3() != null)
                            .mapToDouble(DonneeIoT::getO3).average().orElse(0.0)
            ));

            // Statistiques météo
            OptionalDouble humiditeMoyenne = donnees.stream()
                    .filter(d -> d.getHumidite() != null)
                    .mapToDouble(DonneeIoT::getHumidite)
                    .average();

            stats.put("meteo", Map.of(
                    "humidite_moyenne", Math.round(humiditeMoyenne.orElse(0.0) * 100.0) / 100.0,
                    "vitesse_vent_moyenne", donnees.stream().filter(d -> d.getVitesseVentKph() != null)
                            .mapToDouble(DonneeIoT::getVitesseVentKph).average().orElse(0.0),
                    "precipitation_totale", donnees.stream().filter(d -> d.getPrecipitationMm() != null)
                            .mapToDouble(DonneeIoT::getPrecipitationMm).sum()
            ));
        }

        return stats;
    }

    private Map<String, Object> convertDonneeToMap(DonneeIoT donnee) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", donnee.getIdDonneeIoT());
        map.put("timestamp", donnee.getTimestampCollecte());
        map.put("ville", donnee.getVilleNom());
        map.put("region", donnee.getRegion());
        map.put("pays", donnee.getPays());
        map.put("latitude", donnee.getLatitude());
        map.put("longitude", donnee.getLongitude());
        map.put("temperature_c", donnee.getTemperatureCelsius());
        map.put("temperature_f", donnee.getTemperatureFahrenheit());
        map.put("humidite", donnee.getHumidite());
        map.put("vitesse_vent", donnee.getVitesseVentKph());
        map.put("precipitation", donnee.getPrecipitationMm());
        map.put("pm10", donnee.getPm10());
        map.put("co", donnee.getCo());
        map.put("no2", donnee.getNo2());
        map.put("o3", donnee.getO3());
        map.put("so2", donnee.getSo2());
        map.put("indice_uv", donnee.getIndiceUv());
        map.put("capteur_id", donnee.getCapteur() != null ? donnee.getCapteur().getIdCapteur() : null);
        map.put("capteur_nom", donnee.getCapteur() != null ? donnee.getCapteur().getNomCapteur() : null);
        return map;
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void genererAnalyseTemperature(StringBuilder contenu, List<DonneeIoT> donnees) {
        contenu.append("=== ANALYSE DE TEMPÉRATURE ===\n");

        double tempMoyenne = donnees.stream()
                .filter(d -> d.getTemperatureCelsius() != null)
                .mapToDouble(DonneeIoT::getTemperatureCelsius)
                .average()
                .orElse(0.0);

        double tempMin = donnees.stream()
                .filter(d -> d.getTemperatureCelsius() != null)
                .mapToDouble(DonneeIoT::getTemperatureCelsius)
                .min()
                .orElse(0.0);

        double tempMax = donnees.stream()
                .filter(d -> d.getTemperatureCelsius() != null)
                .mapToDouble(DonneeIoT::getTemperatureCelsius)
                .max()
                .orElse(0.0);

        contenu.append("Température moyenne: ").append(String.format("%.2f", tempMoyenne)).append("°C\n");
        contenu.append("Température minimale: ").append(String.format("%.2f", tempMin)).append("°C\n");
        contenu.append("Température maximale: ").append(String.format("%.2f", tempMax)).append("°C\n");
        contenu.append("Écart de température: ").append(String.format("%.2f", tempMax - tempMin)).append("°C\n\n");

        // Analyse par ville
        Map<String, Double> tempParVille = donnees.stream()
                .filter(d -> d.getTemperatureCelsius() != null && d.getVilleNom() != null)
                .collect(Collectors.groupingBy(
                        DonneeIoT::getVilleNom,
                        Collectors.averagingDouble(DonneeIoT::getTemperatureCelsius)
                ));

        if (!tempParVille.isEmpty()) {
            contenu.append("Température moyenne par ville:\n");
            tempParVille.forEach((ville, temp) ->
                    contenu.append("- ").append(ville).append(": ").append(String.format("%.2f", temp)).append("°C\n"));
            contenu.append("\n");
        }
    }

    private void genererAnalysePollution(StringBuilder contenu, List<DonneeIoT> donnees) {
        contenu.append("=== ANALYSE DE POLLUTION ===\n");

        double coMoyen = donnees.stream()
                .filter(d -> d.getCo() != null)
                .mapToDouble(DonneeIoT::getCo)
                .average()
                .orElse(0.0);

        double pm10Moyen = donnees.stream()
                .filter(d -> d.getPm10() != null)
                .mapToDouble(DonneeIoT::getPm10)
                .average()
                .orElse(0.0);

        double no2Moyen = donnees.stream()
                .filter(d -> d.getNo2() != null)
                .mapToDouble(DonneeIoT::getNo2)
                .average()
                .orElse(0.0);

        contenu.append("CO moyen: ").append(String.format("%.2f", coMoyen)).append(" µg/m³\n");
        contenu.append("PM10 moyen: ").append(String.format("%.2f", pm10Moyen)).append(" µg/m³\n");
        contenu.append("NO2 moyen: ").append(String.format("%.2f", no2Moyen)).append(" µg/m³\n\n");

        // Évaluation de la qualité de l'air
        String qualiteAir;
        if (pm10Moyen <= 20) {
            qualiteAir = "Bonne";
        } else if (pm10Moyen <= 40) {
            qualiteAir = "Moyenne";
        } else if (pm10Moyen <= 50) {
            qualiteAir = "Dégradée";
        } else if (pm10Moyen <= 100) {
            qualiteAir = "Mauvaise";
        } else {
            qualiteAir = "Très mauvaise";
        }

        contenu.append("Qualité de l'air globale: ").append(qualiteAir).append("\n\n");

        // Analyse par ville pour la pollution
        Map<String, Double> pm10ParVille = donnees.stream()
                .filter(d -> d.getPm10() != null && d.getVilleNom() != null)
                .collect(Collectors.groupingBy(
                        DonneeIoT::getVilleNom,
                        Collectors.averagingDouble(DonneeIoT::getPm10)
                ));

        if (!pm10ParVille.isEmpty()) {
            contenu.append("PM10 moyen par ville:\n");
            pm10ParVille.forEach((ville, pm10) ->
                    contenu.append("- ").append(ville).append(": ").append(String.format("%.2f", pm10)).append(" µg/m³\n"));
            contenu.append("\n");
        }
    }

    private void genererAnalyseGlobale(StringBuilder contenu, List<DonneeIoT> donnees) {
        // Inclure toutes les analyses
        genererAnalyseTemperature(contenu, donnees);
        genererAnalysePollution(contenu, donnees);

        contenu.append("=== ANALYSE MÉTÉOROLOGIQUE ===\n");

        double humiditeMoyenne = donnees.stream()
                .filter(d -> d.getHumidite() != null)
                .mapToDouble(DonneeIoT::getHumidite)
                .average()
                .orElse(0.0);

        double ventMoyen = donnees.stream()
                .filter(d -> d.getVitesseVentKph() != null)
                .mapToDouble(DonneeIoT::getVitesseVentKph)
                .average()
                .orElse(0.0);

        double precipitationTotale = donnees.stream()
                .filter(d -> d.getPrecipitationMm() != null)
                .mapToDouble(DonneeIoT::getPrecipitationMm)
                .sum();

        double indiceUvMoyen = donnees.stream()
                .filter(d -> d.getIndiceUv() != null)
                .mapToDouble(DonneeIoT::getIndiceUv)
                .average()
                .orElse(0.0);

        contenu.append("Humidité moyenne: ").append(String.format("%.1f", humiditeMoyenne)).append("%\n");
        contenu.append("Vitesse du vent moyenne: ").append(String.format("%.1f", ventMoyen)).append(" km/h\n");
        contenu.append("Précipitations totales: ").append(String.format("%.1f", precipitationTotale)).append(" mm\n");
        contenu.append("Indice UV moyen: ").append(String.format("%.1f", indiceUvMoyen)).append("\n\n");

        // Analyse des conditions météo par ville
        contenu.append("=== SYNTHÈSE PAR VILLE ===\n");
        Map<String, List<DonneeIoT>> donneesParVille = donnees.stream()
                .filter(d -> d.getVilleNom() != null)
                .collect(Collectors.groupingBy(DonneeIoT::getVilleNom));

        donneesParVille.forEach((ville, donneesVille) -> {
            double tempMoyVille = donneesVille.stream()
                    .filter(d -> d.getTemperatureCelsius() != null)
                    .mapToDouble(DonneeIoT::getTemperatureCelsius)
                    .average().orElse(0.0);

            double pm10MoyVille = donneesVille.stream()
                    .filter(d -> d.getPm10() != null)
                    .mapToDouble(DonneeIoT::getPm10)
                    .average().orElse(0.0);

            contenu.append("🏙️ ").append(ville).append(": ")
                    .append("Temp: ").append(String.format("%.1f", tempMoyVille)).append("°C, ")
                    .append("PM10: ").append(String.format("%.1f", pm10MoyVille)).append(" µg/m³")
                    .append(" (").append(donneesVille.size()).append(" mesures)\n");
        });
        contenu.append("\n");
    }

    private void genererAnalyseBasique(StringBuilder contenu, List<DonneeIoT> donnees) {
        contenu.append("=== ANALYSE BASIQUE ===\n");
        contenu.append("Données collectées et analysées avec succès.\n");
        contenu.append("Type de rapport: Analyse générale des données IoT\n\n");

        // Répartition par source
        Map<String, Long> repartitionSource = donnees.stream()
                .filter(d -> d.getSourceApi() != null)
                .collect(Collectors.groupingBy(
                        DonneeIoT::getSourceApi,
                        Collectors.counting()
                ));

        if (!repartitionSource.isEmpty()) {
            contenu.append("Répartition par source de données:\n");
            repartitionSource.forEach((source, count) ->
                    contenu.append("- ").append(source).append(": ").append(count).append(" mesures\n"));
            contenu.append("\n");
        }

        // Répartition par capteur
        Map<String, Long> repartitionCapteur = donnees.stream()
                .filter(d -> d.getCapteur() != null && d.getCapteur().getNomCapteur() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getCapteur().getNomCapteur(),
                        Collectors.counting()
                ));

        if (!repartitionCapteur.isEmpty()) {
            contenu.append("Répartition par capteur:\n");
            repartitionCapteur.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(entry ->
                            contenu.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" mesures\n"));
            contenu.append("\n");
        }
    }

    // Méthodes privées de validation et construction

    private void validateRapportData(CreateRapportDTO dto, Chercheur chercheur) {
        // Vérification de la période
        if (!dto.isPeriodeValide()) {
            throw new BusinessException("La période de début doit être antérieure à la période de fin");
        }

        // Vérification que la période n'est pas dans le futur
        if (dto.getPeriodeDebut().isAfter(LocalDateTime.now()) ||
                dto.getPeriodeFin().isAfter(LocalDateTime.now())) {
            throw new BusinessException("La période d'analyse ne peut pas être dans le futur");
        }

        // Vérification que la période n'est pas trop ancienne (plus de 2 ans)
        if (dto.getPeriodeDebut().isBefore(LocalDateTime.now().minusYears(2))) {
            log.warn("Période très ancienne demandée: {}", dto.getPeriodeDebut());
        }

        // Vérification d'unicité du nom pour ce chercheur
        if (rapportRepository.existsByNomRapportAndChercheur(dto.getNomRapport(), chercheur)) {
            throw new BusinessException("Un rapport avec ce nom existe déjà pour ce chercheur");
        }

        // Vérification qu'il n'y a pas déjà un rapport en cours avec le même nom
        if (rapportRepository.existsRapportEnCoursByNomAndChercheur(dto.getNomRapport(), chercheur.getIdUtilisateur())) {
            throw new BusinessException("Un rapport avec ce nom est déjà en cours de génération");
        }

        // Vérification de la taille de la période (max 1 an)
        if (dto.getPeriodeDebut().plusYears(1).isBefore(dto.getPeriodeFin())) {
            throw new BusinessException("La période d'analyse ne peut pas dépasser 1 an");
        }
    }

    private Rapport buildRapportFromDTO(CreateRapportDTO dto, Chercheur chercheur) {
        return Rapport.builder()
                .nomRapport(dto.getNomRapport())
                .description(dto.getDescription())
                .periodeDebut(dto.getPeriodeDebut())
                .periodeFin(dto.getPeriodeFin())
                .typeRapport(dto.getTypeRapport())
                .formatFichier(dto.getFormatFichier())
                .statut("EN_COURS")
                .chercheur(chercheur)
                .build();
    }

    private void updateRapportFields(Rapport rapport, UpdateRapportDTO dto) {
        if (dto.getNomRapport() != null && !dto.getNomRapport().trim().isEmpty()) {
            rapport.setNomRapport(dto.getNomRapport().trim());
        }
        if (dto.getDescription() != null) {
            rapport.setDescription(dto.getDescription().trim());
        }
        if (dto.getStatut() != null) {
            rapport.setStatut(dto.getStatut());
        }
        if (dto.getContenu() != null) {
            rapport.setContenu(dto.getContenu());
        }
        if (dto.getTailleFichier() != null) {
            rapport.setTailleFichier(dto.getTailleFichier());
        }
        if (dto.getCheminFichier() != null) {
            rapport.setCheminFichier(dto.getCheminFichier().trim());
        }
        if (dto.getNombreDonnees() != null) {
            rapport.setNombreDonnees(dto.getNombreDonnees());
        }
    }

}
