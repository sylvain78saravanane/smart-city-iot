package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.alerteDTO.CreateAlerteDTO;
import com.ensitech.smart_city_iot.dto.alerteDTO.ResponseAlerteDTO;
import com.ensitech.smart_city_iot.dto.alerteDTO.UpdateAlerteDTO;
import com.ensitech.smart_city_iot.entity.Alerte;
import com.ensitech.smart_city_iot.entity.Capteur;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.repository.AlerteRepository;
import com.ensitech.smart_city_iot.repository.CapteurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AlerteServiceImpl implements AlerteService{
    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private CapteurRepository capteurRepository;

    @Override
    public ResponseAlerteDTO createAlerte(CreateAlerteDTO dto) throws Exception {
        log.info("Création d'une nouvelle alerte: {}", dto.getTitre());

        // Vérification que le capteur existe
        Capteur capteur = capteurRepository.findById(dto.getIdCapteur())
                .orElseThrow(() -> new EntityNotFoundException("Capteur non trouvé avec l'ID: " + dto.getIdCapteur()));

        // Vérification que le capteur est actif
        if (!capteur.isActif()) {
            throw new BusinessException("Impossible de créer une alerte sur un capteur inactif");
        }

        // Création de l'alerte
        Alerte alerte = Alerte.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .seuilValeur(dto.getSeuilValeur())
                .typeCondition(dto.getTypeCondition())
                .priorite(dto.getPriorite())
                .active(dto.getActive())
                .capteur(capteur)
                .build();

        alerte = alerteRepository.save(alerte);

        log.info("Alerte créée avec succès: ID {}", alerte.getIdAlerte());
        return ResponseAlerteDTO.fromEntity(alerte);
    }

    @Override
    public ResponseAlerteDTO getAlerteById(Long id) throws Exception {
        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée avec l'ID: " + id));

        return ResponseAlerteDTO.fromEntity(alerte);
    }

    @Override
    public List<ResponseAlerteDTO> getAllAlertes() throws Exception {
        List<Alerte> alertes = alerteRepository.findAll();
        return alertes.stream()
                .map(ResponseAlerteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseAlerteDTO> getAlertesByCapteur(Long idCapteur) throws Exception {
        List<Alerte> alertes = alerteRepository.findByCapteurIdCapteur(idCapteur);
        return alertes.stream()
                .map(ResponseAlerteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseAlerteDTO> getAlertesActives() throws Exception {
        List<Alerte> alertes = alerteRepository.findByActive(true);
        return alertes.stream()
                .map(ResponseAlerteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseAlerteDTO updateAlerte(Long id, UpdateAlerteDTO dto) throws Exception {
        log.info("Mise à jour de l'alerte ID: {}", id);

        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée avec l'ID: " + id));

        // Mise à jour des champs
        if (dto.getTitre() != null) {
            alerte.setTitre(dto.getTitre());
        }
        if (dto.getDescription() != null) {
            alerte.setDescription(dto.getDescription());
        }
        if (dto.getSeuilValeur() != null) {
            alerte.setSeuilValeur(dto.getSeuilValeur());
        }
        if (dto.getTypeCondition() != null) {
            alerte.setTypeCondition(dto.getTypeCondition());
        }
        if (dto.getPriorite() != null) {
            alerte.setPriorite(dto.getPriorite());
        }
        if (dto.getActive() != null) {
            alerte.setActive(dto.getActive());
        }

        alerte = alerteRepository.save(alerte);

        log.info("Alerte mise à jour avec succès: ID {}", id);
        return ResponseAlerteDTO.fromEntity(alerte);
    }

    @Override
    public void deleteAlerte(Long id) throws Exception {
        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée avec l'ID: " + id));

        alerteRepository.delete(alerte);
        log.info("Alerte supprimée: ID {}", id);
    }

    @Override
    public void activerAlerte(Long id) throws Exception {
        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée avec l'ID: " + id));

        alerte.setActive(true);
        alerteRepository.save(alerte);
        log.info("Alerte activée: ID {}", id);
    }

    @Override
    public void desactiverAlerte(Long id) throws Exception {
        Alerte alerte = alerteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée avec l'ID: " + id));

        alerte.setActive(false);
        alerteRepository.save(alerte);
        log.info("Alerte désactivée: ID {}", id);
    }
}
