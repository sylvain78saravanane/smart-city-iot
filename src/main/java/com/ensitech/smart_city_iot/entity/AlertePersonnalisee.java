package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alerte_personnalisée")
public class AlertePersonnalisee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerte_personnalisée")
    private Long idAlertePersonnalisee;
}
