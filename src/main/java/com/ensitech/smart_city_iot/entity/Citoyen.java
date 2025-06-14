package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "citoyen")
public class Citoyen extends Utilisateur{
}
