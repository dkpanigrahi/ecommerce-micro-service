package com.example.service;

import com.example.external.ProductServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final ProductServiceClient productServiceClient;

    public ResponseEntity<Map<String, Object>> sendFileToProductService(MultipartFile file) {
        try {
            return ResponseEntity.ok(productServiceClient.uploadProductExcel(file));
        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("status", false);
            errorMap.put("message", "Failed to upload file to Product Service: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorMap);
        }
    }

}
