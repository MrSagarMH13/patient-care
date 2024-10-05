package com.makeen.patientcare.service.impl;

import com.makeen.patientcare.service.ITokenService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements ITokenService {
    @Value("${aidbox.base-url}")
    private String baseUrl;

    @Value("${aidbox.auth.endpoint}")
    private String authEndpoint;

    @Value("${aidbox.auth.clientId}")
    private String clientId;

    @Value("${aidbox.auth.clientSecret}")
    private String clientSecret;

    private final RestTemplate restTemplate;


    public String generateToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + authEndpoint, request, String.class);
        String tokenResponse = responseEntity.getBody();
        JSONObject jsonObject = new JSONObject(tokenResponse);
        return jsonObject.getString("access_token");
    }
}
