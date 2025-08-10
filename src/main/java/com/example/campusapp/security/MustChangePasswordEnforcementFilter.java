package com.example.campusapp.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MustChangePasswordEnforcementFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Object claimsObj = request.getAttribute("jwtClaims");
        String path = request.getRequestURI();
        if (claimsObj instanceof Claims claims) {
            Boolean mcp = claims.get("mcp", Boolean.class);
            if (Boolean.TRUE.equals(mcp)) {
                if (!(path.startsWith("/api/auth/change-password") || path.startsWith("/api/auth/login"))) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"PASSWORD_CHANGE_REQUIRED\"}");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}