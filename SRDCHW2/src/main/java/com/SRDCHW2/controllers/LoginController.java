package com.SRDCHW2.controllers;

import com.SRDCHW2.models.User;
import com.SRDCHW2.models.loginUser;
import com.SRDCHW2.repository.UserRepository;
import com.SRDCHW2.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    public static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @PostMapping
    public ResponseEntity<?> login(@RequestBody loginUser user) {
//        if(userService.validateAdmin(user.getUsername(), user.getPassword())){
//            User usr = userRepository.findByUsername(user.getUsername());
//            String token = Jwts.builder()
//                    .setSubject(usr.getUsername())
//                    .claim("name", usr.getName())
//                    .claim("surname", usr.getSurname())
//                    .claim("email", usr.getEmail())
//                    .claim("address", usr.getAddress())
//                    .claim("gender", usr.getGender())
//                    .claim("birthdate", usr.getBirthdate())
//                    .claim("admin", usr.isAdmin())
//                    .setIssuedAt(new Date(System.currentTimeMillis()))
//                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
//                    .signWith(SignatureAlgorithm.HS256, "secretKey")
//                    .compact();
//            return ResponseEntity.ok(token);
//        }
        if(userService.validateUser(user.getUsername(), user.getPassword())) {
            User usr = userRepository.findByUsername(user.getUsername());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedBirthdate = dateFormat.format(usr.getBirthdate());
            String token = Jwts.builder()
                    .setSubject(usr.getUsername())
                    .claim("username", usr.getUsername())
                    .claim("name", usr.getName())
                    .claim("surname", usr.getSurname())
                    .claim("email", usr.getEmail())
                    .claim("address", usr.getAddress())
                    .claim("gender", usr.getGender())
                    .claim("birthdate", formattedBirthdate)
                    .claim("admin", usr.isAdmin())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                    .signWith(secretKey)
                    .compact();
            return ResponseEntity.ok(token);
        }
        else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}


