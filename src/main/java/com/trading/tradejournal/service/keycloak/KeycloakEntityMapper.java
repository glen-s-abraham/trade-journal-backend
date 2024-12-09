package com.trading.tradejournal.service.keycloak;

import org.keycloak.representations.idm.UserRepresentation;

import com.trading.tradejournal.dto.keycloak.UserModificationDto;

public class KeycloakEntityMapper {
    public static UserRepresentation toUserRepresentation(UserModificationDto userDetails) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDetails.username());
        user.setEmail(userDetails.email());
        user.setFirstName(userDetails.firstName());
        user.setLastName(userDetails.lastName());
        return user;
    }
}
