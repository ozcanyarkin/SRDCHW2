package com.SRDCHW2.controllers;

import com.SRDCHW2.services.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import com.SRDCHW2.models.Message;
import com.SRDCHW2.models.User;
import com.SRDCHW2.repository.MessageRepository;
import com.SRDCHW2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserService userService;

    private static final SecretKey secretKey = LoginController.secretKey;

    @GetMapping("/get-all-users")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam("page") int page, @RequestParam("size") int size, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }


    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@RequestBody User user, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            if (!isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Optional<User> user1 = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));
            if (user1.isPresent()) {
                return new ResponseEntity<>(user1.get(), HttpStatus.CONFLICT);
            } else {
                User savedUser = userRepository.save(new User(user.getBirthdate(),user.getAddress(),user.getEmail(),
                        user.getGender(),user.isAdmin(),user.getName(),user.getPassword(),user.getSurname(),user.getUsername()));
                return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<User>> filterUsers(
            @RequestParam String field,
            @RequestParam String value,
            Pageable pageable,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Page<User> users = userService.getFilteredUsers(field, value, pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/update-user/{username}")
    public ResponseEntity<User> updateUser(@PathVariable("username") String username, @RequestBody User user,@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if(userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            userToUpdate.setBirthdate(user.getBirthdate());
            userToUpdate.setAddress(user.getAddress());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setGender(user.getGender());
            userToUpdate.setAdmin(user.isAdmin());
            userToUpdate.setName(user.getName());
            userToUpdate.setPassword(user.getPassword());
            userToUpdate.setSurname(user.getSurname());
            return new ResponseEntity<>(userRepository.save(userToUpdate), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete-by-username/{username}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable("username") String username,@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            if (!isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<Message> sentMessages = messageRepository.findBySender(username);
            for (Message message : sentMessages) {
                message.setSender(null);
                messageRepository.save(message);
            }

            List<Message> receivedMessages = messageRepository.findByReceiver(username);
            for (Message message : receivedMessages) {
                message.setReceiver(null);
                messageRepository.save(message);
            }
            userRepository.deleteByUsername(username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-users")
    public ResponseEntity<HttpStatus> deleteAllUsers(@RequestBody User requestingUser,@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            if (!isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if(requestingUser.isAdmin()){
                userRepository.deleteAll();
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @GetMapping("/get-user/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username,@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if(userOptional.isPresent()) {
            return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

