package com.trading.tradejournal.service.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.tradejournal.config.KeycloakProperties;
import com.trading.tradejournal.exception.auth.AuthServiceException;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final KeycloakProperties keycloakProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AuthServiceImpl(KeycloakProperties keycloakProperties, RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.keycloakProperties = keycloakProperties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String login(String username, String password) throws AuthServiceException {
        try {
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

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Login failed with status: {}", response.getStatusCode());
                throw new AuthServiceException("Login failed for user: " + username);
            }

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error logging in user: {}", username, e);
            throw new AuthServiceException("Error logging in user: " + username, e);
        }
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        try {
            MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
            requestParams.add("client_id", keycloakProperties.getResource());
            requestParams.add("client_secret", keycloakProperties.getCredentialsSecret());
            requestParams.add("refresh_token", refreshToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams,
                    createHeaders(accessToken));

            String logoutUrl = keycloakProperties.getAuthServerUrl() + "/realms/" + keycloakProperties.getRealm()
                    + "/protocol/openid-connect/logout";

            ResponseEntity<Object> response = restTemplate.postForEntity(logoutUrl, request, Object.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.warn("Logout failed with status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error revoking token", e);
            throw new AuthServiceException("Error revoking access token", e);
        }
    }

    public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            throw new AuthServiceException("No authenticated user found!");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("sub");
        if (userId == null) {
            throw new AuthServiceException("User ID (sub) not found in token");
        }
        return userId;
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
