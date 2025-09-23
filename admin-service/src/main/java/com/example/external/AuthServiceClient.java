package com.example.external;

import DTO.UserDTO;
import config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "AUTH-SERVICE")
public interface AuthServiceClient {

    @PostMapping("/api/auth/generate-token")
    String getToken(@RequestBody UserDTO userDTO);
}
