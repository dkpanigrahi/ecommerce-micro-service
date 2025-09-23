package com.example.service;

import DTO.ResponseEntityObject;
import DTO.UserDTO;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.UserJpaRepository;
import constant.KeyConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userRepository;

    public ResponseEntityObject createUser(String phoneNumber) {
        try{
            if (userRepository.existsByPhoneNumber(phoneNumber)) {
                return new ResponseEntityObject(false,"User with phone number already exists",null,null);
            }

            User user = new User(phoneNumber);
            Role role = new Role();
            role.setId(KeyConstant.userRole);
            user.setRole(role);
            user.setUuid(UUID.randomUUID().toString());
            userRepository.save(user);

            return new ResponseEntityObject(true,"User created Successfully",null,null);

        } catch (Exception e) {
            return new ResponseEntityObject(false,"Internal Server Error",null,null);
        }
    }

    public UserDTO findByPhoneNumber(String phoneNumber) {
        try {
            return userRepository.findByPhoneNumber(phoneNumber)
                    .map(user -> {
                        UserDTO userDTO = new UserDTO();
                        userDTO.setUuid(user.getUuid());
                        userDTO.setPhoneNumber(user.getPhoneNumber());
                        userDTO.setRole(user.getRole().getName());
                        userDTO.setVerified(user.isVerified());
                        userDTO.setIsActive(user.getIsActive());
                        return userDTO;
                    })
                    .orElse(null); // or throw custom exception if preferred
        } catch (Exception ex) {
            // Log the error or rethrow custom exception
            ex.printStackTrace();
            return null;
        }
    }


    public Boolean existsByPhoneNumber(String phoneNumber) {
        try{
            return userRepository.existsByPhoneNumber(phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }


    public Boolean markUserAsVerified(String phoneNumber) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(true);
            updateUser(user);
            return true;
        }else{
            return false;
        }
    }
}
