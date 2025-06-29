package com.kitsune.kitsune.controller;

import com.kitsune.kitsune.dto.request.JwtRequest;
import com.kitsune.kitsune.dto.request.UserAuthenticationRequest;
import com.kitsune.kitsune.dto.request.UserCreationRequest;
import com.kitsune.kitsune.dto.request.UserUpdateRequest;
import com.kitsune.kitsune.entity.User;
import com.kitsune.kitsune.service.PasswordService;
import com.kitsune.kitsune.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserCreationRequest request){
        try{
            return userService.createUser(request);
        }catch(Exception ex){
            throw ex;
        }
    };

    @PostMapping("/auth")
    public ResponseEntity<Map<String,String>> userAuth(@RequestBody UserAuthenticationRequest request){
        try{
            String token = userService.userAuth(request);
            Map<String,String> res = Map.of("token", token);
            return ResponseEntity.ok(res);
        }catch(Exception ex){
            throw ex;
        }
    };

    @PostMapping("/verify")
    public ResponseEntity<User> tokenVerify(@RequestBody JwtRequest request){
        try{
            String userId = userService.verifyAuth(request.getToken());
            Optional<User> user = userService.getUsers(userId);
            if (user.isEmpty()){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token not valid");
            }else{
                return ResponseEntity.ok(user.get());
            }
        }catch(Exception ex){
            throw ex;
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsers(){
      List<User> users =  userService.getUsers();
      return ResponseEntity.ok(users);
    };

    @GetMapping("/{id}")
    public ResponseEntity<User> getUsers(@PathVariable String id){
        Optional<User> user = userService.getUsers(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateUserDetail(@PathVariable String id, @RequestBody UserUpdateRequest request){
        try{
            Optional<User> user = userService.updateUser(id,request);
            return ResponseEntity.ok("Update Successfully");
        }catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id){
        return userService.deleteUser(id);
    }
}
