package com.ensitech.smart_city_iot.repository;

import com.ensitech.smart_city_iot.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Utilisateur findByEmail(String email);

    List<Utilisateur> findByActif(Boolean actif);

    @Query("SELECT u FROM Utilisateur u WHERE TYPE(u) = :type")
    List<Utilisateur> findByType(@Param("type") Class<? extends Utilisateur> type);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE TYPE(u) = :type")
    Long countByType(@Param("type") Class<? extends Utilisateur> type);

    // Méthodes spécifiques par type
    @Query("SELECT u FROM Utilisateur u WHERE TYPE(u) = Administrateur")
    List<Administrateur> findAllAdministrateurs();

    @Query("SELECT u FROM Utilisateur u WHERE TYPE(u) = GestionnaireDeVille")
    List<GestionnaireDeVille> findAllGestionnaires();

    @Query("SELECT u FROM Utilisateur u WHERE TYPE(u) = Chercheur")
    List<Chercheur> findAllChercheurs();

    @Query("SELECT u FROM Utilisateur u WHERE TYPE(u) = Citoyen")
    List<Citoyen> findAllCitoyens();

    @Transactional
    @Modifying
    @Query("DELETE FROM Utilisateur u WHERE u.actif = false ")
    void deleteInactifUtilisateur();

    @Query("")
    void deleteUtilisateursById();
}
