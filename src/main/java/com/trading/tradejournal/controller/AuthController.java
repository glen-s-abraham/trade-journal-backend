package com.trading.tradejournal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.tradejournal.dto.auth.LoginDto;
import com.trading.tradejournal.service.auth.AuthService;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto credentials) {
        try {
            String result = authService.login(credentials.username(), credentials.password());
            return ResponseEntity.ok(result); // Return the result with HTTP 200 OK
        } catch (Exception e) {
            // Handle errors gracefully
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestBody String refresToken) {
        try {
            if (authHeader.length() == 0) {
                ResponseEntity.badRequest();
            }
            String accessToken = authHeader.replace("Bearer ", "");
            authService.logout(accessToken,refresToken);
            return ResponseEntity.ok("Success"); // Return the result with HTTP 200 OK
        } catch (Exception e) {
            // Handle errors gracefully
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

}