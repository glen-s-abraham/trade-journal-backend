package com.trading.tradejournal.service.auth;

import java.util.HashMap;
import java.util.Map;

import org.jboss.resteasy.spi.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.trading.tradejournal.config.KeycloakProperties;

@Service
public class AuthServiceImpl implements AuthService {

    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;

    @Autowired
    public AuthServiceImpl(KeycloakProperties keycloakProperties, RestTemplate restTemplate) {
        this.keycloakProperties = keycloakProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public String login(String username, String password) {
        String url = keycloakProperties.getAuthServerUrl() + "/realms/" + keycloakProperties.getRealm()
                + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakProperties.getResource());
        body.add("client_secret", keycloakProperties.getCredentialsSecret());
        body.add("username", username);
        body.add("password", password);
        body.add("scope", "openid");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        try {
            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("client_id", keycloakProperties.getResource());
            requestParams.add("client_secret", keycloakProperties.getCredentialsSecret());
            requestParams.add("refresh_token", refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);

            String logoutUrl = keycloakProperties.getAuthServerUrl() + "/realms/" + keycloakProperties.getRealm()
                    + "/protocol/openid-connect/logout";

            restTemplate.postForEntity(logoutUrl, request, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Error revoking access token");
        }
    }
}