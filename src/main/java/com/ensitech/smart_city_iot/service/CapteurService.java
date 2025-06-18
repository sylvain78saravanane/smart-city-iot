package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.capteurDTO.CreateCapteurDTO;
import com.ensitech.smart_city_iot.dto.capteurDTO.ResponseCapteurDTO;

import java.util.List;


public interface CapteurService {

    ResponseCapteurDTO createCapteur(CreateCapteurDTO dto) throws Exception;
    ResponseCapteurDTO getCapteurById(Long id) throws Exception;
    List<ResponseCapteurDTO> getCapteursByGestionnaire(Long idGestionnaire) throws Exception;
    List<ResponseCapteurDTO> getAllCapteurs() throws Exception;
    void deleteCapteur(Long id) throws Exception;
}
