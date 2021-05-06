
package com.louisga.banking.config;

import java.time.ZonedDateTime;

import org.springframework.context.event.EventListener;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.louisga.banking.utility.AppConstants;
import com.louisga.banking.utility.CentrifugoJwt;
import com.louisga.banking.utility.CustomNewAccountCreatedEvent;
import com.louisga.banking.utility.CustomTransactionMadeEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor 
public class DefaultAdvice {

    private final CentrifugoJwt centrifugoJwt;
	private final CentrifugoConfiguration centrifugo;
	private final CompanyConfiguration companyConfig;
    
	@ModelAttribute
    public void getCentrifugoConfigs(Model model) {
		
		final String CENTRIFUGO_ACCESS_TOKEN = centrifugoJwt.createJWT();
		final String CENTRIFUGO_WEBSOCKET = centrifugo.getWebsocket();
		final String CENTRIFUGO_ACCOUNT_CHANNEL = centrifugo.getChannelAccount();
		final String CENTRIFUGO_TRANSACTION_CHANNEL = centrifugo.getChannelTransaction();
		final Integer COMPANY_TRANSACTION_PREFIX_LENGTH = companyConfig.getAccountNamesPrefix().length();
		
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_CENTRIFUGO_TOKEN, CENTRIFUGO_ACCESS_TOKEN);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_CENTRIFUGO_WEBSOCKET, CENTRIFUGO_WEBSOCKET);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_ACCOUNT_CHANNEL, CENTRIFUGO_ACCOUNT_CHANNEL);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_TRANSACTION_CHANNEL, CENTRIFUGO_TRANSACTION_CHANNEL);
		model.addAttribute(AppConstants.MODEL_ATTRIBUTE_TRANSACTION_PREFIX_LENGTH, COMPANY_TRANSACTION_PREFIX_LENGTH);
	}
    
	@EventListener
	void onEvent(CustomNewAccountCreatedEvent event) {
		String accountName = event.getAccountName();
		String accountNumber = event.getAccountNumber();
		String openedBy = event.getOpenedBy();
		ZonedDateTime dateOpened = event.getDateOpened();	
		log.info("EVENT PUBLISHED FOR CREATING NEW ACCOUNT --> {}, {}, {}, {}", accountName, accountNumber, openedBy, dateOpened);
	}

	@EventListener
	void onEvent(CustomTransactionMadeEvent event) {
		String transactionType = event.getTransactionType();
		Double transactionAmount = event.getTransactionAmount();
		ZonedDateTime transactionDate = event.getTransactionDate();	
		String transactionIssuedBy = event.getTransactionIssuedBy();
		String transactionAccountNumber = event.getTransactionAccountNumber();
		log.info("EVENT PUBLISHED FOR CREATING NEW TRANSACTION --> {}, {}, {}, {}, {}", transactionType, transactionAmount, transactionAccountNumber, transactionDate, transactionIssuedBy);
	}
}
