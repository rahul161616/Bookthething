package com.jugger.bookthething.apigateway.filters;

import java.util.List;
import java.util.UUID;

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

        // Extract username and userId from JWT
        String username = jwtUtil.getUsername(token);
        Long userId = jwtUtil.getUserId(token);
        
        System.out.println(">>> Extracted username: " + username);
        System.out.println(">>> Extracted userId: " + userId);
        
        // 🔥 Mutate request and inject both headers
        ServerWebExchange mutatedExchange = exchange.mutate()
        // .request(r -> r.headers(h -> {
        //     h.set("X-Username", username);
        //     if (userId != null) {
        //         // Convert Long userId to UUID format for booking service
        //         // Using a deterministic UUID based on the userId
        //         java.util.UUID userUuid = new java.util.UUID(0L, userId);
        //         String uuidString = userUuid.toString();
        //         h.set("X-User-Id", uuidString);
        //         System.out.println(">>> Set X-User-Id: " + uuidString + " (from userId: " + userId + ")");
        //     } else {
        //         System.out.println(">>> Warning: userId is null in JWT token, not setting X-User-Id header");
        //     }
        // }))
        .request(r-> r.headers(h->{
            h.set("X-Username",username);
            if(userId!=null){
                UUID userUuid=new UUID(0L,userId);
                h.set("X-User-Id",userUuid.toString());
                System.out.println(">>> Set X-User-Id: "+userUuid.toString()+" (from userId: "+userId+")");

            }
        }))
        .build();
        return chain.filter(mutatedExchange);
        // return chain.filter(mutatedExchange);
    }
}
