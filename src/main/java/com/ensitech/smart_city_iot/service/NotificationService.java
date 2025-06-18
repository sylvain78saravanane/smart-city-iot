package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.notificationDTO.CreateNotificationDTO;
import com.ensitech.smart_city_iot.dto.notificationDTO.ResponseNotificationDTO;

import java.util.List;

public interface NotificationService {

    ResponseNotificationDTO createNotification(CreateNotificationDTO dto) throws Exception;

    ResponseNotificationDTO getNotificationById(Long id) throws Exception;

    List<ResponseNotificationDTO> getAllNotifications() throws Exception;

    List<ResponseNotificationDTO> getNotificationsByAlerte(Long idAlerte) throws Exception;

    List<ResponseNotificationDTO> getNotificationsByUtilisateur(Long idUtilisateur) throws Exception;

    List<ResponseNotificationDTO> getNotificationsNonLues(Long idUtilisateur) throws Exception;


    void marquerCommeLu(Long id) throws Exception;

    void marquerCommeEnvoye(Long id) throws Exception;

    void envoyerNotification(Long id) throws Exception;
}
