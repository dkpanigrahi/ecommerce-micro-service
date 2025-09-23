package com.example.service;

import DTO.ResponseEntityObject;
import com.example.repository.CategoryRepository;
import com.example.repository.TagJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicService {

    private final TagJpaRepository tagJpaRepository;
    private final CategoryRepository categoryRepository;

    public ResponseEntity allTagList(){
        try{
            List<String> tagList = tagJpaRepository.findAll().stream().map(list -> list.getName()).toList();
            return ResponseEntity.ok(new ResponseEntityObject(true,"Success",tagList,null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ResponseEntityObject(false,"Internal Server Error",null,null));
        }
    }

    public ResponseEntity allCategoryList(){
        try{
            List<String> categoryList = categoryRepository.findAll().stream().map(list -> list.getName()).toList();
            return ResponseEntity.ok(new ResponseEntityObject(true,"Success",categoryList,null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ResponseEntityObject(false,"Internal Server Error",null,null));
        }
    }
}
