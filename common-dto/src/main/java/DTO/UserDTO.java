package DTO;

import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {

    private String uuid;
    private String name;
    private String phoneNumber;
    private String email;
    private String role;
    private Date lastLogin;
    private Boolean isActive;
    private boolean isVerified;
}
