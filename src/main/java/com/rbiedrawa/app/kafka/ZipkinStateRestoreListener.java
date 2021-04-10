package com.rbiedrawa.app.kafka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import brave.Span;
import brave.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.processor.StateRestoreListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZipkinStateRestoreListener implements StateRestoreListener {
	private final Map<TopicPartition, SpanWithTime> metrics = new ConcurrentHashMap<>();

	private final Tracer tracer;

	@Override
	public void onRestoreStart(final TopicPartition topicPartition,
							   final String storeName,
							   final long startingOffset,
							   final long endingOffset) {

		long totalRecords = endingOffset - startingOffset;
		Span span = this.tracer.nextSpan()
							   .annotate("state-restore")
							   .tag("totalRecords", String.valueOf(totalRecords))
							   .tag("state-store", storeName)
							   .tag("partition", String.valueOf(topicPartition.partition()))
							   .tag("topic", String.valueOf(topicPartition.topic()))
							   .name("State store: " + topicPartition.toString())
							   .start();

		StopWatch sw = new StopWatch();
		sw.start();

		this.metrics.put(topicPartition, new SpanWithTime(span, sw));

		log.info("Started restoration of {} partition {} total records to be restored {}", storeName, topicPartition.partition(), totalRecords);
	}

	@Override
	public void onBatchRestored(final TopicPartition topicPartition,
								final String storeName,
								final long batchEndOffset,
								final long numRestored) {

		log.info("Restored batch {} for {} partition {} ", numRestored, storeName, topicPartition.partition());
	}

	@Override
	public void onRestoreEnd(final TopicPartition topicPartition,
							 final String storeName,
							 final long totalRestored) {
		StopWatch stopWatch = this.metrics.get(topicPartition).getStopWatch();
		stopWatch.stop();

		Span span = this.metrics.get(topicPartition)
								.getSpan()
								.tag("totalTimeMillis", "" + stopWatch.getLastTaskTimeMillis());
		span.finish();
		log.info("Restoration complete for {} partition {} totalRestoredRecords: {} totalTime: {}", storeName, topicPartition.partition(), totalRestored, stopWatch.getLastTaskTimeMillis());

		this.metrics.remove(topicPartition);
	}
}