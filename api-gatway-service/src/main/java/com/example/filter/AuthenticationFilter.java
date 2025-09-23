package com.example.filter;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Autowired
    private TokenValidator tokenValidator;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // Allow public endpoints
            if (path.contains("/auth/login") ||
                    path.contains("/auth/verify") ||
                    path.contains("/auth/validate") ||
                    path.contains("/api/product/public") ||
                    path.contains("/api/product/public/category-list") ||
                    path.contains("/api/product/public/tag-list")) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            try {

                Claims claims = tokenValidator.validateTokenAndGetClaims(token);
                if (claims == null) {
                    return unauthorized(exchange, "Token validation failed");
                }

                ServerHttpRequest enrichedRequest = request.mutate()
                        .header("userUuid", claims.get("userUuid",String.class))
                        .header("phoneNumber", claims.get("phoneNumber", String.class))
                        .header("role", claims.get("role", String.class))
                        .header("isVerified", String.valueOf(claims.get("isVerified", Boolean.class)))
                        .build();

                return chain.filter(exchange.mutate().request(enrichedRequest).build());

            } catch (Exception e) {
                e.printStackTrace();
                return unauthorized(exchange, "Token validation failed or auth service unreachable");
            }
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String body = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    public static class Config {}
}

