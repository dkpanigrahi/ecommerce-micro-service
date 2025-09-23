package com.example.controller;

import DTO.ResponseEntityObject;
import DTO.UserDTO;
import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.AuthenticatedRequestContext;
import security.AuthenticatedRequestInterceptor;
import security.RequestIntercepter;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/create-user")
    public ResponseEntityObject createUser(@RequestParam("phoneNumber") String phoneNumber){
        return service.createUser(phoneNumber);
    }

    @GetMapping("/exist-phoneno")
    public Boolean existsByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber){
        return service.existsByPhoneNumber(phoneNumber);
    }

    @GetMapping("/find-by-phoneno")
    public UserDTO findByPhoneNumber(@RequestParam("phoneNumber") String phoneNumber) {

            return service.findByPhoneNumber(phoneNumber);
    }

    @PostMapping("/mark-verified")
    public Boolean markUserAsVerified(@RequestParam("phoneNumber") String phoneNumber){
        return service.markUserAsVerified(phoneNumber);
    }

    @GetMapping("/transaction")
    @RequestIntercepter
    public ResponseEntity<?> checkExpensePolicy(
            @RequestParam(value = "id", required = true) String id) {
        AuthenticatedRequestContext context = AuthenticatedRequestInterceptor.getContext();
       System.out.println(context.toString());
       return ResponseEntity.ok("Success");
    }
}
