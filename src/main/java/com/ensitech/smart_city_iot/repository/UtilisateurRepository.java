package com.ensitech.smart_city_iot.repository;

import com.ensitech.smart_city_iot.entity.Citoyen;
import com.ensitech.smart_city_iot.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

}
