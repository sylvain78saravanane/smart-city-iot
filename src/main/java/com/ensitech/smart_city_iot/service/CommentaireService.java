package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.commentaireDTO.CreateCommentaireDTO;
import com.ensitech.smart_city_iot.dto.commentaireDTO.ResponseCommentaireDTO;
import com.ensitech.smart_city_iot.dto.commentaireDTO.UpdateCommentaireDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentaireService {

    ResponseCommentaireDTO createCommentaire(CreateCommentaireDTO dto) throws Exception;

    ResponseCommentaireDTO getCommentaireById(Long id) throws Exception;

    List<ResponseCommentaireDTO> getAllCommentaires() throws Exception;

    Page<ResponseCommentaireDTO> getAllCommentaires(Pageable pageable) throws Exception;

    List<ResponseCommentaireDTO> getCommentairesByCitoyen(Long idCitoyen) throws Exception;

    List<ResponseCommentaireDTO> getCommentairesBySujet(String sujet) throws Exception;

    ResponseCommentaireDTO updateCommentaire(Long id, UpdateCommentaireDTO dto) throws Exception;

    void deleteCommentaire(Long id) throws Exception;

    void softDeleteCommentaire(Long id) throws Exception;

    // Gestion des interactions
    void ajouterLike(Long id) throws Exception;

    void retirerLike(Long id) throws Exception;

    void ajouterDislike(Long id) throws Exception;

    void retirerDislike(Long id) throws Exception;

    // Recherche et filtres
    List<ResponseCommentaireDTO> rechercherCommentaires(String texte) throws Exception;

    List<ResponseCommentaireDTO> getCommentairesPopulaires(int limite) throws Exception;

    List<ResponseCommentaireDTO> getCommentairesRecents(int jours) throws Exception;

    // Statistiques
    Long getNombreCommentairesByCitoyen(Long idCitoyen) throws Exception;

    Double getNoteAverage() throws Exception;
}
