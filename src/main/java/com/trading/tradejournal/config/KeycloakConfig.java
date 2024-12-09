package com.trading.tradejournal.config;

import org.keycloak.admin.client.Keycloak;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {
    @Bean
    public Keycloak keycloak(KeycloakProperties keycloakProperties) {

        return Keycloak.getInstance(
                keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getAdminRealm(),
                keycloakProperties.getAdminUsername(),
                keycloakProperties.getAdminPassword(),
                keycloakProperties.getAdminResource());
    }
}
