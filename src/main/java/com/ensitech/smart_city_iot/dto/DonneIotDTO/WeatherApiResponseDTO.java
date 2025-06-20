package com.ensitech.smart_city_iot.dto.DonneIotDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeatherApiResponseDTO {

    private Location location;
    private Current current;

    @Data
    public static class Location {
        private String name;
        private String region;
        private String country;
        private Double lat;
        private Double lon;
        private String localtime;
    }

    @Data
    public static class Current {
        @JsonProperty("temp_c")
        private Double tempC;

        @JsonProperty("temp_f")
        private Double tempF;

        @JsonProperty("wind_kph")
        private Double windKph;

        @JsonProperty("precip_mm")
        private Double precipMm;

        private Double humidity;
        private Double cloud;
        private Double uv;

        @JsonProperty("air_quality")
        private AirQuality airQuality;
    }

    @Data
    public static class AirQuality{
        private Double co;
        private Double no2;
        private Double o3;
        private Double so2;
        private Double pm10;

    }
}
