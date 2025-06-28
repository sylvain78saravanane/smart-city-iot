package com.ensitech.smart_city_iot.repository;

import com.ensitech.smart_city_iot.entity.Capteur;
import com.ensitech.smart_city_iot.entity.DonneeIoT;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonneeIoTRepository extends JpaRepository<DonneeIoT, Long> {
    // Recherche par capteur
    List<DonneeIoT> findByCapteur(Capteur capteur);

    List<DonneeIoT> findByCapteurIdCapteur(Long idCapteur);

    @Query("SELECT d FROM DonneeIoT d " +
            "JOIN FETCH d.capteur c " +  // Force le chargement du capteur
            "WHERE c.idCapteur = :idCapteur " +
            "ORDER BY d.timestampCollecte DESC")
    List<DonneeIoT> findLatestByCapteurWithCapteur(@Param("idCapteur") Long idCapteur, Pageable pageable);

    Page<DonneeIoT> findByCapteurIdCapteur(Long idCapteur, Pageable pageable);

    // Recherche par période
    List<DonneeIoT> findByTimestampCollecteBetween(LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT d FROM DonneeIoT d WHERE d.capteur.idCapteur = :idCapteur AND d.timestampCollecte BETWEEN :debut AND :fin ORDER BY d.timestampCollecte DESC")
    List<DonneeIoT> findByCapteurAndPeriode(@Param("idCapteur") Long idCapteur,
                                            @Param("debut") LocalDateTime debut,
                                            @Param("fin") LocalDateTime fin);

    // Recherche par ville/région
    List<DonneeIoT> findByVilleNom(String villeNom);

    List<DonneeIoT> findByRegion(String region);

    // Données récentes
    @Query("SELECT d FROM DonneeIoT d WHERE d.timestampCollecte >= :dateDebut ORDER BY d.timestampCollecte DESC")
    List<DonneeIoT> findRecentData(@Param("dateDebut") LocalDateTime dateDebut);

    @Query("SELECT d FROM DonneeIoT d WHERE d.capteur.idCapteur = :idCapteur ORDER BY d.timestampCollecte DESC")
    List<DonneeIoT> findLatestByCapteur(@Param("idCapteur") Long idCapteur, Pageable pageable);

    // Statistiques
    @Query("SELECT COUNT(d) FROM DonneeIoT d WHERE d.capteur.idCapteur = :idCapteur")
    Long countByCapteur(@Param("idCapteur") Long idCapteur);

    @Query("SELECT AVG(d.temperatureCelsius) FROM DonneeIoT d WHERE d.capteur.idCapteur = :idCapteur AND d.timestampCollecte BETWEEN :debut AND :fin")
    Double getAverageTemperatureByCapteurAndPeriode(@Param("idCapteur") Long idCapteur,
                                                    @Param("debut") LocalDateTime debut,
                                                    @Param("fin") LocalDateTime fin);

    @Query("SELECT MAX(d.temperatureCelsius) FROM DonneeIoT d WHERE d.capteur.idCapteur = :idCapteur AND d.timestampCollecte BETWEEN :debut AND :fin")
    Double getMaxTemperatureByCapteurAndPeriode(@Param("idCapteur") Long idCapteur,
                                                @Param("debut") LocalDateTime debut,
                                                @Param("fin") LocalDateTime fin);

    @Query("SELECT MIN(d.temperatureCelsius) FROM DonneeIoT d WHERE d.capteur.idCapteur = :idCapteur AND d.timestampCollecte BETWEEN :debut AND :fin")
    Double getMinTemperatureByCapteurAndPeriode(@Param("idCapteur") Long idCapteur,
                                                @Param("debut") LocalDateTime debut,
                                                @Param("fin") LocalDateTime fin);

    // Qualité de l'air
    @Query("SELECT AVG(d.co) FROM DonneeIoT d WHERE d.capteur.idCapteur = :idCapteur AND d.timestampCollecte BETWEEN :debut AND :fin")
    Double getAverageCOByCapteurAndPeriode(@Param("idCapteur") Long idCapteur,
                                           @Param("debut") LocalDateTime debut,
                                           @Param("fin") LocalDateTime fin);

    // Données par statut
    List<DonneeIoT> findByStatutDonnee(String statutDonnee);

    // Recherche par source API
    List<DonneeIoT> findBySourceApi(String sourceApi);

    // Nettoyage des anciennes données
    @Query("DELETE FROM DonneeIoT d WHERE d.timestampCollecte < :dateLimit")
    void deleteOldData(@Param("dateLimit") LocalDateTime dateLimit);
}
