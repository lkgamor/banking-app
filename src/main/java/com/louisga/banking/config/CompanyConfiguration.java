package com.louisga.banking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("company")
public class CompanyConfiguration {

	private String name;
	private String mainBranchName;
	private String mainBranchAddress;
	private String accountNamesPrefix;
	private String accountPasswordSuffix;
	private Double minimumAllowedAccountBalance;
	private Double initialAccountBalance;
	private Boolean sendEmails;
	private String adminContact;
}
