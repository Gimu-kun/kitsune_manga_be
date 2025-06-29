package com.kitsune.kitsune.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.kitsune.kitsune.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {
    final String secret = "my-secret-key";

    public String getAuthJwt(Optional<User> user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            Date expireAt = new Date(System.currentTimeMillis() + 3600 * 24 * 1000);
            String token = JWT.create()
                    .withIssuer("kitsune")
                    .withSubject("user-auth")
                    .withClaim("role","user")
                    .withClaim("id", user.get().getId())
                    .withExpiresAt(expireAt)
                    .sign(algorithm);
            return token;
        }catch (JWTCreationException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public String verifyAuthJwt(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("kitsune")
                    .build();
            return String.valueOf(verifier.verify(token).getClaim("id").asString());
        }catch(TokenExpiredException ex){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token has expired");
        }
        catch(JWTCreationException ex ){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Token not valid");
        }
    }
}
