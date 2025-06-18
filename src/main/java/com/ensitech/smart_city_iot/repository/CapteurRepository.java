package com.ensitech.smart_city_iot.repository;

import com.ensitech.smart_city_iot.entity.Administrateur;
import com.ensitech.smart_city_iot.entity.Capteur;
import com.ensitech.smart_city_iot.entity.GestionnaireDeVille;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapteurRepository extends JpaRepository<Capteur, Long> {
    // Recherche par gestionnaire
    List<Capteur> findByAdministrateur(Administrateur administrateur);

    List<Capteur> findByGestionnaireDeVille(GestionnaireDeVille gestionnaireDeVille);

    // Recherche par type et statut
    List<Capteur> findByTypeCapteur(String typeCapteur);

    List<Capteur> findByStatut(String statut);

    List<Capteur> findByTypeCapteurAndStatut(String typeCapteur, String statut);

    // Recherche par numéro de série
    Optional<Capteur> findByNumeroSerie(String numeroSerie);

    boolean existsByNumeroSerie(String numeroSerie);

    // Recherche géographique
    @Query("SELECT c FROM Capteur c WHERE c.latitude BETWEEN :latMin AND :latMax AND c.longitude BETWEEN :lonMin AND :lonMax")
    List<Capteur> findByZoneGeographique(@Param("latMin") Double latMin,
                                         @Param("latMax") Double latMax,
                                         @Param("lonMin") Double lonMin,
                                         @Param("lonMax") Double lonMax);

    // Recherche par gestionnaire responsable (Admin OU Gestionnaire)
    @Query("SELECT c FROM Capteur c WHERE " +
            "(c.administrateur.idUtilisateur = :idUtilisateur) OR " +
            "(c.gestionnaireDeVille.idUtilisateur = :idUtilisateur)")
    List<Capteur> findByGestionnaireResponsable(@Param("idUtilisateur") Long idUtilisateur);

    // Statistiques
    @Query("SELECT COUNT(c) FROM Capteur c WHERE c.administrateur.idUtilisateur = :idAdmin")
    Long countByAdministrateur(@Param("idAdmin") Long idAdmin);

    @Query("SELECT COUNT(c) FROM Capteur c WHERE c.gestionnaireDeVille.idUtilisateur = :idGestionnaire")
    Long countByGestionnaireDeVille(@Param("idGestionnaire") Long idGestionnaire);

    @Query("SELECT c.typeCapteur, COUNT(c) FROM Capteur c GROUP BY c.typeCapteur")
    List<Object[]> countByTypeCapteur();

    @Query("SELECT c.statut, COUNT(c) FROM Capteur c GROUP BY c.statut")
    List<Object[]> countByStatut();
}
