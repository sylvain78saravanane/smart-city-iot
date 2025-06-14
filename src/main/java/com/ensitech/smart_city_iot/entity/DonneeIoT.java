package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "donnee_iot")
public class DonneeIoT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_donnee_iot")
    private Long idDonneeIoT;
}
