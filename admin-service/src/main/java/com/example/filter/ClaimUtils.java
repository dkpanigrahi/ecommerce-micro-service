package com.example.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class ClaimUtils {

    public static AdminClaimData getClaim() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken auth) {
            Object principal = auth.getPrincipal();
            if (principal instanceof AdminClaimData claimData) {
                return claimData;
            }
        }
        return null;
    }
}