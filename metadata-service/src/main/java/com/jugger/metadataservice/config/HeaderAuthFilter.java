package com.jugger.metadataservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Enforce minimal header presence:
 * - /api/v1/vendor/** requires X-User-Id header (authenticated user)
 * - /api/v1/admin/** requires X-User-Role: ADMIN header
 */
@Component
public class HeaderAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        if (path.startsWith("/api/v1/vendor")) {
            String userId = req.getHeader("X-User-Id");
            if (userId == null || userId.isBlank()) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().write("Missing X-User-Id header");
                return;
            }
        }

        if (path.startsWith("/api/v1/admin")) {
            String role = req.getHeader("X-User-Role");
            if (role == null || !role.equalsIgnoreCase("ADMIN")) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.getWriter().write("Admin role required (X-User-Role: ADMIN)");
                return;
            }
        }

        chain.doFilter(req, res);
    }
}
