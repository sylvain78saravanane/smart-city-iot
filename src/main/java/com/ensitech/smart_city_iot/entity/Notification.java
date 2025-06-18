package com.ensitech.smart_city_iot.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notification")
    private Long idNotification;

    @Column(name = "description_notification")
    private String descriptionNotification;

    @Column(name = "date_notification")
    private Date dateNotification;

    @Column(name = "mention_lu")
    private Boolean mentionLu;
}
