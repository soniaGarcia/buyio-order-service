package com.buyio.order.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${SPRING_KAFKA_BOOTSTRAP_SERVERS:${KAFKA_BOOTSTRAP_SERVERS:kafka:29092}}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // 1. Instanciación explícita del Mapper de Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 2. Escanea el classpath y registra automáticamente el módulo JSR-310 (java.time)
        objectMapper.findAndRegisterModules();
        
        // 3. Serializa fechas en formato estándar ISO-8601 ("2026-09-20T00:35:59") en lugar de timestamps numéricos
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new DefaultKafkaProducerFactory<>(
                configProps,
                new StringSerializer(),
                new JsonSerializer<>(objectMapper)
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}