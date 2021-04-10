package com.rbiedrawa.app.words;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;

@Slf4j
@Configuration
public class WordCountKStream {

	public static final int WINDOW_SIZE_MS = 1_000;

	@Bean
	public Function<KStream<Bytes, String>, KStream<Bytes, WordCount>> process() {
		return input -> input
			.peek((key, value) -> log.info("Received key: {} value: {}", key, value))
			.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
			.map((key, value) -> new KeyValue<>(value, value))
			.groupByKey(Grouped.with(Serdes.String(), Serdes.String()))
			.windowedBy(TimeWindows.of(Duration.ofMillis(WINDOW_SIZE_MS)))
			.count(Materialized.as("WordCounts"))
			.toStream()
			.map((key, value) -> new KeyValue<>(null, new WordCount(key.key(), value, new Date(key.window().start()), new Date(key.window().end()))));
	}
}
