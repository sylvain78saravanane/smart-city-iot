package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "alerte")

public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerte")
    private Long idAlerte;



}
