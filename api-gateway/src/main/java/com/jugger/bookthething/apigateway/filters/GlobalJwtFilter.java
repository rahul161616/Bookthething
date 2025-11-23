package com.jugger.bookthething.apigateway.filters;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import com.jugger.bookthething.apigateway.utils.JwtUtil;

@Component
public class GlobalJwtFilter implements GlobalFilter {

    @Autowired private JwtUtil jwtUtil;

    // Public paths that DO NOT require JWT
    private static final List<String> OPEN_API_ENDPOINTS = List.of(
        "/api/v1/auth/login",
        "/api/v1/auth/register",
        "/eureka",
        "/swagger",
        "/actuator"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
       String path = exchange.getRequest().getURI().getPath();
       System.out.println(">>> Incoming path: " + path);

          // 1️⃣ ALLOW EXACT PUBLIC ENDPOINTS
        if (OPEN_API_ENDPOINTS.contains(path)) {
            System.out.println(">>> Allowed public endpoint");
            return chain.filter(exchange);
        }
        // Check JWT
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
