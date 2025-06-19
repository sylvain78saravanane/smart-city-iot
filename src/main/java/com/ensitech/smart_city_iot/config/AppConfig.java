package com.ensitech.smart_city_iot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * Configuration RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(){
        return restTemplate();
    }

    /**
     * Configuration Kafka (Le nom du Topic, Partition, Réplica)
     */

    @Value("smart-city-iot")
    private String topicName;

    @Bean
    public NewTopic smartCityIotTopic(){
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .build();
    }


}
