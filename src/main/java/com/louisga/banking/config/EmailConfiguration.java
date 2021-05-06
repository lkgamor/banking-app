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
@ConfigurationProperties("spring.mail")
public class EmailConfiguration {
	private String sender;
}
