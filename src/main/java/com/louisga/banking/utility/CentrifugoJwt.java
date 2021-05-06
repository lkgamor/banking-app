package com.louisga.banking.utility;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.louisga.banking.config.CentrifugoConfiguration;
import com.louisga.banking.config.CompanyConfiguration;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

/**
 * 
 * @author LouisGa
 *
 */

@Component
@RequiredArgsConstructor
public class CentrifugoJwt {

	private final CentrifugoConfiguration centrifugo;
	private final CompanyConfiguration companyConfig;
	
    public String createJWT() {
    	
    	final String CENTRIFUGO_SECRET = centrifugo.getSecret(); 
    	
        //The JWT signature algorithm to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //Get Current Day/Time to set as IssuedAt
        long nowMillis = System.currentTimeMillis();
        Date currentDate = new Date(nowMillis);

        //Get 1 month from the current Day/Time to set as Expiration
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, + 1);
        Date futureDate = cal.getTime();

        //Sign Token Using Custom Secret
        Key signingKey = new SecretKeySpec(CENTRIFUGO_SECRET.getBytes(), signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuer(companyConfig.getName())
                .setAudience("Centrifugo")
                .setIssuedAt(currentDate)
                .setExpiration(futureDate)
                .setSubject("Service Request")
                .signWith(signatureAlgorithm, signingKey);

        //Builds JWT and serializes to URL-safe string
        return builder.compact();
    }

}
