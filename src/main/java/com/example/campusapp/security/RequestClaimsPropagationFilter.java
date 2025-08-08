package com.example.campusapp.security;

import com.example.campusapp.service.RequestContextClaimsHolder;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestClaimsPropagationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Object claimsObj = request.getAttribute("jwtClaims");
        try {
            if (claimsObj instanceof Claims claims) {
                RequestContextClaimsHolder.setClaims(claims);
            }
            filterChain.doFilter(request, response);
        } finally {
            RequestContextClaimsHolder.clear();
        }
    }
}