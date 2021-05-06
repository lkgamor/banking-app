package com.louisga.banking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 
 * @author LouisGa
 *
 */

@Data
@Configuration
@ConfigurationProperties("centrifugo")
public class CentrifugoConfiguration {

	private String url;
	private String secret;
	private String method;
	private String apiKey;
	private String channelAccount;
	private String channelTransaction;
	private String websocket;
}
