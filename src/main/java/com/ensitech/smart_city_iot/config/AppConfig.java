package com.ensitech.smart_city_iot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class AppConfig {

    /**
     * Configuration RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /**
     * Configuration Kafka (Le nom du Topic, Partition, Réplica)
     */

    @Value("${kafka.topic.name}")
    private String topicName;

    @Bean
    public NewTopic smartCityIotTopic(){
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(1)
                .build();
    }


}
