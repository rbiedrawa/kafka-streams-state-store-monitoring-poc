package com.rbiedrawa.app.words;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class WordCount {
	private String word;
	private long count;
	private Date start;
	private Date end;
}