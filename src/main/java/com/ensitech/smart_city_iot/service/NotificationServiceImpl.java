package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.notificationDTO.CreateNotificationDTO;
import com.ensitech.smart_city_iot.dto.notificationDTO.ResponseNotificationDTO;
import com.ensitech.smart_city_iot.entity.Alerte;
import com.ensitech.smart_city_iot.entity.Notification;
import com.ensitech.smart_city_iot.entity.Utilisateur;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.repository.AlerteRepository;
import com.ensitech.smart_city_iot.repository.NotificationRepository;
import com.ensitech.smart_city_iot.repository.UtilisateurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AlerteRepository alerteRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public ResponseNotificationDTO createNotification(CreateNotificationDTO dto) throws Exception {
        log.info("Création d'une nouvelle notification: {}", dto.getTitre());

        // Vérification que l'alerte existe
        Alerte alerte = alerteRepository.findById(dto.getIdAlerte())
                .orElseThrow(() -> new EntityNotFoundException("Alerte non trouvée avec l'ID: " + dto.getIdAlerte()));

        // Vérification que l'alerte est active
        if (!alerte.isActive()) {
            throw new BusinessException("Impossible de créer une notification pour une alerte inactive");
        }

        // Vérification des utilisateurs
        List<Utilisateur> utilisateurs = new ArrayList<>();
        for (Long idUtilisateur : dto.getIdUtilisateurs()) {
            Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + idUtilisateur));

            if (!utilisateur.isActif()) {
                log.warn("Utilisateur inactif ignoré: {}", idUtilisateur);
                continue;
            }

            utilisateurs.add(utilisateur);
        }

        if (utilisateurs.isEmpty()) {
            throw new BusinessException("Aucun utilisateur actif spécifié pour la notification");
        }

        // Création de la notification
        Notification notification = Notification.builder()
                .titre(dto.getTitre())
                .message(dto.getMessage())
                .typeNotification(dto.getTypeNotification())
                .statut("EN_ATTENTE")
                .lu(false)
                .alerte(alerte)
                .utilisateurs(utilisateurs)
                .build();

        notification = notificationRepository.save(notification);

        log.info("Notification créée avec succès: ID {}", notification.getIdNotification());
        return ResponseNotificationDTO.fromEntity(notification);
    }

    @Override
    public ResponseNotificationDTO getNotificationById(Long id) throws Exception {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification non trouvée avec l'ID: " + id));

        return ResponseNotificationDTO.fromEntity(notification);
    }

    @Override
    public List<ResponseNotificationDTO> getAllNotifications() throws Exception {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream()
                .map(ResponseNotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseNotificationDTO> getNotificationsByAlerte(Long idAlerte) throws Exception {
        List<Notification> notifications = notificationRepository.findByAlerteIdAlerte(idAlerte);
        return notifications.stream()
                .map(ResponseNotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseNotificationDTO> getNotificationsByUtilisateur(Long idUtilisateur) throws Exception {
        List<Notification> notifications = notificationRepository.findByUtilisateur(idUtilisateur);
        return notifications.stream()
                .map(ResponseNotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseNotificationDTO> getNotificationsNonLues(Long idUtilisateur) throws Exception {
        List<Notification> notifications = notificationRepository.findByUtilisateurAndLu(idUtilisateur, false);
        return notifications.stream()
                .map(ResponseNotificationDTO::fromEntity)
                .collect(Collectors.toList());
    }


    @Override
    public void marquerCommeLu(Long id) throws Exception {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification non trouvée avec l'ID: " + id));

        notificationRepository.marquerCommeLu(id);
        log.info("Notification marquée comme lue: ID {}", id);
    }

    @Override
    public void marquerCommeEnvoye(Long id) throws Exception {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification non trouvée avec l'ID: " + id));

        notificationRepository.marquerCommeEnvoye(id, LocalDateTime.now());
        log.info("Notification marquée comme envoyée: ID {}", id);
    }

    @Override
    public void envoyerNotification(Long id) throws Exception {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification non trouvée avec l'ID: " + id));

        if ("ENVOYE".equals(notification.getStatut())) {
            throw new BusinessException("Cette notification a déjà été envoyée");
        }

        // Ici, vous pouvez ajouter la logique d'envoi réel (email, SMS, etc.)
        // Pour cet exemple, on se contente de marquer comme envoyé

        log.info("Envoi de la notification: {}", notification.getTitre());

        // Simulation d'envoi selon le type
        switch (notification.getTypeNotification()) {
            case "EMAIL":
                log.info("Envoi par email à {} utilisateurs", notification.getUtilisateurs().size());
                break;
            case "SMS":
                log.info("Envoi par SMS à {} utilisateurs", notification.getUtilisateurs().size());
                break;
            case "PUSH":
                log.info("Envoi push à {} utilisateurs", notification.getUtilisateurs().size());
                break;
            case "SYSTEME":
                log.info("Notification système créée pour {} utilisateurs", notification.getUtilisateurs().size());
                break;
        }

        marquerCommeEnvoye(id);
        log.info("Notification envoyée avec succès: ID {}", id);
    }

}
