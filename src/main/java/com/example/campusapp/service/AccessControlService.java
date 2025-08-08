package com.example.campusapp.service;

import com.example.campusapp.model.Role;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    public boolean canAccessCampus(Long campusId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        boolean isSuperAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Role.SUPER_ADMIN.name()));
        if (isSuperAdmin) return true;
        Object claimsObj = RequestContextClaimsHolder.getClaims();
        if (claimsObj instanceof Claims claims) {
            Long userCampusId = claims.get("campusId", Long.class);
            return userCampusId != null && userCampusId.equals(campusId);
        }
        return false;
    }
}