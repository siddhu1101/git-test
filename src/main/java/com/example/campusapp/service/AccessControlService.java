package com.example.campusapp.service;

import com.example.campusapp.model.Role;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessControlService {

    public boolean canAccessCampus(Long campusId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        boolean isSuperAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Role.SUPER_ADMIN.name()));
        if (isSuperAdmin) return true;
        Claims claims = RequestContextClaimsHolder.getClaims();
        if (claims != null) {
            List<Integer> campusIds = claims.get("campusIds", List.class);
            if (campusIds != null) {
                return campusIds.stream().map(Long::valueOf).anyMatch(id -> id.equals(campusId));
            }
        }
        return false;
    }
}