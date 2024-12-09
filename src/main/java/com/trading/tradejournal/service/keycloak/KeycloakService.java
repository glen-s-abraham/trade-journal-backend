package com.trading.tradejournal.service.keycloak;

import com.trading.tradejournal.dto.keycloak.UserModificationDto;

public interface KeycloakService {
    public String createUser(UserModificationDto userDetails);
}
