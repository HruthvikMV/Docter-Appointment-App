package com.auth_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    // Must match the secret in api-gateway JwtAuthenticationFilter
    private static final String SECRET_KEY = "secret12345";

    public String generateToken(String username, String role) {
        Instant now = Instant.now();

        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(now.plus(1, ChronoUnit.HOURS))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String validateTokenAndRetriveSubject(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token)
                .getSubject();
    }
}


