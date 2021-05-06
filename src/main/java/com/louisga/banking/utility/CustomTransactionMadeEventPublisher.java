package com.louisga.banking.utility;

import java.time.ZonedDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * @author LouisGa
 */

@Component
public class CustomTransactionMadeEventPublisher {

	private final ApplicationEventPublisher publisher;

	public CustomTransactionMadeEventPublisher(ApplicationEventPublisher publisher) {
		super();
		this.publisher = publisher;
	}
	
	public void publishTransactionMadeEvent(final String transactionType, final Double transactionAmount, 
			 final String transactionAccountNumber, final ZonedDateTime transactionDate, final String transactionIssuedBy) {
		
		publisher.publishEvent(new CustomTransactionMadeEvent(transactionType, transactionAmount, transactionAccountNumber,  transactionDate, transactionIssuedBy));
		
	}	
}
