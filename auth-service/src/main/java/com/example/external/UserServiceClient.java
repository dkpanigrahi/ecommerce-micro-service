package com.example.external;

import DTO.ResponseEntityObject;
import DTO.UserDTO;
import config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "USER-SERVICE", configuration = FeignClientConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/users/exist-phoneno")
    Boolean existsByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber);

    @PostMapping("/api/users/create-user")
    ResponseEntityObject createUser(@RequestParam("phoneNumber") String phoneNumber);

    @GetMapping("/api/users/find-by-phoneno")
    UserDTO findByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber);

    @PostMapping("/api/users/mark-verified")
    Boolean markUserAsVerified(@RequestParam("phoneNumber") String phoneNumber);
}
