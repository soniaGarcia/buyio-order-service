package com.buyio.order.service;

import com.buyio.order.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic-order}")
    private String orderTopic;

    public void publishEvent(OrderEvent event) {
        log.info("Publicando evento Kafka [{}] a tópico: {}", event.getEventType(), orderTopic);
        kafkaTemplate.send(orderTopic, event.getOrderId().toString(), event);
    }
}