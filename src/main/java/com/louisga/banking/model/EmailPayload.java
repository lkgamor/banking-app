package com.louisga.banking.model;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class EmailPayload {
	
	private String subject;	
	private String body;
	private ZonedDateTime dateTime;
	private String to;
}
