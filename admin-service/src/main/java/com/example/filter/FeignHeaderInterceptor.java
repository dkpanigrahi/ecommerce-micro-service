package com.example.filter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        AdminClaimData claim = ClaimUtils.getClaim();
        if (claim != null) {
            template.header("userUuid", claim.getUserUuid());
            template.header("phoneNumber", claim.getPhoneNumber());
            template.header("role", claim.getRole());
            template.header("isVerified", String.valueOf(claim.isVerified()));
        }
    }
}