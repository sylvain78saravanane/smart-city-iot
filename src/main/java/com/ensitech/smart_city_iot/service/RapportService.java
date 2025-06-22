package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.rapportDTO.CreateRapportDTO;
import com.ensitech.smart_city_iot.dto.rapportDTO.ResponseRapportDTO;
import com.ensitech.smart_city_iot.dto.rapportDTO.UpdateRapportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface RapportService {
    ResponseRapportDTO createRapport(CreateRapportDTO dto) throws Exception;

    ResponseRapportDTO getRapportById(Long id) throws Exception;

    List<ResponseRapportDTO> getAllRapports() throws Exception;

    Page<ResponseRapportDTO> getAllRapports(Pageable pageable) throws Exception;

    List<ResponseRapportDTO> getRapportsByChercheur(Long idChercheur) throws Exception;

    List<ResponseRapportDTO> getRapportsByStatut(String statut) throws Exception;

    List<ResponseRapportDTO> getRapportsByType(String typeRapport) throws Exception;

    ResponseRapportDTO updateRapport(Long id, UpdateRapportDTO dto) throws Exception;

    void deleteRapport(Long id) throws Exception;

    // Génération de contenu
    void genererContenuRapport(Long id) throws Exception;

    byte[] telechargerRapport(Long id) throws Exception;

    // Recherche et filtres
    List<ResponseRapportDTO> rechercherRapports(String texte) throws Exception;

    List<ResponseRapportDTO> getRapportsRecents(int jours) throws Exception;

    List<ResponseRapportDTO> getRapportsByPeriodeAnalyse(LocalDateTime debut, LocalDateTime fin) throws Exception;

    // Statistiques
    Long getNombreRapportsByChercheur(Long idChercheur) throws Exception;

    Long getTailleTotaleByChercheur(Long idChercheur) throws Exception;
}
