package com.example.controller;

import com.example.service.ProductService;
import com.example.service.PublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import security.AuthenticatedRequestContext;
import security.AuthenticatedRequestInterceptor;
import security.RequestIntercepter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductService productService;
    private final PublicService publicService;

    private Boolean hasAccess(){
        AuthenticatedRequestContext context = AuthenticatedRequestInterceptor.getContext();
        if(context.getRole().equals("ADMIN")){
            return true;
        }else {
            return false;
        }
    }

//    @GetMapping("/admin")
//    @RequestIntercepter
//    public String adminAccessMethod(){
//        AuthenticatedRequestContext context = AuthenticatedRequestInterceptor.getContext();
//        if(context.getRole().equals("ADMIN")){
//            return "Admin Access Success";
//        }
//        return "Your Are Not Permission To access this API";
//    }

    @PostMapping("/bulk-upload")
    @RequestIntercepter
    public Map<String, Object> uploadProductExcel(@RequestParam("file") MultipartFile file) {
        if (hasAccess()) {
            if (file.isEmpty()) {
                Map<String, Object> errorRes = new HashMap<>();
                errorRes.put("status", false);
                errorRes.put("message", "File is empty");
                return errorRes;
            }
            return productService.processExcelFile(file);
        } else {
            Map<String, Object> errorRes = new HashMap<>();
            errorRes.put("status", false);
            errorRes.put("message", "Only admin access");
            return errorRes;
        }
    }

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> getProductsForUserOrPublic(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean wholesaleOnly,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tagList,
            @RequestParam(defaultValue = "10") Integer itemPerPage,
            @RequestParam(defaultValue = "1") Integer pageNumber
    ) {
        return productService.getProductsForUserOrPublic(category, wholesaleOnly, search, tagList,itemPerPage, pageNumber);
    }

    // Endpoint for admin
    @GetMapping("/admin")
    @RequestIntercepter
    public ResponseEntity<Map<String, Object>> getProductsForAdmin(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean wholesaleOnly,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tagList,
            @RequestParam(defaultValue = "10") Integer itemPerPage,
            @RequestParam(defaultValue = "1") Integer pageNumber
    ) {
        if (hasAccess()) {
             return productService.getProductsForAdmin(category, wholesaleOnly, search, tagList,itemPerPage, pageNumber);
        } else {
            Map<String, Object> errorRes = new HashMap<>();
            errorRes.put("status", false);
            errorRes.put("message", "Only admin access");
            return ResponseEntity.ok(errorRes);
        }

    }


    @GetMapping("/public/category-list")
    public ResponseEntity getAllCategory() {
        return publicService.allCategoryList();
    }

    @GetMapping("/public/tag-list")
    public ResponseEntity getAllTag() {
        return publicService.allTagList();
    }


}
