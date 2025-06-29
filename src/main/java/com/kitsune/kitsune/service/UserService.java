package com.kitsune.kitsune.service;

import com.kitsune.kitsune.dto.request.UserCreationRequest;
import com.kitsune.kitsune.dto.request.UserUpdateRequest;
import com.kitsune.kitsune.entity.User;
import com.kitsune.kitsune.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User createUser(UserCreationRequest request){
      User user = new User();
      user.setUsername(request.getUsername());
      user.setFirstName(request.getFirstName());
      user.setLastName(request.getLastName());
      user.setPassword(request.getPassword());
      user.setDob(request.getDob());
      System.out.println(user.toString());
      userRepository.save(user);
      return user;
    };

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id){
        return userRepository.findById(id);
    }

    public Optional<User> updateUser(String id, UserUpdateRequest request){
        return userRepository.findById(id).map(u -> {
            if (request.getFirstName() != null) {
                u.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                u.setLastName(request.getLastName());
            }
            if (request.getPassword() != null) {
                u.setPassword(request.getPassword());
            }
            if (request.getDob() != null) {
                u.setDob(request.getDob());
            }
            return userRepository.save(u);
        });
    }

    public void deleteUser(String id){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){
            userRepository.deleteById(id);
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
