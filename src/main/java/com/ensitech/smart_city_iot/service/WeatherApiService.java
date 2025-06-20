package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.DonneIotDTO.WeatherApiResponseDTO;
import com.ensitech.smart_city_iot.entity.Capteur;

import java.util.Map;

public interface WeatherApiService {

    WeatherApiResponseDTO getWeatherData(Capteur capteur) throws Exception;

    Map<String, String> getVillesFrancaisesDisponibles();
}
