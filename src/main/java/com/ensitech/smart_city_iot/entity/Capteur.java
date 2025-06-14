package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "capteur")
public class Capteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_capteur")
    private Long idCapteur;

}
