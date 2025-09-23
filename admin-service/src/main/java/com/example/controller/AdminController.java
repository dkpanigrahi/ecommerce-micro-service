package com.example.controller;

import com.example.DTO.AdminLoginRequest;
import com.example.service.AdminAuthService;
import com.example.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminAuthService service;
    private final AdminProductService productService;

    @GetMapping("/login")
    public ResponseEntity adminLogin(@RequestBody AdminLoginRequest request){

        return service.AdminLogin(request);
    }

    @PostMapping("/product/bulk-upload")
    public ResponseEntity<Map<String, Object>> uploadFile(MultipartFile file){
        return productService.sendFileToProductService(file);
    }
}
