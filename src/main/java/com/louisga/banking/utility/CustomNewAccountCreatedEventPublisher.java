package com.louisga.banking.utility;

import java.time.ZonedDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author LouisGa
 */

@Component
public class CustomNewAccountCreatedEventPublisher {

	private final ApplicationEventPublisher publisher;

	public CustomNewAccountCreatedEventPublisher(ApplicationEventPublisher publisher) {
		super();
		this.publisher = publisher;
	}
	
	public void publishCreatedAccountEvent(final String accountName, final String accountNumber, final String openedBy, final ZonedDateTime dateOpened) {
		publisher.publishEvent(new CustomNewAccountCreatedEvent(accountName, accountNumber, openedBy, dateOpened));
	}	
}
