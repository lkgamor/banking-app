package com.louisga.banking.utility;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.louisga.banking.config.CentrifugoConfiguration;
import com.louisga.banking.model.AccountResponse;
import com.louisga.banking.model.CentrifugalDto;
import com.louisga.banking.model.TransactionResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CentrifugoPublisher {

    private final RestTemplate restTemplate;
	private final CentrifugoConfiguration centrifugo;

    public ResponseEntity<CentrifugalDto<?>> sendTransactionMadeToCentrifugo(TransactionResponse data) {
        var params = new CentrifugalDto.Params<TransactionResponse>();
        params.setChannel(centrifugo.getChannelTransaction());
        params.setData(data);
        var payload = CentrifugalDto.<TransactionResponse>builder() //
                .method(centrifugo.getMethod()) //
                .params(params).build();
        
        return sendToApi("apikey", centrifugo.getApiKey(), centrifugo.getUrl(), payload);
    }

    public ResponseEntity<CentrifugalDto<?>> sendAccountCreatedToCentrifugo(AccountResponse data) {
        var params = new CentrifugalDto.Params<AccountResponse>();
        params.setChannel(centrifugo.getChannelAccount());
        params.setData(data);
        var payload = CentrifugalDto.<AccountResponse>builder() //
                .method(centrifugo.getMethod()) //
                .params(params).build();
        
        return sendToApi("apikey", centrifugo.getApiKey(), centrifugo.getUrl(), payload);
    }
    
    private <T> ResponseEntity<T> sendToApi(String tokenScheme, String token, String url, T payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, tokenScheme + " " + token);
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<T> request = new HttpEntity<>(payload, headers);
        URI uri = UriComponentsBuilder.fromHttpUrl(url).path("/api").build().toUri();
        return restTemplate.exchange(uri, HttpMethod.POST, request, new ParameterizedTypeReference<T>() {});
    }
}
