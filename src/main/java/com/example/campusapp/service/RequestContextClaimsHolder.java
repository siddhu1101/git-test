package com.example.campusapp.service;

import io.jsonwebtoken.Claims;

public class RequestContextClaimsHolder {
    private static final ThreadLocal<Claims> CLAIMS = new ThreadLocal<>();

    public static void setClaims(Claims claims) { CLAIMS.set(claims); }
    public static Claims getClaims() { return CLAIMS.get(); }
    public static void clear() { CLAIMS.remove(); }
}