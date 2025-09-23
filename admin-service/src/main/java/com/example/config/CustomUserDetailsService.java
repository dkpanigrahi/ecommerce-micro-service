package com.example.config;

import com.example.repository.AdminJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminJpaRepository adminJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserCredential userCredential = adminJpaRepository.findByEmail(username);

        if (userCredential == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return new CustomUserDetails(userCredential);
    }
}
