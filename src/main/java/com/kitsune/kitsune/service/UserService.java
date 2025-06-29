package com.kitsune.kitsune.service;

import com.kitsune.kitsune.dto.request.UserAuthenticationRequest;
import com.kitsune.kitsune.dto.request.UserCreationRequest;
import com.kitsune.kitsune.dto.request.UserUpdateRequest;
import com.kitsune.kitsune.entity.User;
import com.kitsune.kitsune.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwt;
    @Autowired
    private PasswordService passwordService;

    public ResponseEntity<String> createUser(UserCreationRequest request){
        if (isUsernameTaken(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Account was existed"); //status 409
        }
      try{
          User user = new User();
          user.setUsername(request.getUsername());
          user.setFirstName(request.getFirstName());
          user.setLastName(request.getLastName());
          user.setPassword(passwordService.encoderPassword(request.getPassword()));
          user.setDob(request.getDob());
          System.out.println(user.toString());
          userRepository.save(user);
          return ResponseEntity.ok("Create account successfully");
      }catch (Exception ex){
          throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unknown Error"); //status 500
      }
    };

    public boolean isUsernameTaken(String username){
        return userRepository.existsByUsername(username);
    };

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getUsers(String id){
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

    public ResponseEntity<Void> deleteUser(String id){
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public String userAuth(UserAuthenticationRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isPresent()) {
            User foundUser = user.get();
            if (!passwordService.checkPassword(foundUser.getPassword(), request.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Password incorrect");
            }
            return jwt.getAuthJwt(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found");
        }
    }

    public String verifyAuth(String token){
        try{
            return jwt.verifyAuthJwt(token);
        }catch(Exception ex){
            throw ex;
        }
    }
}
