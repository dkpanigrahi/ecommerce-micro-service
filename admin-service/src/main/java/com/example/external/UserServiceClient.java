package com.example.external;

import DTO.UserDTO;
import config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "USER-SERVICE", configuration = FeignClientConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/users/find-by-phoneno")
    UserDTO findByEmail(@RequestParam("email") String email);

}
