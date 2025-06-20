package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.entity.Capteur;

public interface DonneeIoTService {

    void collecterDonneesAutomatiquement();

    void collecterDonneesPourCapteur(Capteur capteur) throws Exception;

    void traiterMessageKafka(String message);

    void collecterManuellement(Long idCapteur) throws Exception;
}
