package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "modèle")
public class Modele {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modele")
    private Long idModele;


}
