package com.kitsune.kitsune.controller;

import com.kitsune.kitsune.dto.request.JwtRequest;
import com.kitsune.kitsune.dto.request.UserAuthenticationRequest;
import com.kitsune.kitsune.dto.request.UserCreationRequest;
import com.kitsune.kitsune.dto.request.UserUpdateRequest;
import com.kitsune.kitsune.entity.User;
import com.kitsune.kitsune.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserCreationRequest request){
        userService.createUser(request);
        return ResponseEntity.ok("Create user successed");
    };

    @PostMapping("/auth")
    public ResponseEntity<String> userAuth(@RequestBody UserAuthenticationRequest request){
        String token = userService.userAuth(request);
        return ResponseEntity.ok(token);
    };

    @PostMapping("/verify")
    public ResponseEntity<User> tokenVerify(@RequestBody JwtRequest request){
        String userId = userService.verifyAuth(request.getToken());
        if (userId != null){
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()){
                return ResponseEntity.ok(user.get());
            }else{
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token not valid");
            }
        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid or expired");
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUser(){
      List<User> users =  userService.getAllUser();
      return ResponseEntity.ok(users);
    };

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id){
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUserDetail(@PathVariable String id, @RequestBody UserUpdateRequest request){
        Optional<User> user = userService.updateUser(id,request);
        return ResponseEntity.ok("Update Successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id){
        userService.deleteUser(id);
        return ResponseEntity.ok("Delete Successfully");
    }
}
