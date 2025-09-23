package com.example.repository;

import com.example.config.UserCredential;
import com.example.entity.Admin;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminJpaRepository extends JpaRepository<Admin,Long> {


    @Query("""
        SELECT a.email AS email, a.password AS password 
        FROM Admin a 
        WHERE a.email = :email
    """)
    UserCredential findByEmail(@Param("email") String email);

    Admin findByEmailAndPassword(String email, String password);

    @Query("SELECT a FROM Admin a WHERE LOWER(a.email) = LOWER(:email)")
    Admin findByEmailNew(@Param("email") String email);
}
