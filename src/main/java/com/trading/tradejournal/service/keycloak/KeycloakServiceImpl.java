package com.trading.tradejournal.service.keycloak;

import java.util.Collections;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.trading.tradejournal.config.KeycloakProperties;
import com.trading.tradejournal.dto.keycloak.UserModificationDto;
import com.trading.tradejournal.exception.keycloak.KeycloakServiceException;

import jakarta.ws.rs.core.Response;

@Service
public class KeycloakServiceImpl implements KeycloakService{
    private static final Logger logger = LoggerFactory.getLogger(KeycloakServiceImpl.class);

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakServiceImpl(Keycloak keycloak, KeycloakProperties keycloakProperties) {
        this.keycloak = keycloak;
        this.realm = keycloakProperties.getRealm();
    }

    public String createUser(UserModificationDto userDetails) throws RuntimeException {
        try {
            UserRepresentation user = KeycloakEntityMapper.toUserRepresentation(userDetails);
            user.setEnabled(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(userDetails.password());
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));

            Response res = keycloak.realm(realm).users().create(user);
            if (res.getStatus() == Response.Status.CREATED.getStatusCode()) {
                logger.info("User successfully created in Keycloak: {}", userDetails.username());
                return "User created successfully";
            } else {
                String errorMessage = String.format("Error creating user: status=%d, response=%s", res.getStatus(),
                        res.readEntity(String.class));
                logger.error(errorMessage);
                throw new KeycloakServiceException("Failed to create User. " + errorMessage);
            }
        } catch (Exception e) {
            logger.error("Unexpected error while creating user:", e);
            throw new KeycloakServiceException("Failed to create User. ", e);
        }

    }
}
