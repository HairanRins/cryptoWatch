package com.cryptowatch.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtConfig jwtConfig;

    public String generateToken(UUID userId, String email) {
        return JWT.create()
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getExpirationMs()))
            .sign(Algorithm.HMAC256(jwtConfig.getSecret()));
    }

    public DecodedJWT validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(jwtConfig.getSecret()))
                .build()
                .verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public String getUserIdFromToken(String token) {
        DecodedJWT jwt = validateToken(token);
        return jwt != null ? jwt.getSubject() : null;
    }
}
