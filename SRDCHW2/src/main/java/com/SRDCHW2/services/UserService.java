package com.SRDCHW2.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.SRDCHW2.models.User;
import com.SRDCHW2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public boolean validateUser(String username, String password) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public Page<User> getFilteredUsers(String field, String value, Pageable pageable) {
        return userRepository.findUsersByFieldAndValue(field, value, pageable);
    }

    public boolean validateAdmin(String username, String password) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        if (user.isPresent() && user.get().isAdmin()) {
            if (user.get().getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

}
