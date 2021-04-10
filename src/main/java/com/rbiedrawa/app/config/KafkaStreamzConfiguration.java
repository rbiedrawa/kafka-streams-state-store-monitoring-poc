package com.rbiedrawa.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.StateRestoreListener;

@Slf4j
@Configuration
public class KafkaStreamzConfiguration {

	@Bean
	public StreamsBuilderFactoryBeanConfigurer streamsBuilderFactoryBeanCustomizer(StateRestoreListener stateRestoreListener) {
		return factoryBean -> factoryBean.setKafkaStreamsCustomizer(kafkaStreams -> kafkaStreams.setGlobalStateRestoreListener(stateRestoreListener));
	}

}