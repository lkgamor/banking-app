package com.louisga.banking.serviceImpl;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.louisga.banking.config.CompanyConfiguration;
import com.louisga.banking.config.EmailConfiguration;
import com.louisga.banking.model.EmailPayload;
import com.louisga.banking.utility.AppConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailServiceImplementation {
	
	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;
	private final EmailConfiguration emailConfig;
	private final CompanyConfiguration companyConfig;
	
	@Async
	public void processEmail(EmailPayload payload) throws MessagingException, UnsupportedEncodingException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");			 
		String payloadDate = payload.getDateTime().format(formatter);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(AppConstants.EMAIL_COMPANY, companyConfig.getName());
		model.put(AppConstants.EMAIL_BODY, payload.getBody());
		model.put(AppConstants.EMAIL_DATETIME, payloadDate);
		
		Context context = new Context(LocaleContextHolder.getLocale());
		context.setVariables(model);
		
		String mailBody = this.templateEngine.process(AppConstants.EMAIL_TEMPLATE_FILE, context);
		sendEmail(payload, mailBody);
	}


	private void sendEmail(EmailPayload payload, String mailBody) throws MessagingException, UnsupportedEncodingException {
		
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom(emailConfig.getSender(), companyConfig.getName());;
		helper.setReplyTo(emailConfig.getSender());
		helper.setSentDate(new Date());
		helper.setText(mailBody, true);
		helper.setSubject(payload.getSubject().toUpperCase());
		
		String recipientAddress = payload.getTo();		
		helper.setTo(recipientAddress);
		
		try {
			javaMailSender.send(mimeMessage);
			log.info(AppConstants.EMAIL_SENT_SUCCESS_LOG, recipientAddress);
		} catch (MailException e) {
			log.info(AppConstants.EMAIL_SENT_FAILURE_LOG, recipientAddress);
			log.info(AppConstants.EMAIL_SENT_FAILURE_REASON_LOG, e.getMessage());
		}
	}

}
