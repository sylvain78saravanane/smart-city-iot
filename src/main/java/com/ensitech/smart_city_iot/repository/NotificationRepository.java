package com.ensitech.smart_city_iot.repository;

import com.ensitech.smart_city_iot.entity.Alerte;
import com.ensitech.smart_city_iot.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Recherche par alerte
    List<Notification> findByAlerte(Alerte alerte);

    List<Notification> findByAlerteIdAlerte(Long idAlerte);

    // Recherche par utilisateur
    @Query("SELECT n FROM Notification n JOIN n.utilisateurs u WHERE u.idUtilisateur = :idUtilisateur")
    List<Notification> findByUtilisateur(@Param("idUtilisateur") Long idUtilisateur);

    // Recherche par statut
    List<Notification> findByStatut(String statut);

    // Recherche par type
    List<Notification> findByTypeNotification(String typeNotification);

    // Recherche par statut de lecture
    List<Notification> findByLu(Boolean lu);

    // Recherche par utilisateur et statut de lecture
    @Query("SELECT n FROM Notification n JOIN n.utilisateurs u WHERE u.idUtilisateur = :idUtilisateur AND n.lu = :lu")
    List<Notification> findByUtilisateurAndLu(@Param("idUtilisateur") Long idUtilisateur, @Param("lu") Boolean lu);

    // Recherche par période
    List<Notification> findByDateCreationBetween(LocalDateTime debut, LocalDateTime fin);

    // Marquer comme lu
    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.lu = true WHERE n.idNotification = :id")
    void marquerCommeLu(@Param("id") Long id);

    // Marquer comme envoyé
    @Transactional
    @Modifying
    @Query("UPDATE Notification n SET n.statut = 'ENVOYE', n.dateEnvoi = :dateEnvoi WHERE n.idNotification = :id")
    void marquerCommeEnvoye(@Param("id") Long id, @Param("dateEnvoi") LocalDateTime dateEnvoi);

    // Notifications non lues par utilisateur
    @Query("SELECT COUNT(n) FROM Notification n JOIN n.utilisateurs u WHERE u.idUtilisateur = :idUtilisateur AND n.lu = false")
    Long countNonLuesByUtilisateur(@Param("idUtilisateur") Long idUtilisateur);
}
