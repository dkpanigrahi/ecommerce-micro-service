package com.example.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class TokenValidator {

    private final String secret = "W8YwRV5zzjHfMpmg3m5g8T6FZcN9qZxX7k6oU7r1M5R9vB4dP3zG6xJ2d8L4sT2fX3gV1aE9mC5bN7qH9sU3yT1wA5cR8eF6k";

    public Claims validateTokenAndGetClaims(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
