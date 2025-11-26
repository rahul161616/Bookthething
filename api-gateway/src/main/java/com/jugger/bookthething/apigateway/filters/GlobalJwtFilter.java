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

          // allow auth service public routes
        if (OPEN_API_ENDPOINTS.stream().anyMatch(path::startsWith)) {
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
            System.out.println(">>>Invalid Jwt Token");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        System.out.println(">>>Jwt Validated Successfully");

        // Extract username from JWT
        String username = jwtUtil.getUsername(token);
        
        // 🔥 Mutate request and inject X-Username header
        ServerWebExchange mutatedExchange = exchange.mutate()
        .request(r -> r.headers(h -> h.set("X-Username", username)))
        .build();
        
        return chain.filter(mutatedExchange);
    }
}
