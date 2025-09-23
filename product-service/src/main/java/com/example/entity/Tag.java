package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}
