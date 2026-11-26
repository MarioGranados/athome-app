package com.athome.application.services;

import com.athome.application.model.User;
import com.athome.application.repo.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
//gets info from jwt for active user from front end
public class UserService {

    private final UserRepository userRepository;

    public User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}