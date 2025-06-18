package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.alerteDTO.CreateAlerteDTO;
import com.ensitech.smart_city_iot.dto.alerteDTO.ResponseAlerteDTO;
import com.ensitech.smart_city_iot.dto.alerteDTO.UpdateAlerteDTO;

import java.util.List;

public interface AlerteService {
    ResponseAlerteDTO createAlerte(CreateAlerteDTO dto) throws Exception;

    ResponseAlerteDTO getAlerteById(Long id) throws Exception;

    List<ResponseAlerteDTO> getAllAlertes() throws Exception;

    List<ResponseAlerteDTO> getAlertesByCapteur(Long idCapteur) throws Exception;

    List<ResponseAlerteDTO> getAlertesActives() throws Exception;

    ResponseAlerteDTO updateAlerte(Long id, UpdateAlerteDTO dto) throws Exception;

    void deleteAlerte(Long id) throws Exception;

    void activerAlerte(Long id) throws Exception;

    void desactiverAlerte(Long id) throws Exception;
}
