package com.example.service;

import DTO.ResponseEntityObject;
import com.example.DTO.ProductRequestDTO;
import com.example.entity.Category;
import com.example.entity.Product;
import com.example.entity.ProductTagMapping;
import com.example.entity.Tag;
import com.example.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductTagMappingRepository productTagMappingRepository;
    private final WholeSalePriceJpaRepository wholeSalePriceJpaRepository;
    private final TagJpaRepository tagJpaRepository;
    private final ProductCriteriaRepository productCriteriaRepository;

    public ResponseEntityObject createProduct(ProductRequestDTO requestDTO) {
        try{
            Category category = categoryRepository.findById(requestDTO.getCategoryId()).orElse(null);
            if(category == null){
                return new ResponseEntityObject(false,"Category Not Found",null,null);
            }
            Product product = new Product();
            product.setProductCode(UUID.randomUUID().toString());
            product.setName(requestDTO.getName());
            product.setPrice(requestDTO.getPrice());
            product.setDescription(requestDTO.getDescription());
            product.setStockQuantity(requestDTO.getStockQuantity());
            product.setCategory(category);
            product.setBrand(requestDTO.getBrand());
            product.setImageUrl(requestDTO.getImageUrl());
            product.setWholesaleAvailable(Boolean.TRUE.equals(requestDTO.getWholesaleAvailable()));
            product.setIsAvailable(true);

            if (Boolean.TRUE.equals(requestDTO.getWholesaleAvailable())) {
                product.setMinWholesaleQuantity(requestDTO.getMinWholesaleQuantity());
                product.setWholesalePricePerUnit(requestDTO.getWholesalePrice());

            }
            Product savedProduct = productRepository.save(product);

            return new ResponseEntityObject(true,"Product Created Successfully",null,null);

        } catch (Exception e) {
            e.printStackTrace();;
            return new ResponseEntityObject(false,"Internal Server Error",null,null);
        }
    }


    public Map<String, Object> processExcelFile(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        File tempFile = null;

        try (Workbook inputWorkbook = new XSSFWorkbook(file.getInputStream());
             SXSSFWorkbook workbook = new SXSSFWorkbook()) {

            Sheet inputSheet = inputWorkbook.getSheetAt(0);
            Row inputHeader = inputSheet.getRow(0);

            if (inputHeader == null) {
                response.put("status", false);
                response.put("error", "Excel file has no header row.");
                return response;
            }

            //Required Headers
            List<String> requiredHeaders = Arrays.asList(
                    "productcode", "name", "price", "category", "stockquantity", "brand", "tags"
            );

            // Build header map (lowercase for case-insensitivity)
            Map<String, Integer> headerMap = new HashMap<>();
            for (int c = 0; c < inputHeader.getLastCellNum(); c++) {
                Cell cell = inputHeader.getCell(c);
                String headerName = (cell != null ? cell.getStringCellValue().trim().toLowerCase() : "");
                if (!headerName.isEmpty()) {
                    headerMap.put(headerName, c);
                }
            }

            //Validate all required headers exist
            for (String required : requiredHeaders) {
                if (!headerMap.containsKey(required)) {
                    response.put("status", false);
                    response.put("error", "Missing required header: " + required);
                    return response;
                }
            }

            // If all headers are present, continue processing...
            Sheet outputSheet = workbook.createSheet("Product Add File");
            Row outputHeader = outputSheet.createRow(0);

            int errorColIndex = inputHeader.getLastCellNum();
            for (int c = 0; c < inputHeader.getLastCellNum(); c++) {
                Cell cell = inputHeader.getCell(c);
                outputHeader.createCell(c).setCellValue(cell != null ? cell.getStringCellValue() : "");
            }
            outputHeader.createCell(errorColIndex).setCellValue("Status");

            // Process rows
            for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
                Row inputRow = inputSheet.getRow(i);
                if (inputRow == null) continue;

                Row outputRow = outputSheet.createRow(i);

                for (int j = 0; j < errorColIndex; j++) {
                    Cell inCell = inputRow.getCell(j);
                    Cell outCell = outputRow.createCell(j);
                    if (inCell != null) outCell.setCellValue(getCellValue(inputRow, j));
                }

                String status;
                try {
                    status = processRow(inputRow, headerMap);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    status = "ERROR: " + ex.getMessage();
                }

                outputRow.createCell(errorColIndex).setCellValue(status);
            }

            // Save temp file (same as earlier)
            tempFile = File.createTempFile("product_upload_", ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
            }

            // Replace with Firebase/S3 logic
            String downloadUrl = "UPLOAD_URL_HERE";
            response.put("status", true);
            response.put("downloadUrl", downloadUrl);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", false);
            response.put("downloadUrl", null);
            response.put("error", e.getMessage());
            return response;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }


    private String getCellValue(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("dd/MM/yyyy").format(cell.getDateCellValue());
                } else {
                    return new BigDecimal(cell.getNumericCellValue()).toPlainString();
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanCellValue());
            default:
                return cell.toString().trim();
        }
    }

    @Transactional
    public String processRow(Row row, Map<String, Integer> headerMap) {
        try {
            String productCode = getCellValue(row, headerMap.get("productcode"));
            String name = getCellValue(row, headerMap.get("name"));
            String priceStr = getCellValue(row, headerMap.get("price"));
            String categoryName = getCellValue(row, headerMap.get("category"));
            String stockQtyStr = getCellValue(row, headerMap.get("stockquantity"));
            String brand = getCellValue(row, headerMap.get("brand"));
            String tagsCell = getCellValue(row, headerMap.get("tags"));

            if (productCode.isEmpty()) return "ERROR: Product code is empty";
            if (name.isEmpty()) return "ERROR: Product name is empty";
            if (priceStr.isEmpty()) return "ERROR: Price is empty";
            if (stockQtyStr.isEmpty()) return "ERROR: Stock quantity is empty";

            BigDecimal price;
            int stockQty;
            try {
                price = new BigDecimal(priceStr);
                stockQty = Integer.parseInt(stockQtyStr);
            } catch (NumberFormatException e) {
                return "ERROR: Invalid price or stock quantity format";
            }

            Category category = categoryRepository.findByNameIgnoreCase(categoryName.trim()).orElse(null);
            if (category == null) return "ERROR: Category Not Found";

            Product product = productRepository.findByProductCode(productCode)
                    .orElseGet(() -> {
                        Product p = new Product();
                        p.setProductCode(productCode);
                        p.setName(name);
                        p.setPrice(price);
                        p.setStockQuantity(stockQty);
                        p.setBrand(brand);
                        p.setCategory(category);
                        return productRepository.save(p);
                    });

            if (!tagsCell.isEmpty()) {
                String[] tagNames = tagsCell.split(",");
                for (String tagName : tagNames) {
                    String trimmedTag = tagName.trim();
                    if (trimmedTag.isEmpty()) continue;

                    Tag tag = tagJpaRepository.findByNameIgnoreCase(trimmedTag)
                            .orElseGet(() -> {
                                Tag t = new Tag();
                                t.setName(trimmedTag);
                                return tagJpaRepository.save(t);
                            });

                    boolean tagMappingExists = productTagMappingRepository.existsByProductAndTag(product, tag);
                    if (!tagMappingExists) {
                        ProductTagMapping mapping = new ProductTagMapping();
                        mapping.setProduct(product);
                        mapping.setTag(tag);
                        productTagMappingRepository.save(mapping);
                    }
                }
            }

            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Unexpected error - " + e.getMessage();
        }
    }


    public ResponseEntity<Map<String, Object>> getProductsForUserOrPublic(String category, Boolean wholesaleOnly, String search, List<String> tagList,Integer itemPerPage, Integer pageNumber) {
        try{
            Map<String, Object> response = productCriteriaRepository.getProductsForUser(category, wholesaleOnly, search, tagList, itemPerPage, pageNumber);
            response.put("status",true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorRes = new HashMap<>();
            errorRes.put("status", false);
            errorRes.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorRes);
        }
    }

    public Map<String, Object> getProductsForAdmin(
            String category, Boolean wholesaleOnly, String search,
            List<String> tagList, Integer itemPerPage, Integer pageNumber) {
        return productCriteriaRepository.getProductsForAdmin(
                category, wholesaleOnly, search, tagList, itemPerPage, pageNumber);
    }




}
