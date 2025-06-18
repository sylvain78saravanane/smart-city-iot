package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.capteurDTO.CreateCapteurDTO;
import com.ensitech.smart_city_iot.dto.capteurDTO.ResponseCapteurDTO;
import com.ensitech.smart_city_iot.entity.Administrateur;
import com.ensitech.smart_city_iot.entity.Capteur;
import com.ensitech.smart_city_iot.entity.GestionnaireDeVille;
import com.ensitech.smart_city_iot.entity.Utilisateur;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.repository.CapteurRepository;
import com.ensitech.smart_city_iot.repository.UtilisateurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CapteurServiceImpl implements CapteurService{

    @Autowired
    private CapteurRepository capteurRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public ResponseCapteurDTO createCapteur(CreateCapteurDTO dto) throws Exception {
        log.info("Création d'un nouveau capteur: {}", dto.getNomCapteur());

        // Validation métier
        validateCapteurData(dto);

        // Vérification du gestionnaire responsable
        Utilisateur gestionnaire = utilisateurRepository.findById(dto.getIdGestionnaireResponsable())
                .orElseThrow(() -> new EntityNotFoundException("Gestionnaire non trouvé avec l'ID: " + dto.getIdGestionnaireResponsable()));

        // Vérification des permissions
        if (!gestionnaire.peutCreerCapteur()) {
            throw new BusinessException("Ce type d'utilisateur ne peut pas créer de capteurs");
        }

        // Vérification du type de gestionnaire
        validateGestionnaireType(gestionnaire, dto.getTypeGestionnaire());

        // Création du capteur
        Capteur capteur = buildCapteurFromDTO(dto, gestionnaire);

        capteur = capteurRepository.save(capteur);

        log.info("Capteur créé avec succès: ID {}", capteur.getIdCapteur());
        return ResponseCapteurDTO.fromEntity(capteur);
    }

    @Override
    public ResponseCapteurDTO getCapteurById(Long id) throws Exception {
        Capteur capteur = capteurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Capteur non trouvé avec l'ID: " + id));

        return ResponseCapteurDTO.fromEntity(capteur);
    }

    @Override
    public List<ResponseCapteurDTO> getCapteursByGestionnaire(Long idGestionnaire) throws Exception {
        List<Capteur> capteurs = capteurRepository.findByGestionnaireResponsable(idGestionnaire);

        return capteurs.stream()
                .map(ResponseCapteurDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseCapteurDTO> getAllCapteurs() throws Exception {
        List<Capteur> capteurs = capteurRepository.findAll();

        return capteurs.stream()
                .map(ResponseCapteurDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCapteur(Long id) throws Exception {
        Capteur capteur = capteurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Capteur non trouvé avec l'ID: " + id));

        capteurRepository.delete(capteur);
        log.info("Capteur supprimé: ID {}", id);
    }

    // Méthodes privées
    private void validateCapteurData(CreateCapteurDTO dto) {
        // Vérification du numéro de série unique
        if (dto.getNumeroSerie() != null && capteurRepository.existsByNumeroSerie(dto.getNumeroSerie())) {
            throw new BusinessException("Un capteur avec ce numéro de série existe déjà");
        }

        // Validation des valeurs min/max
        if (!dto.isValeurMinMaxValid()) {
            throw new BusinessException("La valeur minimale doit être inférieure ou égale à la valeur maximale");
        }

        // Validation des coordonnées GPS
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            if (dto.getLatitude() < -90 || dto.getLatitude() > 90) {
                throw new BusinessException("Latitude invalide");
            }
            if (dto.getLongitude() < -180 || dto.getLongitude() > 180) {
                throw new BusinessException("Longitude invalide");
            }
        }
    }

    private void validateGestionnaireType(Utilisateur gestionnaire, String typeAttendu) {
        String typeReel = gestionnaire.getRole();

        if (!typeReel.equals(typeAttendu)) {
            throw new BusinessException("Type de gestionnaire incorrect. Attendu: " + typeAttendu + ", Réel: " + typeReel);
        }
    }

    private Capteur buildCapteurFromDTO(CreateCapteurDTO dto, Utilisateur gestionnaire) {
        Capteur capteur = Capteur.builder()
                .nomCapteur(dto.getNomCapteur())
                .typeCapteur(dto.getTypeCapteur())
                .description(dto.getDescription())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .adresseInstallation(dto.getAdresseInstallation())
                .statut(dto.getStatut())
                .dateInstallation(dto.getDateInstallation() != null ? dto.getDateInstallation() : LocalDateTime.now())
                .frequenceMesure(dto.getFrequenceMesure())
                .uniteMesure(dto.getUniteMesure())
                .valeurMin(dto.getValeurMin())
                .valeurMax(dto.getValeurMax())
                .numeroSerie(dto.getNumeroSerie())
                .modele(dto.getModele())
                .fabricant(dto.getFabricant())
                .creePar(gestionnaire.getNomComplet())
                .build();

        // Association avec le bon type de gestionnaire
        if (gestionnaire instanceof Administrateur) {
            capteur.setAdministrateur((Administrateur) gestionnaire);
        } else if (gestionnaire instanceof GestionnaireDeVille) {
            capteur.setGestionnaireDeVille((GestionnaireDeVille) gestionnaire);
        }

        return capteur;
    }
}
