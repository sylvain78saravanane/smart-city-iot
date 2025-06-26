package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.utilisateurDTO.CreateUtilisateurDTO;
import com.ensitech.smart_city_iot.dto.utilisateurDTO.ResponseUtilisateurDTO;
import com.ensitech.smart_city_iot.dto.utilisateurDTO.UpdateUtilisateurDTO;
import com.ensitech.smart_city_iot.entity.Administrateur;
import com.ensitech.smart_city_iot.entity.Utilisateur;

public interface UtilisateurService {

    ResponseUtilisateurDTO createUtilisateur (CreateUtilisateurDTO dto) throws Exception;

    Utilisateur findByEmail(String email);

    Utilisateur login(String email, String password) throws Exception;

    Administrateur loginAdmin (String email, String password, String codeAdmin) throws Exception;

    UpdateUtilisateurDTO updateUtilisateur (Long id, UpdateUtilisateurDTO dto) throws Exception;

    void deleteUtilisateur(Long id) throws Exception;

    void updateFields(Utilisateur utilisateur, UpdateUtilisateurDTO dto) throws Exception;

    ResponseUtilisateurDTO getUtilisateurById(Long id) throws Exception;



}
