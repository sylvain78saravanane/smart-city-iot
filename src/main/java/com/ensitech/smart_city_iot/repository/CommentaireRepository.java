package com.ensitech.smart_city_iot.repository;

import com.ensitech.smart_city_iot.entity.Citoyen;
import com.ensitech.smart_city_iot.entity.Commentaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    // Recherche par citoyen
    List<Commentaire> findByCitoyen(Citoyen citoyen);

    List<Commentaire> findByCitoyenAndActif(Citoyen citoyen, Boolean actif);

    Page<Commentaire> findByCitoyen(Citoyen citoyen, Pageable pageable);

    // Recherche par statut
    List<Commentaire> findByActif(Boolean actif);

    Page<Commentaire> findByActif(Boolean actif, Pageable pageable);

    // Recherche par sujet
    List<Commentaire> findBySujet(String sujet);

    List<Commentaire> findBySujetAndActif(String sujet, Boolean actif);

    // Recherche par note d'évaluation
    List<Commentaire> findByNoteEvaluationGreaterThanEqual(Integer note);

    @Query("SELECT c FROM Commentaire c WHERE c.noteEvaluation >= :noteMin AND c.noteEvaluation <= :noteMax AND c.actif = true")
    List<Commentaire> findByNoteEvaluationBetween(@Param("noteMin") Integer noteMin, @Param("noteMax") Integer noteMax);

    // Recherche par période
    List<Commentaire> findByDateCreationBetween(LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT c FROM Commentaire c WHERE c.dateCreation >= :dateDebut AND c.actif = true ORDER BY c.dateCreation DESC")
    List<Commentaire> findRecentCommentaires(@Param("dateDebut") LocalDateTime dateDebut);

    // Recherche par popularité
    @Query("SELECT c FROM Commentaire c WHERE c.actif = true ORDER BY (c.nombreLikes - c.nombreDislikes) DESC")
    List<Commentaire> findMostPopular(Pageable pageable);

    @Query("SELECT c FROM Commentaire c WHERE c.actif = true ORDER BY c.nombreLikes DESC")
    List<Commentaire> findMostLiked(Pageable pageable);

    // Recherche textuelle
    @Query("SELECT c FROM Commentaire c WHERE (LOWER(c.titre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.contenu) LIKE LOWER(CONCAT('%', :search, '%'))) AND c.actif = true")
    List<Commentaire> findByTitreOrContenuContaining(@Param("search") String search);

    // Statistiques
    @Query("SELECT COUNT(c) FROM Commentaire c WHERE c.citoyen.idUtilisateur = :idCitoyen")
    Long countByCitoyen(@Param("idCitoyen") Long idCitoyen);

    @Query("SELECT c.sujet, COUNT(c) FROM Commentaire c WHERE c.actif = true GROUP BY c.sujet")
    List<Object[]> countBySujet();

    @Query("SELECT AVG(c.noteEvaluation) FROM Commentaire c WHERE c.noteEvaluation IS NOT NULL AND c.actif = true")
    Double getAverageNote();

    // Gestion des likes/dislikes
    @Transactional
    @Modifying
    @Query("UPDATE Commentaire c SET c.nombreLikes = c.nombreLikes + 1 WHERE c.idCommentaire = :id")
    void incrementLikes(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Commentaire c SET c.nombreDislikes = c.nombreDislikes + 1 WHERE c.idCommentaire = :id")
    void incrementDislikes(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Commentaire c SET c.nombreLikes = GREATEST(0, c.nombreLikes - 1) WHERE c.idCommentaire = :id")
    void decrementLikes(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Commentaire c SET c.nombreDislikes = GREATEST(0, c.nombreDislikes - 1) WHERE c.idCommentaire = :id")
    void decrementDislikes(@Param("id") Long id);

    // Suppression logique
    @Transactional
    @Modifying
    @Query("UPDATE Commentaire c SET c.actif = false WHERE c.idCommentaire = :id")
    void softDelete(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Commentaire c SET c.actif = false WHERE c.citoyen.idUtilisateur = :idCitoyen")
    void softDeleteByCitoyen(@Param("idCitoyen") Long idCitoyen);

    // Modération
    @Query("SELECT c FROM Commentaire c WHERE c.nombreDislikes > :seuil AND c.actif = true")
    List<Commentaire> findCommentairesAModerer(@Param("seuil") Integer seuil);

    @Query("SELECT c FROM Commentaire c WHERE c.actif = true AND " +
            "(LOWER(c.contenu) LIKE '%spam%' OR LOWER(c.contenu) LIKE '%inapproprie%')")
    List<Commentaire> findPotentialSpam();
}
