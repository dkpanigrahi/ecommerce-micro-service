package com.example.filter;

public class AdminClaimData {
    private final String userUuid;
    private final String phoneNumber;
    private final String role;
    private final boolean isVerified;

    public AdminClaimData(String userUuid, String phoneNumber, String role, boolean isVerified) {
        this.userUuid = userUuid;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isVerified = isVerified;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public boolean isVerified() {
        return isVerified;
    }
}