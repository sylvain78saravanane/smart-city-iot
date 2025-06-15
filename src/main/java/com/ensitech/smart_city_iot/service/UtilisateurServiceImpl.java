package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.utilisateurDTO.CreateUtilisateurDTO;
import com.ensitech.smart_city_iot.dto.utilisateurDTO.ResponseUtilisateurDTO;
import com.ensitech.smart_city_iot.dto.utilisateurDTO.UpdateUtilisateurDTO;
import com.ensitech.smart_city_iot.entity.*;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;



@Service
@Transactional
@Slf4j
public class UtilisateurServiceImpl implements UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public ResponseUtilisateurDTO createUtilisateur(CreateUtilisateurDTO dto) throws Exception {

        log.info("Création d'un nouvel utilisateur: {}", dto.getEmail());

        // Vérification email unique
        if (utilisateurRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Un utilisateur avec cet email existe déjà");
        }

        // Création selon le type
        Utilisateur utilisateur = createSpecificUser(dto);

        // Champs communs
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setMotDePasse(BCrypt.hashpw(dto.getMotDePasse(), BCrypt.gensalt()));
        utilisateur.setDateNaissance(dto.getDateNaissance());
        utilisateur.setTelephone(dto.getTelephone());
        utilisateur.setAdresse(dto.getAdresse());
        utilisateur.setCodePostal(dto.getCodePostal());
        utilisateur.setActif(true);
        utilisateur.setNotificationActive(true);

        utilisateur = utilisateurRepository.save(utilisateur);

        log.info("Utilisateur créé avec succès: ID {}", utilisateur.getIdUtilisateur());
        return ResponseUtilisateurDTO.fromEntity(utilisateur);
    }

    private Utilisateur createSpecificUser(CreateUtilisateurDTO dto) {
        return switch (dto.getTypeUtilisateur()) {
            case "ADMINISTRATEUR" -> new Administrateur();

            case "GESTIONNAIRE_VILLE" -> new GestionnaireDeVille();

            case "CHERCHEUR" -> new Chercheur();

            case "CITOYEN" -> new Citoyen();

            default -> throw new BusinessException("Type d'utilisateur non supporté: " + dto.getTypeUtilisateur());
        };
    }

    @Override
    public Utilisateur login(String email, String password) throws Exception {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur != null && BCrypt.checkpw(password, utilisateur.getMotDePasse())){
            return utilisateur;
        }
        return null;
    }

    @Override
    public UpdateUtilisateurDTO updateUtilisateur(Long id, UpdateUtilisateurDTO dto) throws Exception {
        return null;
    }

    @Override
    public void deleteUtilisateur(Long id) throws Exception {

    }

    @Override
    public void updateFields(Utilisateur utilisateur, UpdateUtilisateurDTO dto) throws Exception {

    }

    @Override
    public ResponseUtilisateurDTO getUtilisateurById(Long id) throws Exception {
        log.debug("ID Utilisateur : {}",id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        return ResponseUtilisateurDTO.fromEntity(utilisateur);
    }
}
