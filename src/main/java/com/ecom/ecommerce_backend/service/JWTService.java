package com.ecom.ecommerce_backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ecom.ecommerce_backend.model.LocalUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {
    @Value("${jwt.algorith.key}")
    private String algorithKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;
    private Algorithm algorithm;

    private static final String USERNAME_KEY = "USERNAME";
    private static final String EMAIL_KEY = "EMAIL";

    @PostConstruct
    public void postConstruct(){
        algorithm = Algorithm.HMAC256(algorithKey);
    }

    public String generateJWT(LocalUser user){
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateVerificationJWT(LocalUser user){
        return JWT.create()
                .withClaim(EMAIL_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getUsername(String token){
        return JWT.decode(token).getClaim(USERNAME_KEY).asString();
    }
}
