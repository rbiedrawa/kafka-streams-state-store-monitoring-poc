package com.rbiedrawa.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

import org.apache.kafka.clients.admin.NewTopic;

@EnableKafka
@Configuration
public class KafkaTopicConfiguration {

	private static final int DEFAULT_PARTITION_COUNT = 6;

	@Bean
	NewTopic wordsTopic() {
		return TopicBuilder.name("words")
						   .partitions(6)
						   .replicas(1)
						   .build();
	}

	@Bean
	NewTopic countsTopic() {
		return TopicBuilder.name("counts")
						   .partitions(DEFAULT_PARTITION_COUNT)
						   .replicas(1)
						   .build();
	}
}
