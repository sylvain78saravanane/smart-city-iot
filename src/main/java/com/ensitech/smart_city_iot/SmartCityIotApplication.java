package com.ensitech.smart_city_iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartCityIotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCityIotApplication.class, args);
    }

}

// TODO : CRUD Utilisateur (DTO + Service)
// TODO : Hashage mot de passe
// TODO : Exception Classe à créer (EntityNotFoundException et BusinessException) pour la gestion des erreurs à implémenter dans le controleur
// TODO : Création du Controller Utilisateur
// TODO : Test Unitaire (CRUD Utilisateur)
