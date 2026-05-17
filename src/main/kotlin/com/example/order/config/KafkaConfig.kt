package com.example.order.config

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {

    @Bean
    fun orderCreatedTopic(): NewTopic =
        TopicBuilder.name("order.created")
            .partitions(3)
            .replicas(1)
            .build()
}
