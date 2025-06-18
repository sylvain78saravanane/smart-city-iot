package com.ensitech.smart_city_iot.repository;

import com.ensitech.smart_city_iot.entity.Alerte;
import com.ensitech.smart_city_iot.entity.Capteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlerteRepository extends JpaRepository<Alerte, Long> {

    // Recherche par capteur
    List<Alerte> findByCapteur(Capteur capteur);

    List<Alerte> findByCapteurIdCapteur(Long idCapteur);

    // Recherche par statut
    List<Alerte> findByActive(Boolean active);

    // Recherche par priorité
    List<Alerte> findByPriorite(String priorite);

    // Recherche par type de condition
    List<Alerte> findByTypeCondition(String typeCondition);

    // Recherche combinée
    List<Alerte> findByCapteurAndActive(Capteur capteur, Boolean active);

    List<Alerte> findByPrioriteAndActive(String priorite, Boolean active);

    // Requêtes personnalisées
    @Query("SELECT a FROM Alerte a WHERE a.active = true ORDER BY a.priorite DESC, a.dateCreation DESC")
    List<Alerte> findActiveAlertesByPriority();

    @Query("SELECT COUNT(a) FROM Alerte a WHERE a.capteur.idCapteur = :idCapteur AND a.active = true")
    Long countActiveByCapteur(@Param("idCapteur") Long idCapteur);
}
