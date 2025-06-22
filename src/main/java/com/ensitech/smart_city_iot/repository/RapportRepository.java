package com.ensitech.smart_city_iot.repository;

import com.ensitech.smart_city_iot.entity.Chercheur;
import com.ensitech.smart_city_iot.entity.Rapport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {

    // Recherche par chercheur
    List<Rapport> findByChercheur(Chercheur chercheur);

    List<Rapport> findByChercheurIdUtilisateur(Long idChercheur);

    Page<Rapport> findByChercheurIdUtilisateur(Long idChercheur, Pageable pageable);

    // Recherche par statut
    List<Rapport> findByStatut(String statut);

    Page<Rapport> findByStatut(String statut, Pageable pageable);

    // Recherche par type de rapport
    List<Rapport> findByTypeRapport(String typeRapport);

    List<Rapport> findByTypeRapportAndStatut(String typeRapport, String statut);

    // Recherche par format
    List<Rapport> findByFormatFichier(String formatFichier);

    // Recherche par période de création
    List<Rapport> findByDateCreationBetween(LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT r FROM Rapport r WHERE r.dateCreation >= :dateDebut ORDER BY r.dateCreation DESC")
    List<Rapport> findRecentRapports(@Param("dateDebut") LocalDateTime dateDebut);

    // Recherche par période d'analyse
    @Query("SELECT r FROM Rapport r WHERE r.periodeDebut >= :debut AND r.periodeFin <= :fin")
    List<Rapport> findByPeriodeAnalyse(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    // Recherche combinée
    List<Rapport> findByChercheurAndStatut(Chercheur chercheur, String statut);

    List<Rapport> findByChercheurAndTypeRapport(Chercheur chercheur, String typeRapport);

    @Query("SELECT r FROM Rapport r WHERE r.chercheur.idUtilisateur = :idChercheur AND r.statut = :statut ORDER BY r.dateCreation DESC")
    List<Rapport> findByChercheurAndStatutOrderByDate(@Param("idChercheur") Long idChercheur, @Param("statut") String statut);

    // Statistiques
    @Query("SELECT COUNT(r) FROM Rapport r WHERE r.chercheur.idUtilisateur = :idChercheur")
    Long countByChercheur(@Param("idChercheur") Long idChercheur);

    @Query("SELECT COUNT(r) FROM Rapport r WHERE r.chercheur.idUtilisateur = :idChercheur AND r.statut = :statut")
    Long countByChercheurAndStatut(@Param("idChercheur") Long idChercheur, @Param("statut") String statut);

    @Query("SELECT r.typeRapport, COUNT(r) FROM Rapport r GROUP BY r.typeRapport")
    List<Object[]> countByTypeRapport();

    @Query("SELECT r.statut, COUNT(r) FROM Rapport r GROUP BY r.statut")
    List<Object[]> countByStatut();

    @Query("SELECT SUM(r.tailleFichier) FROM Rapport r WHERE r.chercheur.idUtilisateur = :idChercheur")
    Long getTailleTotaleByChercheur(@Param("idChercheur") Long idChercheur);

    // Recherche textuelle
    @Query("SELECT r FROM Rapport r WHERE LOWER(r.nomRapport) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Rapport> findByNomOrDescriptionContaining(@Param("search") String search);

    // Rapports les plus volumineux
    @Query("SELECT r FROM Rapport r WHERE r.statut = 'TERMINE' ORDER BY r.tailleFichier DESC")
    List<Rapport> findLargestRapports(Pageable pageable);

    // Rapports récents par chercheur
    @Query("SELECT r FROM Rapport r WHERE r.chercheur.idUtilisateur = :idChercheur " +
            "ORDER BY r.dateCreation DESC")
    List<Rapport> findRecentByChercheur(@Param("idChercheur") Long idChercheur, Pageable pageable);

    // Vérification d'existence
    boolean existsByNomRapportAndChercheur(String nomRapport, Chercheur chercheur);

    @Query("SELECT COUNT(r) > 0 FROM Rapport r WHERE r.nomRapport = :nomRapport AND " +
            "r.chercheur.idUtilisateur = :idChercheur AND r.statut = 'EN_COURS'")
    boolean existsRapportEnCoursByNomAndChercheur(@Param("nomRapport") String nomRapport, @Param("idChercheur") Long idChercheur);
}
