package com.rbiedrawa.app.kafka;

import org.springframework.util.StopWatch;

import brave.Span;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
class SpanWithTime {
	private final Span span;
	private final StopWatch stopWatch;
}
