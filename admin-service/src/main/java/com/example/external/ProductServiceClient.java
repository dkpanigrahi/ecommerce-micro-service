package com.example.external;

import DTO.UserDTO;
import com.example.config.MultipartSupportConfig;
import config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Component
@FeignClient(name = "PRODUCT-SERVICE", configuration = MultipartSupportConfig.class)
public interface ProductServiceClient {


    @PostMapping(value = "/api/product/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> uploadProductExcel(@RequestPart("file") MultipartFile file);
}
