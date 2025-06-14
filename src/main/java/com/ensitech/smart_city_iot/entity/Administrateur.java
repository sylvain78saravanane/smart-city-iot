package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrateur")
public class Administrateur extends Utilisateur {

    @Column(name = "code_admin")
    private String codeAdmin;
}
