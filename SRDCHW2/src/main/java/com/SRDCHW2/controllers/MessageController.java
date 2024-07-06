package com.SRDCHW2.controllers;

import com.SRDCHW2.services.MessageService;
import com.SRDCHW2.util.TokenUtil;
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
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageService messageService;

    private static final SecretKey secretKey = LoginController.secretKey;

    @GetMapping("/inbox/{username}")
    public ResponseEntity<Page<Message>> getInbox(@PathVariable String username, @RequestParam("page") int page, @RequestParam("size") int size,@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageService.getInbox(username, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/outbox/{username}")
    public ResponseEntity<Page<Message>> getOutbox(@PathVariable String username, @RequestParam("page") int page, @RequestParam("size") int size,@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageService.getOutbox(username, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Message>> filterMessages(
            @RequestParam String field,
            @RequestParam String value,
            Pageable pageable,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Page<Message> messages = messageService.getFilteredMessages(field, value, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/filter/inbox")
    public ResponseEntity<Page<Message>> filterInboxMessages(
            @RequestParam String username,
            @RequestParam String field,
            @RequestParam String value,
            Pageable pageable,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Page<Message> messages = messageService.getFilteredInboxMessages(username, field, value, pageable);
        return ResponseEntity.ok(messages);
    }


    @PostMapping("/send-msg")
    public ResponseEntity<Message> sendMessage(@RequestBody Message message,@RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            if (!isTokenValid(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Optional<User> user = Optional.ofNullable(userRepository.findByUsername(message.getReceiver()));
            if (user.isPresent()) {
                Message sentMessage = messageRepository.save(new Message(message.getBody(),message.getReceiver(),
                        message.getSender(), message.getTitle()));
                return new ResponseEntity<>(sentMessage, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
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

}
